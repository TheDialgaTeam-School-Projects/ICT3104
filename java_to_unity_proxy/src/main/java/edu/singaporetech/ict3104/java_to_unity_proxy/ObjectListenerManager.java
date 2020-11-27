package edu.singaporetech.ict3104.java_to_unity_proxy;

import java.util.ArrayList;
import java.util.List;

public final class ObjectListenerManager {

    private static final List<ObjectListener> objectListeners = new ArrayList<>();

    public static void registerListener(ObjectListener objectListener) {
        objectListeners.add(objectListener);
    }

    public static void clearListener() {
        objectListeners.clear();
    }

    /**
     * Invoke all object listeners.
     *
     * @apiNote This method is referenced by Unity and should not be refactored.
     */
    public static void invoke() {
        for (final ObjectListener objectListener : objectListeners) {
            objectListener.invokeObjectListener();
        }
    }

}
