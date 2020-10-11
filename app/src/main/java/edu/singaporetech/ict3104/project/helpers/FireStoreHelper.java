package edu.singaporetech.ict3104.project.helpers;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.ExecutionException;

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
        if (userData == null)
            return Tasks.forException(new NullPointerException("userData is null."));

        final DocumentReference userDataDocumentReference = FirebaseFirestore.getInstance()
                .collection(USER_DATA_COLLECTION)
                .document(email);

        final Task<DocumentSnapshot> userDataDocumentSnapshotTask = userDataDocumentReference.get();

        try {
            final DocumentSnapshot userDataDocumentSnapshot = Tasks.await(userDataDocumentSnapshotTask);

            if (userDataDocumentSnapshot.exists()) {
                return userDataDocumentReference.update(userData);
            } else {
                return userDataDocumentReference.set(userData);
            }
        } catch (ExecutionException | InterruptedException e) {
            return Tasks.forException(e);
        }
    }

}
