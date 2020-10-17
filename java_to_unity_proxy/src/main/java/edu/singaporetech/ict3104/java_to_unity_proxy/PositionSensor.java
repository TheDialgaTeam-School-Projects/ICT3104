package edu.singaporetech.ict3104.java_to_unity_proxy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public final class PositionSensor implements SensorEventListener {

    private static final String TAG = PositionSensor.class.getName();

    private static final float[] accelerometerReading = new float[3];
    private static final float[] magnetometerReading = new float[3];
    private static final float[] rotationMatrix = new float[9];
    private static final float[] orientationAngles = new float[3];

    private final SensorManager sensorManager;

    public PositionSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static float getAzimuthInDegree() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        float azimuth = orientationAngles[0];
        float azimuthInDegree = (float) ((azimuth >= 0) ? azimuth * (180 / Math.PI) : (azimuth * (180 / Math.PI)) + 360);
        Log.d(TAG, "Current Azimuth (Degree): " + azimuthInDegree);
        return azimuthInDegree;
    }

    public void onResume() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
