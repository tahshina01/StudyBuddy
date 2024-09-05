package com.example.frenbot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    ShapeableImageView profilePic;
    String imgPath;
    TextView name, phone, dob, varsity, dept, session;
    TextView gmail, nameText;
    FirebaseStorage storage;
    StorageReference storageRef;
    private static final int finish_code = 436;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root= (ViewGroup) inflater.inflate(R.layout.profile_fragment,container,false);
        FloatingActionButton edit=root.findViewById(R.id.edit);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        nameText = root.findViewById(R.id.textView);
        gmail = root.findViewById(R.id.textView2);

        profilePic = root.findViewById(R.id.imageView);
        name = root.findViewById(R.id.name);
        phone = root.findViewById(R.id.name2);
        dob = root.findViewById(R.id.name4);
        varsity = root.findViewById(R.id.name6);
        dept = root.findViewById(R.id.name8);
        session = root.findViewById(R.id.name10);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(userId);

            userDocument.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Access fields from the DocumentSnapshot

                                nameText.setText(documentSnapshot.getString("name"));
                                gmail.setText(documentSnapshot.getString("email"));
                                name.setText(documentSnapshot.getString("name"));
                                phone.setText(documentSnapshot.getString("phone"));
                                dob.setText(documentSnapshot.getString("dob"));
                                varsity.setText(documentSnapshot.getString("varsity"));
                                dept.setText(documentSnapshot.getString("dept"));
                                session.setText(documentSnapshot.getString("session"));

                                imgPath = documentSnapshot.getString("profilePic");
                                if (!Objects.equals(imgPath, "") && imgPath != null) {
                                    loadAndDisplayImage(imgPath);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the FinanceActivity when financeCard is clicked
                Intent intent = new Intent(getActivity(), Edit_Profile.class);
                startActivity(intent);
            }
        });
        return root;
    }

    private void loadAndDisplayImage(String downloadUri) {
        Picasso.get().load(downloadUri).into(profilePic);
    }

    public void updateUI(Map<String, String> updatedData) {
        // Update your UI components with the updated data
        // For example:
        nameText.setText(updatedData.get("name"));
        gmail.setText(updatedData.get("email"));
        // Update other UI components
    }
}
