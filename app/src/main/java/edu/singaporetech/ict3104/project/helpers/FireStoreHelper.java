package edu.singaporetech.ict3104.project.helpers;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public final class FireStoreHelper {

    private static final String USER_DATA_COLLECTION = "Users";

    public static Task<DocumentSnapshot> getUserData(String email) {
        if (email == null) return Tasks.forException(new NullPointerException("email is null."));

        return FirebaseFirestore.getInstance()
                .collection(USER_DATA_COLLECTION)
                .document(email)
                .get();
    }

    public static Task<Void> setOrUpdateUserData(String email, Map<String, Object> userData) {
        if (email == null) return Tasks.forException(new NullPointerException("email is null."));
        if (userData == null) return Tasks.forException(new NullPointerException("userData is null."));

        final DocumentReference userDataDocumentReference = FirebaseFirestore.getInstance()
                .collection(USER_DATA_COLLECTION)
                .document(email);

        return userDataDocumentReference.get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                final DocumentSnapshot userDataDocumentSnapshot = task.getResult();
                if (userDataDocumentSnapshot == null)
                    return Tasks.forException(new NullPointerException());

                if (userDataDocumentSnapshot.exists()) {
                    return userDataDocumentReference.update(userData);
                } else {
                    return userDataDocumentReference.set(userData);
                }
            } else if (task.isCanceled()) {
                return Tasks.forCanceled();
            } else {
                Exception exception = task.getException();
                return exception == null ? Tasks.forException(new NullPointerException()) : Tasks.forException(exception);
            }
        });
    }

}
