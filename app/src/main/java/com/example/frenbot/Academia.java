package com.example.frenbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Academia extends AppCompatActivity implements RCViewInterface {
    public static boolean isArchive;
    private static final int finish_code = 436;
    FloatingActionButton add_course, archive;
    TextView archiveText;
    ArrayList<coursemodel> coursemodels=new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academia);
        RecyclerView recyclerView=findViewById(R.id.rcview);

        courseRVadapter rVadapter =new courseRVadapter(this,coursemodels,this);
        recyclerView.setAdapter(rVadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        add_course = findViewById(R.id.add_course);
        archive = findViewById(R.id.archive);
        archiveText = findViewById(R.id.archiveText);
        ImageView back=findViewById(R.id.back);

        Academia.isArchive = getIntent().getBooleanExtra("isArchive", false);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Academia.this,add_course.class);
                intent.putExtra("title", "");
                intent.putExtra("desc", "");
                intent.putExtra("id", "");
                intent.putExtra("instructor", "");
                intent.putExtra("uuid", "");
                startActivityForResult(intent, finish_code);
            }
        });

        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Academia.this, Academia.class);
                intent.putExtra("isArchive", !isArchive);
                startActivity(intent);
            }
        });

        String colorString;
        if(isArchive) {
            colorString = "#292828";
            archiveText.setText("Archived Course");
        } else {
            colorString = "#FFA500";
            archiveText.setText("Your Course");
        }
        int color = Color.parseColor(colorString);
        ColorStateList newTintList = ColorStateList.valueOf(color);
        archive.setBackgroundTintList(newTintList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == finish_code) {
            if (resultCode == RESULT_OK) {
                // The child activity has finished, so finish the parent activity
                finish();
            }
        }
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent =new Intent(Academia.this,Course_Details.class);
        intent.putExtra("title",coursemodels.get(position).getcourse());
        intent.putExtra("id",coursemodels.get(position).getid());
        intent.putExtra("instructor",coursemodels.get(position).getinstructor());
        intent.putExtra("uuid",coursemodels.get(position).getUuid());
        intent.putExtra("desc",coursemodels.get(position).getDesc());
        intent.putExtra("sharedBy",coursemodels.get(position).sharedBy);
        startActivity(intent);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = courseRVadapter.MyViewHolder.position; // Get the clicked position
        System.out.println(coursemodels.get(position).getcourse());
        System.out.println(item);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference coursesCollection = null;
        DocumentReference userDocument;
        FirebaseFirestore db;
        if (user != null) {
            String userId = user.getUid();

            db = FirebaseFirestore.getInstance();
            userDocument = db.collection("Users").document(userId);
            coursesCollection = userDocument.collection("Course");
        }

        switch (item.getItemId()) {
            case 1: // Edit
                // Handle the "Edit" action here using the 'position'
                Intent intent = new Intent(Academia.this,add_course.class);
                intent.putExtra("isArchive", isArchive);
                intent.putExtra("title", coursemodels.get(position).getcourse());
                intent.putExtra("desc", coursemodels.get(position).getDesc());
                intent.putExtra("id", coursemodels.get(position).getid());
                intent.putExtra("instructor", coursemodels.get(position).getinstructor());
                intent.putExtra("uuid", coursemodels.get(position).getUuid());
                intent.putExtra("archive", coursemodels.get(position).getArchive());
                intent.putExtra("sharedBy", coursemodels.get(position).sharedBy);
                startActivityForResult(intent, finish_code);
                return true;

            case 4: // Archive
                // Handle the "Archive" action here using the 'position'
                Map<String, Object> course = new HashMap<>();
                course.put("title", coursemodels.get(position).getcourse());
                course.put("instructor", coursemodels.get(position).getinstructor());
                course.put("id", coursemodels.get(position).getid());
                course.put("uuid", coursemodels.get(position).getUuid());
                course.put("description", coursemodels.get(position).getDesc());
                course.put("archive", !isArchive);

                coursesCollection.document(coursemodels.get(position).getUuid())
                        .set(course)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // User data has been successfully added.
                                finish();
                                Intent intent = new Intent(Academia.this, Academia.class);
                                intent.putExtra("isArchive", isArchive);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error.
                                Toast.makeText(Academia.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });
                return true;

            case 2: // Delete
                // Handle the "Delete" action here using the 'position'
                DocumentReference courseDocument = coursesCollection.document(coursemodels.get(position).getUuid());
                courseDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document was successfully deleted
                                finish();
                                Intent intent = new Intent(Academia.this, Academia.class);
                                intent.putExtra("isArchive", isArchive);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error
                            }
                        });
                return true;

            case 3: // share
                // Handle the "Delete" action here using the 'position'
                Intent intent5 = new Intent(Academia.this, ShareList.class);
                intent5.putExtra("isArchive", isArchive);
                intent5.putExtra("title", coursemodels.get(position).getcourse());
                intent5.putExtra("desc", coursemodels.get(position).getDesc());
                intent5.putExtra("id", coursemodels.get(position).getid());
                intent5.putExtra("instructor", coursemodels.get(position).getinstructor());
                intent5.putExtra("uuid", coursemodels.get(position).getUuid());
                intent5.putExtra("archive", coursemodels.get(position).getArchive());
                startActivity(intent5);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}