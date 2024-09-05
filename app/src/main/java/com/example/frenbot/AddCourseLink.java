package com.example.frenbot;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCourseLink extends AppCompatActivity {
    Button add;
    TextInputEditText link, title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_link);
        String uuid = getIntent().getStringExtra("uuid");
        ImageView back=findViewById(R.id.back);
        link = findViewById(R.id.link);
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
                String titleString, linkText;
                titleString = String.valueOf(title.getText());
                linkText = String.valueOf(link.getText());

                if(TextUtils.isEmpty(titleString)) {
                    Toast.makeText(AddCourseLink.this,"Enter link",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(linkText)) {
                    Toast.makeText(AddCourseLink.this,"Enter link title",Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userDocument = db.collection("Users").document(userId);
                    CollectionReference coursesCollection = userDocument.collection("Course");

                    DocumentReference courseDocument = coursesCollection.document(Course_Details.courseUUID);
                    CollectionReference courseLinkCollection = courseDocument.collection("courseLinkCollection");

                    String linkId = UUID.randomUUID().toString();
                    Map<String, String> courseLink = new HashMap<>();
                    courseLink.put("title",titleString);
                    courseLink.put("link",linkText);
                    courseLink.put("linkId", linkId);

                    DocumentReference newLinkDocument = courseLinkCollection.document(linkId);

                    newLinkDocument.set(courseLink)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document was successfully added to the courseLinkCollection
                                    Toast.makeText(AddCourseLink.this,"Link added",Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(AddCourseLink.this, Course_Links.class);
                                    startActivity(intent);
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
