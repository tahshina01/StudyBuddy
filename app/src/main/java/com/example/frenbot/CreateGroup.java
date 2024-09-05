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

public class CreateGroup extends AppCompatActivity {
    Button add;
    TextInputEditText title;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ImageView back=findViewById(R.id.back);
        title = findViewById(R.id.title);
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
                    Toast.makeText(CreateGroup.this,"Enter Group name",Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userDocument = db.collection("Users").document(userId);
                    CollectionReference noticeCollection = userDocument.collection("NoticeGroup");

                    String groupId = UUID.randomUUID().toString();
                    Map<String, String> group = new HashMap<>();
                    group.put("uuid", groupId);
                    group.put("groupName", titleString);
                    group.put("admin", userId);

                    DocumentReference newDocument = noticeCollection.document(groupId);

                    newDocument.set(group)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    CollectionReference noticeCol = db.collection("Notice");
                                    Map<String, String> noticeGroup = new HashMap<>();
                                    noticeGroup.put("uuid", groupId);
                                    noticeGroup.put("groupName", titleString);
                                    noticeGroup.put("admin", userId);

                                    DocumentReference noticeDocument = noticeCol.document(groupId);
                                    noticeDocument.set(noticeGroup).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CreateGroup.this, "Group created", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CreateGroup.this, Notice.class);
                                            startActivity(intent);

                                            Intent intent2 = new Intent();
                                            setResult(RESULT_OK, intent2);
                                            finish();
                                        }
                                    });

                                    // Document was successfully added to the courseLinkCollection
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });

                }
            }
        });
    }
}