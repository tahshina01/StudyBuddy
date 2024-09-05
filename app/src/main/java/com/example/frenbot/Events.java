package com.example.frenbot;

import static com.example.frenbot.Constants.TOPIC;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Events extends AppCompatActivity implements RCViewInterface {
    FloatingActionButton create_event;
    ArrayList<eventmodel> eventmodels=new ArrayList<>();

    ArrayList<Map<String,Object>> event_data = new ArrayList<>();
    eventRVadapter rVadapter;
    public static String flag;
    private static String d;
    LinearLayout myEvents, pastEvents;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);
        RecyclerView recyclerView=findViewById(R.id.reventview);
        myEvents = findViewById(R.id.MyEvents);
        pastEvents = findViewById(R.id.pastEvents);
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);
        getFirestoreEvents();
        System.out.println(this.event_data.size());
        flag = getIntent().getStringExtra("flag");



        rVadapter =new eventRVadapter(this,eventmodels,this);
        recyclerView.setAdapter(rVadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        create_event = findViewById(R.id.create_event);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pastEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(Events.this, Events.class);
                intent.putExtra("flag", "two");
                startActivity(intent);
            }
        });

        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(Events.this, Events.class);
                intent.putExtra("flag", "three");
                startActivity(intent);
            }
        });

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Events.this,Create_event.class);
                intent.putExtra("name", "");
                intent.putExtra("desc", "");
                intent.putExtra("note", "");
                intent.putExtra("location", "");
                intent.putExtra("uuid", "");
                startActivity(intent);
            }
        });

    }

    private void getFirestoreEvents(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventCollection = db.collection("Events");
        //CollectionReference eventRef = db.collection("Events");
        eventCollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(Objects.equals(Events.flag, "one") || Objects.equals(Events.flag, "two")) {

                                    Date currentDate = new Date();
                                    System.out.println("element");

                                    // Define a date format to parse the event date
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                    String d = document.getString("date");

                                    try {
                                        Date eventDate = dateFormat.parse(d);

                                        if (Objects.equals(Events.flag, "one") && eventDate.after(currentDate)) {
                                            event_data.add(document.getData());
                                        } else {
                                            System.out.println("not");
                                        }
                                        if (Objects.equals(Events.flag, "two") && !eventDate.after(currentDate)) {
                                            event_data.add(document.getData());
                                        }
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {
                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    String userId = user.getUid();
                                    String x = document.getString("creator");
                                    if(Objects.equals(x, userId)) {
                                        event_data.add(document.getData());
                                    }

                                }
                            }

                            setupeventmodels();

                        } else {
                            System.out.println("Error getting document\n");
                        }

                    }
                });

    }

    private void setupeventmodels(){

        for (Map<String,Object> map: this.event_data){

            String title = map.get("title").toString();
            String location = map.get("location").toString();
            String date = map.get("date").toString();
            String uuid = map.get("uuid").toString();
            String desc = map.get("description").toString();
            String note = map.get("note").toString();
            eventmodels.add(new eventmodel(title,date,location, uuid, desc, note));
        }
        rVadapter.notifyDataSetChanged();
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent =new Intent(Events.this,Event_Details.class);

        intent.putExtra("title",eventmodels.get(position).getTitle());
        intent.putExtra("place",eventmodels.get(position).getPlace());
        intent.putExtra("time",eventmodels.get(position).getTime());
        intent.putExtra("uuid",eventmodels.get(position).uuid);
        intent.putExtra("desc",eventmodels.get(position).desc);
        intent.putExtra("note",eventmodels.get(position).note);
        startActivity(intent);
    }
}


