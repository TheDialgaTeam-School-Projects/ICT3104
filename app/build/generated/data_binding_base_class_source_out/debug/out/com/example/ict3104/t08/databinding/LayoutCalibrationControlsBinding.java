// Generated by view binder compiler. Do not edit!
package com.example.ict3104.t08.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.esri.arcgisruntime.toolkit.control.JoystickSeekBar;
import com.example.ict3104.t08.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutCalibrationControlsBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final JoystickSeekBar altitudeJoystick;

  @NonNull
  public final TextView altitudeTextView;

  @NonNull
  public final ConstraintLayout calibrationView;

  @NonNull
  public final JoystickSeekBar headingJoystick;

  @NonNull
  public final TextView headingTextView;

  private LayoutCalibrationControlsBinding(@NonNull ConstraintLayout rootView,
      @NonNull JoystickSeekBar altitudeJoystick, @NonNull TextView altitudeTextView,
      @NonNull ConstraintLayout calibrationView, @NonNull JoystickSeekBar headingJoystick,
      @NonNull TextView headingTextView) {
    this.rootView = rootView;
    this.altitudeJoystick = altitudeJoystick;
    this.altitudeTextView = altitudeTextView;
    this.calibrationView = calibrationView;
    this.headingJoystick = headingJoystick;
    this.headingTextView = headingTextView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutCalibrationControlsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutCalibrationControlsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_calibration_controls, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutCalibrationControlsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.altitudeJoystick;
      JoystickSeekBar altitudeJoystick = rootView.findViewById(id);
      if (altitudeJoystick == null) {
        break missingId;
      }

      id = R.id.altitudeTextView;
      TextView altitudeTextView = rootView.findViewById(id);
      if (altitudeTextView == null) {
        break missingId;
      }

      ConstraintLayout calibrationView = (ConstraintLayout) rootView;

      id = R.id.headingJoystick;
      JoystickSeekBar headingJoystick = rootView.findViewById(id);
      if (headingJoystick == null) {
        break missingId;
      }

      id = R.id.headingTextView;
      TextView headingTextView = rootView.findViewById(id);
      if (headingTextView == null) {
        break missingId;
      }

      return new LayoutCalibrationControlsBinding((ConstraintLayout) rootView, altitudeJoystick,
          altitudeTextView, calibrationView, headingJoystick, headingTextView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
