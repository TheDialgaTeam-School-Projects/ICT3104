using GoogleARCore;
using UnityEngine;

public class ARController : MonoBehaviour
{
    private void Awake()
    {
        // Enable ARCore to target 60fps camera capture frame rate on supported devices.
        // Note, Application.targetFrameRate is ignored when QualitySettings.vSyncCount != 0.
        Application.targetFrameRate = 60;
    }

    private void Update()
    {
        // Only allow the screen to sleep when not tracking.
        Screen.sleepTimeout = Session.Status != SessionStatus.Tracking ? SleepTimeout.SystemSetting : SleepTimeout.NeverSleep;
    }
}