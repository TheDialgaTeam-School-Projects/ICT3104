using GoogleARCore;
using UnityEngine;
using UnityEngine.EventSystems;

public class ARController : MonoBehaviour
{
    /// <summary>
    /// The first-person camera being used to render the passthrough camera image (i.e. AR
    /// background).
    /// </summary>
    public Camera FirstPersonCamera;

    /// <summary>
    /// A prefab to place when a raycast from a user touch hits a vertical plane.
    /// </summary>
    public GameObject GameObjectPrefab;

    private void Awake()
    {
        // Enable ARCore to target 60fps camera capture frame rate on supported devices.
        // Note, Application.targetFrameRate is ignored when QualitySettings.vSyncCount != 0.
        Application.targetFrameRate = 60;

        Screen.autorotateToLandscapeLeft = false;
        Screen.autorotateToLandscapeRight = false;
        Screen.autorotateToPortraitUpsideDown = false;
        Screen.orientation = ScreenOrientation.Portrait;
    }

    private void Update()
    {
        // Only allow the screen to sleep when not tracking.
        Screen.sleepTimeout = Session.Status != SessionStatus.Tracking ? SleepTimeout.SystemSetting : SleepTimeout.NeverSleep;

        // If the player has not touched the screen, we are done with this update.
        Touch touch;
        if (InstantPreviewInput.touchCount < 1 || (touch = InstantPreviewInput.GetTouch(0)).phase != TouchPhase.Began) return;

        // Should not handle input if the player is pointing on UI.
        if (EventSystem.current.IsPointerOverGameObject(touch.fingerId)) return;

        // Raycast against the location the player touched to search for planes.
        const TrackableHitFlags raycastFilter = TrackableHitFlags.PlaneWithinPolygon | TrackableHitFlags.FeaturePointWithSurfaceNormal;

        if (Frame.Raycast(touch.position.x, touch.position.y, raycastFilter, out var hit))
        {
            // Use hit pose and camera pose to check if hittest is from the
            // back of the plane, if it is, no need to create the anchor.
            if (hit.Trackable is DetectedPlane && Vector3.Dot(FirstPersonCamera.transform.position - hit.Pose.position, hit.Pose.rotation * Vector3.up) < 0)
            {
                Debug.Log("Hit at back of the current DetectedPlane");
            }
            else
            {
                // Instantiate prefab at the hit pose.
                var newGameObject = Instantiate(GameObjectPrefab, hit.Pose.position, hit.Pose.rotation);

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
    }
}