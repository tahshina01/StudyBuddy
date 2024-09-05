package com.example.frenbot;

import static com.example.frenbot.Constants.TOPIC;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Create_event extends AppCompatActivity {
    TextInputEditText event_name, desc,note,location;
    Button date, create;
    private int year, month,day;
    private int event_year=0, event_month=0, event_day=0;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);

        event_name = findViewById(R.id.EventName);
        desc = findViewById(R.id.Description);
        note = findViewById(R.id.Note);
        date = findViewById(R.id.Date);
        location = findViewById(R.id.location);
        create = findViewById(R.id.Create_event);

        db = FirebaseFirestore.getInstance();
        String nam = getIntent().getStringExtra("name");
        String des = getIntent().getStringExtra("desc");
        String not = getIntent().getStringExtra("note");
        String lock = getIntent().getStringExtra("location");
        String uid = getIntent().getStringExtra("uuid");

        event_name.setText(nam);
        desc.setText(des);
        note.setText(not);
        location.setText(lock);


        Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        if(!Objects.equals(uid, "")) {
            create.setText("Update");
        }

        create.setOnClickListener(new View.OnClickListener() {

            String event_nam, event_des , event_loc , event_note;
            @Override
            public void onClick(View v) {
                event_nam = event_name.getText().toString();
                event_des = desc.getText().toString();
                event_loc = location.getText().toString();
                event_note = note.getText().toString();

                Map<String, Object> events = new HashMap<>();
                events.put("title",event_nam);
                events.put("description",event_des);
                events.put("location",event_loc);
                events.put("note",event_note);
                events.put("date", String.format(Locale.US, "%02d-%02d-%04d", event_day, event_month+1, event_year));

                events.put("creator",user.getUid());
                events.put("day", day);
                events.put("month", month);
                events.put("year", year);


                if (!event_nam.isEmpty() && !event_des.isEmpty()
                        && !event_loc.isEmpty() && event_month!=0){


                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE,event_nam);
                    intent.putExtra(CalendarContract.Events.DESCRIPTION,event_des);
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION,event_loc);
                    intent.putExtra(CalendarContract.Events.ALL_DAY,true);


                    if (intent.resolveActivity(getPackageManager())!=null){
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Create_event.this, "No calender app", Toast.LENGTH_SHORT).show();
                    }

                    CollectionReference eventCollection = db.collection("Events");
                    String courseId;
                    if(Objects.equals(uid, "")) {
                        courseId = UUID.randomUUID().toString();
                    } else {
                        courseId = uid;
                    }
                    events.put("uuid", courseId);
                    DocumentReference eventDoc = eventCollection.document(courseId);

                    eventDoc
                            .set(events).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // User data has been successfully added.

                                    Intent intent = new Intent(Create_event.this, Events.class);
                                    intent.putExtra("flag", "one");
                                    startActivity(intent);

                                    Intent intent2 = new Intent();
                                    setResult(RESULT_OK, intent2);

                                    Constants.intentFlag = "event";
                                    PushNotification pushNotification = new PushNotification(new NotificationData(event_nam + "(New Event)", event_des), TOPIC);
                                    sendNotification(pushNotification);

                                    finish();
                                }
                            });



                }
                else {
                    Toast.makeText(Create_event.this, "Please fill the necessary fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void openDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                event_year = year;
                event_month = month;
                event_day = dayOfMonth;
            }
        },year,month,day);
        dialog.show();
    }

    private void sendNotification(PushNotification notification) {
        PushNotification individualNotification = new PushNotification(notification.getData());
        individualNotification.setTo(TOPIC);

        ApiUtilities.getClient().sendNotification(individualNotification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if (response.isSuccessful()) {
                    // Notification sent successfully to the target user

                } else {
                    // Handle the case where the notification failed to send
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                // Handle failure to send the notification
            }
        });
        // Notify the RecyclerView to refresh

    }
}