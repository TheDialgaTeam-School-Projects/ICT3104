using UnityEngine;

public class NavigationArrowComponent : MonoBehaviour
{
    [SerializeField]
    private Transform navigationArrowTransform;

    private AndroidJavaClass _navigationManager;

    private void Start()
    {
        _navigationManager = new AndroidJavaClass("edu.singaporetech.ict3104.java_to_unity_proxy.NavigationManager");
        navigationArrowTransform.eulerAngles = new Vector3(45, 0, 0);
    }

    private void Update()
    {
        var targetRotation = _navigationManager.CallStatic<float>("getTargetAzimuth");
        navigationArrowTransform.eulerAngles = new Vector3(45, 0, Mathf.LerpAngle(navigationArrowTransform.eulerAngles.z, targetRotation, Time.deltaTime * 5));
    }

    private void OnDestroy()
    {
        _navigationManager.Dispose();
    }
}