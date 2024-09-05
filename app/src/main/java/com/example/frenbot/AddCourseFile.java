package com.example.frenbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCourseFile extends AppCompatActivity {
    public static String fileName;
    Button add;
    TextInputEditText title;
    ImageView icon;
    FirebaseStorage storage;
    StorageReference storageRef;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_file);
        String type = Course_Files.Type;
        String storagePath = Course_Files.StoragePath;
        String fileExtension = Course_Files.FileExtension;
        Uri fileUri = Course_Files.FileUri;
        ImageView back=findViewById(R.id.back);
        icon=findViewById(R.id.icon);
        title = findViewById(R.id.title);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if (type.equals("pdf")) {
            icon.setImageResource(R.drawable.pdf);
        } else if (type.equals("img")) {
            icon.setImageResource(R.drawable.image);
        } else if (type.equals("txt")) {
            icon.setImageResource(R.drawable.txt);
        } else if (type.equals("doc")) {
            icon.setImageResource(R.drawable.doc);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add = findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String titleString;
                titleString = String.valueOf(title.getText());

                if(TextUtils.isEmpty(titleString)) {
                    Toast.makeText(AddCourseFile.this,"Enter File name",Toast.LENGTH_SHORT).show();
                    return;
                }
                AddCourseFile.fileName = titleString;
                StorageReference fileRef = storageRef.child(storagePath + UUID.randomUUID().toString() + "/" + AddCourseFile.fileName + "." + fileExtension);
                UploadTask uploadTask = fileRef.putFile(fileUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        String downloadURL = downloadUrl.toString();
                        // Now you can save the downloadURL to Firestore or perform other actions.
                        // File uploaded successfully

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocument = db.collection("Users").document(userId);
                            CollectionReference coursesCollection = userDocument.collection("Course");

                            DocumentReference courseDocument = coursesCollection.document(Course_Details.courseUUID);
                            CollectionReference courseFileCollection = courseDocument.collection("courseFileCollection");

                            String fileId = UUID.randomUUID().toString();
                            Map<String, String> courseFile = new HashMap<>();
                            courseFile.put("title", AddCourseFile.fileName);
                            courseFile.put("downloadUri", downloadURL);
                            courseFile.put("uuid", fileId);
                            courseFile.put("fileType", type);

                            DocumentReference newLinkDocument = courseFileCollection.document(fileId);

                            newLinkDocument.set(courseFile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document was successfully added to the courseLinkCollection
                                            Toast.makeText(AddCourseFile.this, "url stored", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddCourseFile.this, Course_Files.class);
                                            startActivity(intent);

                                            Intent intent2 = new Intent();
                                            setResult(RESULT_OK, intent2);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                        }
                                    });

                        }
                    });

                }).addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                });
            }
        });
    }
}
