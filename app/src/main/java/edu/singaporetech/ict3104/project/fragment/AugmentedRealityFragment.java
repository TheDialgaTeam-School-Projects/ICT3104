package edu.singaporetech.ict3104.project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import edu.singaporetech.ict3104.java_to_unity_proxy.PositionSensor;
import edu.singaporetech.ict3104.project.MainActivity;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.helpers.permission.CameraPermissionHelper;
import edu.singaporetech.ict3104.project.helpers.permission.LocationPermissionHelper;

public class AugmentedRealityFragment extends Fragment {

    private PositionSensor positionSensor;
    private FrameLayout frameLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        positionSensor = new PositionSensor(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_augmented_reality, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MainActivity mainActivity = (MainActivity) requireActivity();

        if (!CameraPermissionHelper.hasCameraPermission(mainActivity) || !LocationPermissionHelper.hasLocationPermission(mainActivity)) {
            Navigation.findNavController(requireActivity(), R.id.nav_view).navigate(R.id.action_augmentedRealityFragment_to_navigation_map);
        }

        frameLayout = view.findViewById(R.id.unityLayout);
        frameLayout.addView(mainActivity.getUnityPlayer().getView());
        mainActivity.getUnityPlayer().resume();
    }

    @Override
    public void onDestroyView() {
        frameLayout.removeAllViews();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        positionSensor.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        positionSensor.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        ((MainActivity) requireActivity()).getUnityPlayer().pause();
        super.onDestroy();
    }
}