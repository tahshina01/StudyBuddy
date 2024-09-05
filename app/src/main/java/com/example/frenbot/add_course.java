package com.example.frenbot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class add_course extends AppCompatActivity {
    TextInputEditText courseName, courseID, instructor, desc;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        courseName = findViewById(R.id.course);
        courseID = findViewById(R.id.course_id);
        desc = findViewById(R.id.Description);
        instructor = findViewById(R.id.instructor);

        courseName.setText(getIntent().getStringExtra("title"));
        courseID.setText(getIntent().getStringExtra("id"));
        instructor.setText(getIntent().getStringExtra("instructor"));
        desc.setText(getIntent().getStringExtra("desc"));

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add = findViewById(R.id.add);

        if(!Objects.equals(getIntent().getStringExtra("uuid"), "")) {
            add.setText("Apply changes");
        }

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title, id, description, instruct;
                title = String.valueOf(courseName.getText());
                id = String.valueOf(courseID.getText());
                description = String.valueOf(desc.getText());
                instruct = String.valueOf(instructor.getText());

                if(title.equals("")) {
                    Toast.makeText(add_course.this,"Enter course title",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(id.equals("")) {
                    Toast.makeText(add_course.this,"Enter course id",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(instruct.equals("")) {
                    Toast.makeText(add_course.this,"Enter course instructor",Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userDocument = db.collection("Users").document(userId);
                    CollectionReference coursesCollection = userDocument.collection("Course");

                    String courseId;
                    if(Objects.equals(getIntent().getStringExtra("uuid"), "")) {
                        courseId = UUID.randomUUID().toString();
                    } else {
                        courseId = getIntent().getStringExtra("uuid");
                    }

                    Map<String, Object> course = new HashMap<>();
                    course.put("title", title);
                    course.put("description", description);
                    course.put("instructor", instruct);
                    course.put("id", id);
                    course.put("uuid", courseId);
                    boolean isArchived = getIntent().getBooleanExtra("archive", false);
                    String sharedBy = getIntent().getStringExtra("sharedBy");
                    if(sharedBy != null) {
                        course.put("sharedBy", sharedBy);
                    }
                    course.put("archive", isArchived);

                    if(Objects.equals(getIntent().getStringExtra("uuid"), "")) {
                        coursesCollection.document(courseId)
                                .set(course)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // User data has been successfully added.
                                        Toast.makeText(add_course.this, "course added", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(add_course.this, Academia.class);
                                        startActivity(intent);

                                        Intent intent2 = new Intent();
                                        setResult(RESULT_OK, intent2);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the error.
                                        Toast.makeText(add_course.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                    } else {
                        coursesCollection.document(courseId)
                                .update(course)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // User data has been successfully added.
                                        Toast.makeText(add_course.this, "course updated", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(add_course.this, Academia.class);
                                        intent.putExtra("isArchive", Academia.isArchive);

                                        startActivity(intent);

                                        Intent intent2 = new Intent();
                                        setResult(RESULT_OK, intent2);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the error.
                                        Toast.makeText(add_course.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                    }

                }
            }
        });


    }

}