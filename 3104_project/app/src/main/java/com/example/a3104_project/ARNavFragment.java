package com.example.a3104_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.NavigationConstraint;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.navigation.RouteTracker;
import com.esri.arcgisruntime.symbology.MultilayerPolylineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.SolidStrokeSymbolLayer;
import com.esri.arcgisruntime.symbology.StrokeSymbolLayer;
import com.esri.arcgisruntime.symbology.SymbolLayer;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.toolkit.ar.ArLocationDataSource;
import com.esri.arcgisruntime.toolkit.ar.ArcGISArView;
import com.esri.arcgisruntime.toolkit.control.JoystickSeekBar;

import java.util.ArrayList;
import java.util.LinkedList;

public class ARNavFragment extends Fragment {

    private static final String TAG = "AR NAV";
    private ArcGISArView mArView;

    private TextView mHelpLabel;
    private View mCalibrationView;

    public static RouteResult sRouteResult;

    private ArcGISScene mScene;

    private boolean mIsCalibrating = false;
    private RouteTracker mRouteTracker;
    private TextToSpeech mTextToSpeech;
    private Button   calibrationButton,navigateButton;
    private float mCurrentVerticalOffset;
    private JoystickSeekBar headingJoystick,altitudeJoystick;

    public ARNavFragment() {
        // Required empty public constructor
    }
    public ARNavFragment(RouteResult r) {
        // Required empty public constructor
        sRouteResult = r;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_arnav, container, false);

        // license with a license key
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4554618479,none,5H80TK8ELBASJ9HSX026");
        mArView =rootView.findViewById(R.id.arView);
        // get references to the ui views defined in the layout
        mHelpLabel = rootView.findViewById(R.id.helpLabelTextView);
        mCalibrationView = rootView.findViewById(R.id.calibrationView);
        // show/hide calibration view
        calibrationButton = rootView.findViewById(R.id.calibrateButton);
        headingJoystick = rootView.findViewById(R.id.headingJoystick);
        altitudeJoystick = rootView.findViewById(R.id.altitudeJoystick);
        // start navigation
        navigateButton = rootView.findViewById(R.id.navigateStartButton);
        // Inflate the layout for this fragment
        return rootView;
        //return inflater.inflate(R.layout.fragment_arnav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestPermissions();
    }

    private void navigateInAr() {
        // get a reference to the ar view
        mArView.registerLifecycle(getLifecycle());
        // disable touch interactions with the scene view
        mArView.getSceneView().setOnTouchListener((view, motionEvent) -> true);
        // create a scene and add it to the scene view
        mScene = new ArcGISScene(Basemap.createImagery());
        mArView.getSceneView().setScene(mScene);
        // create and add an elevation surface to the scene
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(getString(R.string.elevation_url));
        Surface elevationSurface = new Surface();
        elevationSurface.getElevationSources().add(elevationSource);
        mArView.getSceneView().getScene().setBaseSurface(elevationSurface);
        // allow the user to navigate underneath the surface
        elevationSurface.setNavigationConstraint(NavigationConstraint.NONE);
        // hide the basemap. The image feed provides map context while navigating in AR
        elevationSurface.setOpacity(0f);
        // disable plane visualization. It is not useful for this AR scenario.
        mArView.getArSceneView().getPlaneRenderer().setEnabled(false);
        mArView.getArSceneView().getPlaneRenderer().setVisible(false);
        // add an ar location data source to update location
        mArView.setLocationDataSource(new ArLocationDataSource(getContext()));

        // create and add a graphics overlay for showing the route line
        GraphicsOverlay routeOverlay = new GraphicsOverlay();
        mArView.getSceneView().getGraphicsOverlays().add(routeOverlay);
        Graphic routeGraphic = new Graphic(sRouteResult.getRoutes().get(0).getRouteGeometry());
        routeOverlay.getGraphics().add(routeGraphic);
        // display the graphic 3 meters above the ground
        routeOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
        routeOverlay.getSceneProperties().setAltitudeOffset(3);
        // create a renderer for the route geometry
        SolidStrokeSymbolLayer strokeSymbolLayer = new SolidStrokeSymbolLayer(1, Color.YELLOW, new LinkedList<>(),
                StrokeSymbolLayer.LineStyle3D.TUBE);
        strokeSymbolLayer.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);
        ArrayList<SymbolLayer> layers = new ArrayList<>();
        layers.add(strokeSymbolLayer);
        MultilayerPolylineSymbol polylineSymbol = new MultilayerPolylineSymbol(layers);
        SimpleRenderer polylineRenderer = new SimpleRenderer(polylineSymbol);
        routeOverlay.setRenderer(polylineRenderer);

        // create and start a location data source for use with the route tracker
        AndroidLocationDataSource trackingLocationDataSource = new AndroidLocationDataSource(getContext());
        trackingLocationDataSource.addLocationChangedListener(locationChangedEvent -> {
            if (mRouteTracker != null) {
                // pass new location to the route tracker
                mRouteTracker.trackLocationAsync(locationChangedEvent.getLocation());
            }
        });
        trackingLocationDataSource.startAsync();


        calibrationButton.setOnClickListener(v -> {
            // toggle calibration
            mIsCalibrating = !mIsCalibrating;
            if (mIsCalibrating) {
                mScene.getBaseSurface().setOpacity(0.5f);
                mCalibrationView.setVisibility(View.VISIBLE);
            } else {
                mScene.getBaseSurface().setOpacity(0f);
                mCalibrationView.setVisibility(View.GONE);
            }
        });

        // start turn-by-turn when the user is ready
        navigateButton.setOnClickListener(v -> {
            // create a route tracker with the route result
            mRouteTracker = new RouteTracker(getContext(), sRouteResult, 0);
            // initialize text-to-speech to play navigation voice guidance
            mTextToSpeech = new TextToSpeech(getContext(), status -> {
                if (status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
                }
            });
            mRouteTracker.addNewVoiceGuidanceListener((RouteTracker.NewVoiceGuidanceEvent newVoiceGuidanceEvent) -> {
                // Get new guidance
                String newGuidanceText = newVoiceGuidanceEvent.getVoiceGuidance().getText();
                // Display and then read out the new guidance
                mHelpLabel.setText(newGuidanceText);
                // read out directions
                mTextToSpeech.stop();
                mTextToSpeech.speak(newGuidanceText, TextToSpeech.QUEUE_FLUSH, null);
            });
            mRouteTracker
                    .addTrackingStatusChangedListener((RouteTracker.TrackingStatusChangedEvent trackingStatusChangedEvent) -> {
                        // Display updated guidance
                        mHelpLabel.setText(mRouteTracker.generateVoiceGuidance().getText());
                    });
        });

        // listen for calibration value changes for heading
        headingJoystick.addDeltaProgressUpdatedListener(delta -> {
            // get the origin camera
            Camera camera = mArView.getOriginCamera();
            // add the heading delta to the existing camera heading
            double heading = camera.getHeading() + delta;
            // get a camera with a new heading
            Camera newCam = camera.rotateTo(heading, camera.getPitch(), camera.getRoll());
            // apply the new origin camera
            mArView.setOriginCamera(newCam);
        });

        // listen for calibration value changes for altitude
        altitudeJoystick.addDeltaProgressUpdatedListener(delta -> {
            mCurrentVerticalOffset += delta;
            // get the origin camera
            Camera camera = mArView.getOriginCamera();
            // elevate camera by the delta
            Camera newCam = camera.elevate(delta);
            // apply the new origin camera
            mArView.setOriginCamera(newCam);
        });
        // this step is handled on the back end anyways, but we're applying a vertical offset to every update as per the
        // calibration step above
        mArView.getLocationDataSource().addLocationChangedListener(locationChangedEvent -> {
            Point updatedLocation = locationChangedEvent.getLocation().getPosition();
            mArView.setOriginCamera(new Camera(
                    new Point(updatedLocation.getX(), updatedLocation.getY(), updatedLocation.getZ() + mCurrentVerticalOffset),
                    mArView.getOriginCamera().getHeading(), mArView.getOriginCamera().getPitch(),
                    mArView.getOriginCamera().getRoll()));
        });

        // remind the user to calibrate the heading and altitude before starting navigation
        Toast.makeText(getContext(), "Calibrate your heading and altitude before navigating!", Toast.LENGTH_LONG).show();
    }

    /**
     * Request read external storage for API level 23+.
     */
    private void requestPermissions() {
        // define permission to request
        String[] reqPermission = { Manifest.permission.CAMERA };
        int requestCode = 2;
        if (ContextCompat.checkSelfPermission(getContext(), reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            navigateInAr();
        } else {
            // request permission
            ActivityCompat.requestPermissions(getActivity(), reqPermission, requestCode);
        }
    }

    /**
     * Handle the permissions request response.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            navigateInAr();
        } else {
            // report to user that permission was denied
            Toast.makeText(getContext(), getString(R.string.navigate_ar_permission_denied), Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPause() {
        if (mArView != null) {
            mArView.stopTracking();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mArView != null) {
            mArView.startTracking(ArcGISArView.ARLocationTrackingMode.CONTINUOUS);
        }
    }
}
