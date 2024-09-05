package com.example.frenbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Course_Details extends AppCompatActivity {
    public static String courseUUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        String title=getIntent().getStringExtra("title");
        String id1=getIntent().getStringExtra("id");
        String ins=getIntent().getStringExtra("instructor");
        String uuid = getIntent().getStringExtra("uuid");
        String desc = getIntent().getStringExtra("desc");
        String sharedBy = getIntent().getStringExtra("sharedBy");
        Course_Details.courseUUID = uuid;

        TextView tle=findViewById(R.id.ctitle);
        TextView id=findViewById(R.id.course_id);
        TextView instructor=findViewById(R.id.instructor);
        ImageView back=findViewById(R.id.back);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView shared = findViewById(R.id.shared);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView courseDesc = findViewById(R.id.courseDesc);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button links=findViewById(R.id.links);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button files=findViewById(R.id.files);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        links.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Course_Details.this, Course_Links.class);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
            }
        });
        files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Course_Details.this, Course_Files.class);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
            }
        });
        tle.setText(title);
        id.setText(id1);
        instructor.setText(ins);
        courseDesc.setText(desc);

        if(sharedBy == null) {
            shared.setVisibility(View.GONE);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(sharedBy);

            userDocument.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                shared.setText("Shared by " + documentSnapshot.getString("name"));
                            }
                        }
                    });
        }

    }
}