package com.example.frenbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event_Details extends AppCompatActivity {
    Button interBut;
    TextView interText;
    static boolean isInterested;
    static int ct;
    public static String selectedEvent;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        String title=getIntent().getStringExtra("title");
        String place=getIntent().getStringExtra("place");
        String time=getIntent().getStringExtra("time");
        String uuid=getIntent().getStringExtra("uuid");
        String desc = getIntent().getStringExtra("desc");
        String note = getIntent().getStringExtra("note");
        selectedEvent = uuid;
        isInterested = false;

        interBut = findViewById(R.id.interBut);
        interText = findViewById(R.id.interText);
        TextView tle=findViewById(R.id.title);
        TextView tm=findViewById(R.id.time);
        TextView location=findViewById(R.id.location);
        TextView details = findViewById(R.id.details);
        ImageView back=findViewById(R.id.back);
        FloatingActionButton edit_event = findViewById(R.id.edit_event);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edit_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Event_Details.this,Create_event.class);
                intent.putExtra("name", title);
                intent.putExtra("desc", desc);
                intent.putExtra("note", note);
                intent.putExtra("location", place);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
            }
        });
        tle.setText(title);
        tm.setText(time);
        location.setText(place);
        details.setText(desc);
        interBut.setText("Are you interested ?");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        String userId = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocument = db.collection("Users").document(userId);
        CollectionReference eventCol = db.collection("Events");
        DocumentReference eventDoc = eventCol.document(uuid);
        CollectionReference interested = eventDoc.collection("Interested");

        interested.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;// Clear existing data before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId1 = document.getString("userId");

                            if(Objects.equals(userId1, user.getUid())) {
                                isInterested = true;
                                interBut.setText("You are interested");
                            }
                            count++;
                        }
                        interText.setText(count + " are interested");
                        ct = count;

                        // Notify the RecyclerView to refresh
                    } else {
                        // Handle the error
                    }
                });



        interBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference newDock = interested.document(user.getUid());
                userDocument.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Access fields from the DocumentSnapshot
                                    String name = documentSnapshot.getString("name");
                                    String imgPath = documentSnapshot.getString("profilePic");
                                    Map<String, String> userData = new HashMap<>();
                                    userData.put("userId", user.getUid() );
                                    userData.put("name", name);
                                    userData.put("profilePic", imgPath);
                                    if(!Event_Details.isInterested) {
                                        newDock.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                finish();
                                                Intent intent = new Intent(Event_Details.this, Event_Details.class);
                                                intent.putExtra("title",title);
                                                intent.putExtra("place",place);
                                                intent.putExtra("time",time);
                                                intent.putExtra("uuid",user.getUid());
                                                intent.putExtra("desc",desc);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        newDock.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Document was successfully deleted
                                                        finish();
                                                        Intent intent = new Intent(Event_Details.this, Event_Details.class);
                                                        intent.putExtra("title",title);
                                                        intent.putExtra("place",place);
                                                        intent.putExtra("time",time);
                                                        intent.putExtra("uuid",user.getUid());
                                                        intent.putExtra("desc",desc);
                                                        startActivity(intent);
                                                    }
                                                });
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


        });
        interText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Event_Details.this, ResponseList.class);
                startActivity(intent);
            }
        });

    }
}