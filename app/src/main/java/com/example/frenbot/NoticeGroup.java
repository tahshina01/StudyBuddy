package com.example.frenbot;

import static com.example.frenbot.Constants.TOPIC;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeGroup extends AppCompatActivity implements RCViewInterface{

    public static String staticGroup;
    public static String gpName;
    public static String gpAdmin;
    ArrayList<MessageItem> messageItems = new ArrayList<>();
    Button cancelEdit;
    public static boolean isEdit;
    TextInputEditText message;
    public static String editTime;
    public static String editUUID;
    public static int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticegroup);

        LinearLayout ll2 = findViewById(R.id.ll2);
        TextView warning = findViewById(R.id.warning);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView name = findViewById(R.id.name);
        message = findViewById((R.id.message));
        cancelEdit = findViewById(R.id.cancelEdit);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        String groupName = getIntent().getStringExtra("groupName");
        String adminId = getIntent().getStringExtra("adminId");
        String groupId = getIntent().getStringExtra("groupId");
        NoticeGroup.staticGroup = groupId;
        NoticeGroup.gpName = groupName;
        NoticeGroup.gpAdmin = adminId;
        NoticeGroup.isEdit = false;

        name.setText(groupName);

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + user.getUid());


        if (user != null) {
            String userId = user.getUid();
            if(userId.equals(adminId)) {
                warning.setVisibility(View.GONE);
            } else {
                ll2.setVisibility(View.GONE);
            }
        }
        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEdit.setVisibility(View.GONE);
                message.setText("");
                NoticeGroup.isEdit = false;
            }
        });
        cancelEdit.setVisibility(View.GONE);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = String.valueOf(message.getText());
                if(text.equals("")) {
                    Toast.makeText(NoticeGroup.this, "Type message", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getDefault());

                // Get the current time
                Date currentTime = new Date();

                // Format the current time to a string
                String formattedTime = sdf.format(currentTime);
                if(NoticeGroup.isEdit) {
                    formattedTime = NoticeGroup.editTime;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference noticeCol = db.collection("Notice");
                Map<String, String> messageData = new HashMap<>();
                String uuid = UUID.randomUUID().toString();

                if(NoticeGroup.isEdit) {
                    uuid = NoticeGroup.editUUID;
                }
                messageData.put("uuid", uuid);
                messageData.put("message", text);
                messageData.put("time", formattedTime);

                message.setText("");
                cancelEdit.setVisibility(View.GONE);
                NoticeGroup.isEdit = false;

                DocumentReference noticeDocument = noticeCol.document(groupId);
                CollectionReference messageCollection = noticeDocument.collection("Message");

                DocumentReference newDocument = messageCollection.document(uuid);
                newDocument.set(messageData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document was successfully added to the courseLinkCollection
                                finish();
                                Intent intent = new Intent(NoticeGroup.this, NoticeGroup.class);
                                intent.putExtra("groupName", groupName);
                                intent.putExtra("adminId", adminId);
                                intent.putExtra("groupId", groupId);
                                startActivity(intent);

                                Constants.intentFlag = "notice";
                                PushNotification pushNotification = new PushNotification(new NotificationData("New Notice from " + groupName, text), TOPIC);
                                sendNotification(pushNotification);

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

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.recyclerView);
        MessageAdapter rVadapter =new MessageAdapter(this,messageItems,this);
        recyclerView.setAdapter(rVadapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        ImageView menuIcon;
        menuIcon = findViewById(R.id.menu);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a popup menu when the menu icon is clicked
                PopupMenu popupMenu = new PopupMenu(NoticeGroup.this, menuIcon);
                MenuInflater inflater = popupMenu.getMenuInflater();

                if (user != null) {
                    String userId = user.getUid();
                    if(userId.equals(adminId)) {
                        inflater.inflate(R.menu.admin_menu, popupMenu.getMenu());
                    } else {
                        inflater.inflate(R.menu.menu, popupMenu.getMenu());
                    }
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (user != null) {
                            String userId = user.getUid();
                            if(userId.equals(adminId)) {
                                if (item.getItemId() == R.id.all) {
                                    NoticeGroup.flag = 1;
                                    // Handle "all" item click
                                    Intent intent = new Intent(NoticeGroup.this, ListClass.class);
                                    startActivity(intent);
                                    return true;
                                } else if (item.getItemId() == R.id.add) {
                                    NoticeGroup.flag = 2;
                                    // Handle "leave" item click
                                    Intent intent = new Intent(NoticeGroup.this, ListClass.class);
                                    startActivity(intent);
                                    return true;
                                } else if (item.getItemId() == R.id.remove) {
                                    NoticeGroup.flag = 3;
                                    // Handle "all" item click
                                    Intent intent = new Intent(NoticeGroup.this, ListClass.class);
                                    startActivity(intent);
                                    return true;
                                } else if (item.getItemId() == R.id.delete) {
                                    // Handle "leave" item click
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference users = db.collection("Users");
                                    CollectionReference noticeCollection = db.collection("Notice");
                                    DocumentReference noticeGp = noticeCollection.document(NoticeGroup.staticGroup);
                                    CollectionReference allMember = noticeGp.collection("Member");
                                    DocumentReference newDoc = allMember.document(user.getUid());
                                    DocumentReference userDoc = users.document(user.getUid());
                                    CollectionReference userNotice = userDoc.collection("NoticeGroup");
                                    Map<String, String> groupData = new HashMap<>();
                                    groupData.put("admin", NoticeGroup.gpAdmin);
                                    groupData.put("groupName", NoticeGroup.gpName);
                                    groupData.put("uuid", NoticeGroup.staticGroup);
                                    DocumentReference groupDoc = userNotice.document(NoticeGroup.staticGroup);

                                    groupDoc.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(NoticeGroup.this, Notice.class);
                                                    startActivity(intent);

                                                    Intent intent2 = new Intent();
                                                    setResult(RESULT_OK, intent2);
                                                    finish();
                                                }
                                            });
                                }
                            } else {
                                inflater.inflate(R.menu.menu, popupMenu.getMenu());
                                if (item.getItemId() == R.id.all) {
                                    NoticeGroup.flag = 1;
                                    // Handle "all" item click
                                    Intent intent = new Intent(NoticeGroup.this, ListClass.class);
                                    startActivity(intent);
                                    return true;
                                } else if (item.getItemId() == R.id.leave) {
                                    // Handle "leave" item click
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference users = db.collection("Users");
                                    CollectionReference noticeCollection = db.collection("Notice");
                                    DocumentReference noticeGp = noticeCollection.document(NoticeGroup.staticGroup);
                                    CollectionReference allMember = noticeGp.collection("Member");
                                    DocumentReference newDoc = allMember.document(user.getUid());

                                    newDoc.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    DocumentReference userDoc = users.document(user.getUid());
                                                    CollectionReference userNotice = userDoc.collection("NoticeGroup");
                                                    Map<String, String> groupData = new HashMap<>();
                                                    groupData.put("admin", NoticeGroup.gpAdmin);
                                                    groupData.put("groupName", NoticeGroup.gpName);
                                                    groupData.put("uuid", NoticeGroup.staticGroup);
                                                    DocumentReference groupDoc = userNotice.document(NoticeGroup.staticGroup);

                                                    groupDoc.delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent intent = new Intent(NoticeGroup.this, Notice.class);
                                                                    startActivity(intent);

                                                                    Intent intent2 = new Intent();
                                                                    setResult(RESULT_OK, intent2);
                                                                    finish();
                                                                }
                                                            });
                                                }
                                            });
                                    return true;
                                }
                            }
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = MessageAdapter.MyViewHolder.position; // Get the clicked position
        System.out.println(messageItems.get(position).message);
        System.out.println(item);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference noticeCollection = db.collection("Notice");
        DocumentReference noticeGroup = noticeCollection.document(NoticeGroup.staticGroup);
        CollectionReference messageCollection = noticeGroup.collection("Message");
        DocumentReference messageData = messageCollection.document(messageItems.get(position).uuid);

        switch (item.getItemId()) {
            case 1: // Edit
                // Handle the "Edit" action here using the 'position'
                NoticeGroup.isEdit = true;
                message.setText(messageItems.get(position).message);
                cancelEdit.setVisibility(View.VISIBLE);
                NoticeGroup.editTime = messageItems.get(position).timeStamp;
                NoticeGroup.editUUID = messageItems.get(position).uuid;
                return true;

            case 2: // Delete
                // Handle the "Delete" action here using the 'position'
                messageData.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document was successfully deleted
                                finish();
                                Intent intent = new Intent(NoticeGroup.this, NoticeGroup.class);
                                intent.putExtra("groupName", NoticeGroup.gpName);
                                intent.putExtra("adminId", NoticeGroup.gpAdmin);
                                intent.putExtra("groupId", NoticeGroup.staticGroup);
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
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void sendNotification(PushNotification notification) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> temp = new ArrayList<>();
        CollectionReference noticeCollection = db.collection("Notice");
        DocumentReference noticeGp = noticeCollection.document(NoticeGroup.staticGroup);
        CollectionReference allMember = noticeGp.collection("Member");
        allMember.get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Clear existing data before adding new data
                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
                            String uuid = document1.getString("userId");
                            temp.add(uuid);
                        }
                        temp.add(NoticeGroup.gpAdmin);
                        for (int i = 0; i < temp.size(); i++) {

                                // Create a new instance of PushNotification for each target user
                                PushNotification individualNotification = new PushNotification(notification.getData());
                                individualNotification.setTo("/topics/" + temp.get(i));

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
        });

    }


}
