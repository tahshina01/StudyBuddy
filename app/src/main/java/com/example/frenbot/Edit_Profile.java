package com.example.frenbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Edit_Profile extends AppCompatActivity implements OnDataSavedListener{
    private static final int REQUEST_CODE = 123;

//    ImageView profilePic;
//    String nam, phon, birth, varsit, dep, sess, email, pic;
    ShapeableImageView profilePic;
    String imgPath;
    EditText name, phone, dob, varsity, dept, session;
    TextView gmail, nameText;
    Uri fileUrl;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button uploadImage = findViewById(R.id.uploadImage);
        Button save = findViewById(R.id.sendButton);
        fileUrl = null;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        nameText = findViewById(R.id.textView);
        gmail = findViewById(R.id.textView2);

        profilePic = findViewById(R.id.imageView);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.name2);
        dob = findViewById(R.id.name4);
        varsity = findViewById(R.id.name6);
        dept = findViewById(R.id.name8);
        session = findViewById(R.id.name10);

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
                                if(!Objects.equals(imgPath, "") && imgPath != null) {
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

            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open a file picker dialog to allow the user to select image files
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*"); // Set the MIME type to restrict to image files
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        startActivityForResult(Intent.createChooser(intent, "Select an image"), REQUEST_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Handle exception if no file picker is available
                    }
                }
            });

        }



        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().equals("")) {
                    Toast.makeText(Edit_Profile.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser fuser = mAuth.getCurrentUser();

                if (fuser != null) {
                    String userId = fuser.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userDocument = db.collection("Users").document(userId);

                    Map<String, String> userData = new HashMap<>();
                    userData.put("name", name.getText().toString());
                    userData.put("phone", phone.getText().toString());
                    userData.put("dob", dob.getText().toString());
                    userData.put("varsity", varsity.getText().toString());
                    userData.put("dept", dept.getText().toString());
                    userData.put("session", session.getText().toString());
                    userData.put("email", gmail.getText().toString());
                    userData.put("userId", userId);

                    if(fileUrl != null) {
                        String type;
                        String mimeType = getContentResolver().getType(fileUrl);

                        // Determine the file extension from the file name or MIME type
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUrl.toString());

                        // Set up the Firebase Storage reference based on the file type
                        String storagePath = "files/images/";

                        StorageReference fileRef = storageRef.child(storagePath + UUID.randomUUID().toString() + "." + fileExtension);
                        UploadTask uploadTask = fileRef.putFile(fileUrl);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                String downloadURL = downloadUrl.toString();
                                imgPath = downloadURL;
                                userData.put("profilePic", imgPath);

                                userDocument.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // User data has been successfully added.
                                        onDataSaved(userData);
                                        finish();
                                    }
                                });
                            });

                        }).addOnFailureListener(exception -> {
                            // Handle unsuccessful uploads
                        });
                    } else {

                        // Assuming you have a Firebase Firestore instance (db) and a valid DocumentReference (userDocument2)
                        userData.put("profilePic", imgPath);
                        userDocument.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // User data has been successfully added.
                                onDataSaved(userData);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // The user has selected a file
            if (data != null) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    profilePic.setImageURI(fileUri);
                    fileUrl = fileUri;
                }
            }
        }
    }

    private void loadAndDisplayImage(String downloadUri) {
        Picasso.get().load(downloadUri).into(profilePic);
    }

    @Override
    public void onDataSaved(Map<String, String> updatedData) {
        // Notify the ProfileFragment with the updated data
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragmentTag");

        if (profileFragment != null) {
            profileFragment.updateUI(updatedData);
        } else {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
        }
    }

}