package edu.singaporetech.ict3104.project;

import android.media.Image;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import edu.singaporetech.ict3104.project.permissionHelper.CameraPermissionHelper;

public class AugmentedRealityFragment extends Fragment implements GLSurfaceView.Renderer {

    private GLSurfaceView surfaceViewAr;

    private boolean installArCoreRequested;

    private Session session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_augmented_reality, container, false);

        surfaceViewAr = view.findViewById(R.id.surfaceViewAr);
        surfaceViewAr.setPreserveEGLContextOnPause(true);
        surfaceViewAr.setEGLContextClientVersion(3);
        surfaceViewAr.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfaceViewAr.setRenderer(this);
        surfaceViewAr.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        surfaceViewAr.setWillNotDraw(false);

        installArCoreRequested = false;

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            session.close();
            session = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(getActivity(), !installArCoreRequested)) {
                    case INSTALL_REQUESTED:
                        installArCoreRequested = true;
                        return;

                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
                    CameraPermissionHelper.requestCameraPermission(getActivity());
                    return;
                }

                // Create the session.
                session = new Session(getContext());
            } catch (UnavailableArcoreNotInstalledException | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                Log.e(AugmentedRealityFragment.class.getName(), "Exception creating session", exception);
                return;
            }
        }

        try {
            configureSession();
            session.resume();
        } catch (CameraNotAvailableException e) {
            Toast.makeText(getContext(), "Camera not available. Try restarting the app.", Toast.LENGTH_LONG).show();
            session = null;
            return;
        }

        surfaceViewAr.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            surfaceViewAr.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
            Toast.makeText(getContext(), "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(getActivity())) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(getActivity());
            }
            Navigation.findNavController(requireView()).navigate(R.id.action_agumentedRealityFragment_to_navigation_home);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_BLEND);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

        } catch (Throwable t) {
            Log.e(AugmentedRealityFragment.class.getName(), "Exception on the OpenGL thread", t);
        }
    }

    private void configureSession() {
        Config config = session.getConfig();
        config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP);

        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        } else {
            config.setDepthMode(Config.DepthMode.DISABLED);
        }

        session.configure(config);
    }
}