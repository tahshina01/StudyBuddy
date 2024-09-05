package com.example.frenbot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Sos extends AppCompatActivity {

    static final int CONTACT_PICKER_REQUEST_MESSAGE = 1;
    static final int CONTACT_PICKER_REQUEST_CALL = 2;
    static final int PERMISSION_REQUEST_CODE = 123;

    EditText messageNumberEditText;
    EditText callNumberEditText;
    EditText messageEditText;
    FirebaseAuth mAuth;
    Button toggleServiceButton;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        messageNumberEditText = findViewById(R.id.messageNumberEditText);
        callNumberEditText = findViewById(R.id.callNumberEditText);
        messageEditText = findViewById(R.id.messageEditText);
        mAuth = FirebaseAuth.getInstance();
        toggleServiceButton = findViewById(R.id.toggleServiceButton);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkAndRequestPermissions();
        if (isServiceRunning(ShakeDetectionService.class)) {
            toggleServiceButton.setText("Stop service");
        } else {
            toggleServiceButton.setText("Start service");
        }

        @SuppressLint("WrongViewCast") ImageView selectMessageNumberButton = findViewById(R.id.selectMessageNumberButton);
        selectMessageNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactsForMessage(CONTACT_PICKER_REQUEST_MESSAGE);
            }
        });

        @SuppressLint("WrongViewCast") ImageView selectCallNumberButton = findViewById(R.id.selectCallNumberButton);
        selectCallNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactsForMessage(CONTACT_PICKER_REQUEST_CALL);
            }
        });
    }

    private void openContactsForMessage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_REQUEST_MESSAGE) {
                handleContactPickResult(data, messageNumberEditText);
            } else if (requestCode == CONTACT_PICKER_REQUEST_CALL) {
                handleContactPickResult(data, callNumberEditText);
            }
        }
    }

    private void handleContactPickResult(Intent data, EditText editText) {
        Uri contactUri = data.getData();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phoneNumber = cursor.getString(numberColumnIndex);
            editText.setText(phoneNumber);
            cursor.close();
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE},
                    PERMISSION_REQUEST_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    public void saveData(View view) {
        System.out.println("calling the handler");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference sosCollection = db.collection("SosInfo");

            DocumentReference sosDocument = sosCollection.document(userId);

            // Store user data in the document.
            SOSInfo sosInfo = new SOSInfo(messageEditText.getText().toString(), messageNumberEditText.getText().toString(), callNumberEditText.getText().toString());

            Map<String, Object> sosInfoMap = new HashMap<>();
            sosInfoMap.put("message", sosInfo.getMessage());
            sosInfoMap.put("messageNumber", sosInfo.getMessageNumber());
            sosInfoMap.put("callNumber", sosInfo.getCallNumber());
            // Add other user-related data as needed.

            // Set the data in the document.
            sosDocument.set(sosInfoMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // User data has been successfully added.
                            Toast.makeText(Sos.this, "database updated", Toast.LENGTH_SHORT).show();
                            messageNumberEditText.setText("");
                            messageEditText.setText("");
                            callNumberEditText.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error.
                            Toast.makeText(Sos.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @SuppressLint("SetTextI18n")
    public void toggleShakeService(View view) {
        if (isServiceRunning(ShakeDetectionService.class)) {
            toggleServiceButton.setText("Start service");
            // Service is running, stop it
            Intent serviceIntent = new Intent(this, ShakeDetectionService.class);
            stopService(serviceIntent);
        } else {
            toggleServiceButton.setText("Stop service");
            // Service is not running, start it
            Intent serviceIntent = new Intent(this, ShakeDetectionService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

}

class SOSInfo {
    String message; String messageNumber; String callNumber;
    public SOSInfo(String message, String messageNumber, String callNumber) {
        this.message = message;
        this.messageNumber = messageNumber;
        this.callNumber = callNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageNumber() {
        return messageNumber;
    }

    public String getCallNumber() {
        return callNumber;
    }

}
