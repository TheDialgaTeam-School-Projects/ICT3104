using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Text))]
public class NavigationDistanceComponent : MonoBehaviour
{
    [SerializeField]
    private Text navigationDistanceText;

    private AndroidJavaClass _navigationManager;

    private void Start()
    {
        _navigationManager = new AndroidJavaClass("edu.singaporetech.ict3104.java_to_unity_proxy.NavigationManager");
        navigationDistanceText.text = "0.00m";
    }

    private void Update()
    {
        var distanceRemaining = _navigationManager.CallStatic<float>("getDistanceRemaining");
        navigationDistanceText.text = $"{distanceRemaining:0.00}m";
    }

    private void OnDestroy()
    {
        _navigationManager.Dispose();
    }
}