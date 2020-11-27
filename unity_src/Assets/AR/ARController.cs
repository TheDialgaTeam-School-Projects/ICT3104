using GoogleARCore;
using UnityEngine;

#if UNITY_EDITOR
    using Input = GoogleARCore.InstantPreviewInput;
#endif

public class ARController : MonoBehaviour
{
    /// <summary>
    /// A prefab to place when a raycast from a user touch hits a vertical plane.
    /// </summary>
    public GameObject gameObjectPrefab;

    private AndroidJavaClass _objectListenerManager;

    private void Awake()
    {
        // Enable ARCore to target 60fps camera capture frame rate on supported devices.
        // Note, Application.targetFrameRate is ignored when QualitySettings.vSyncCount != 0.
        Application.targetFrameRate = 60;

        Screen.autorotateToLandscapeLeft = false;
        Screen.autorotateToLandscapeRight = false;
        Screen.autorotateToPortraitUpsideDown = false;
        Screen.orientation = ScreenOrientation.Portrait;

        _objectListenerManager = new AndroidJavaClass("edu.singaporetech.ict3104.java_to_unity_proxy.ObjectListenerManager");
    }

    private void Update()
    {
        // Only allow the screen to sleep when not tracking.
        Screen.sleepTimeout = Session.Status != SessionStatus.Tracking ? SleepTimeout.SystemSetting : SleepTimeout.NeverSleep;

        // If the player has not touched the screen, we are done with this update.
        Touch touch;
        if (Input.touchCount < 1 || (touch = Input.GetTouch(0)).phase != TouchPhase.Began) return;

        if (Physics.Raycast(Camera.current.ScreenPointToRay(new Vector3(touch.position.x, touch.position.y, 0)), out var _))
        {
            _objectListenerManager.CallStatic("invoke");
            return;
        }

        if (Frame.Raycast(touch.position.x, touch.position.y, TrackableHitFlags.Default, out var hit))
        {
            // Instantiate prefab at the hit pose.
            var newGameObject = Instantiate(gameObjectPrefab, hit.Pose.position, hit.Pose.rotation);

            // Compensate for the hitPose rotation facing away from the raycast (i.e.
            // camera).
            newGameObject.transform.Rotate(0, 180, 0, Space.Self);

            // Create an anchor to allow ARCore to track the hitpoint as understanding of
            // the physical world evolves.
            var anchor = hit.Trackable.CreateAnchor(hit.Pose);

            // Make game object a child of the anchor.
            newGameObject.transform.parent = anchor.transform;
        }
    }

    private void OnDestroy()
    {
        _objectListenerManager.Dispose();
    }
}