package edu.singaporetech.ict3104.project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.singaporetech.ict3104.project.MainActivity;
import edu.singaporetech.ict3104.project.R;

public class AugmentedRealityFragment extends Fragment {

    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_augmented_reality, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frameLayout = view.findViewById(R.id.unityLayout);
        frameLayout.addView(((MainActivity) requireActivity()).getUnityPlayer().getView());
        ((MainActivity) requireActivity()).getUnityPlayer().resume();
    }

    @Override
    public void onDestroyView() {
        frameLayout.removeAllViews();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        ((MainActivity) requireActivity()).getUnityPlayer().pause();
        super.onDestroy();
    }
}