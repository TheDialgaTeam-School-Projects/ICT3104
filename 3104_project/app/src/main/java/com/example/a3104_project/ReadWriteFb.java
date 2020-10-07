package com.example.a3104_project;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReadWriteFb {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String commuteType;

    public String getCommuteMethod(String username) {
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            commuteType = documentSnapshot.getString("Commute Type");
                            System.out.println("LET GOOOOOOOOOOOOOOO : " + commuteType);
                        } else {
                            // Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        return commuteType;
    }
}
