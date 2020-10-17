package edu.singaporetech.ict3104.java_to_unity_proxy;

public final class NavigationManager {

    private static final double TWO_PI = 2 * Math.PI;

    private static float targetAzimuth = 0;
    private static float distanceRemaining = 0;

    /**
     * Get the target azimuth in degree.
     *
     * @return Target azimuth in degree.
     * @apiNote This method is referenced by Unity and should not be refactored.
     */
    public static float getTargetAzimuth() {
        final float targetAzimuth = NavigationManager.targetAzimuth;
        final float currentAzimuth = PositionSensor.getCurrentAzimuth();

        float resultingAzimuth = targetAzimuth - currentAzimuth;
        while (resultingAzimuth > Math.PI) resultingAzimuth -= TWO_PI;
        while (resultingAzimuth < -Math.PI) resultingAzimuth += TWO_PI;

        return (float) Math.toDegrees(resultingAzimuth * -1);
    }

    /**
     * Set the target azimuth for the navigation route.
     *
     * @param targetAzimuth Target azimuth in radian.
     */
    public static void setTargetAzimuth(float targetAzimuth) {
        while (targetAzimuth > Math.PI) targetAzimuth -= TWO_PI;
        while (targetAzimuth < -Math.PI) targetAzimuth += TWO_PI;
        NavigationManager.targetAzimuth = targetAzimuth;
    }

    /**
     * Get the distance remaining in metres.
     *
     * @return The distance remaining in metres.
     * @apiNote This method is referenced by Unity and should not be refactored.
     */
    public static float getDistanceRemaining() {
        return distanceRemaining;
    }

    /**
     * Set the distance remaining for the navigation route.
     *
     * @param distanceRemaining Distance remaining in metres.
     */
    public static void setDistanceRemaining(float distanceRemaining) {
        NavigationManager.distanceRemaining = distanceRemaining;
    }

}
