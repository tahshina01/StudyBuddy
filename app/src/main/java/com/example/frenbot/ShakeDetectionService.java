package com.example.frenbot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ShakeDetectionService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private boolean isNotFirstTime = false;
    private float shakeThreshold = 30f;

    private LocationManager locationManager;
    private static String longitude = "", latitude = "";

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize and configure the sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Register the sensor listener
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }

        startForeground(1, createNotification());

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                ShakeDetectionService.longitude = String.valueOf(location.getLongitude());
                ShakeDetectionService.latitude = String.valueOf(location.getLatitude());
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        startForegroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if(isNotFirstTime) {
            float xDiff = Math.abs(currentX - lastX);
            float yDiff = Math.abs(currentY - lastY);
            float zDiff = Math.abs(currentZ - lastZ);

            if((xDiff > shakeThreshold && yDiff > shakeThreshold) || (xDiff > shakeThreshold && zDiff > shakeThreshold) || (zDiff > shakeThreshold && yDiff > shakeThreshold)) {
                // Get the current user's UID from Firebase Authentication
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    // Reference to the "sosInfo" collection
                    CollectionReference sosInfoCollection = FirebaseFirestore.getInstance().collection("SosInfo");

                    // Query the collection for the document with the user's UID
                    sosInfoCollection.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Document exists, you can retrieve the data here
                                    String message = document.getString("message");
                                    String messageNumber = document.getString("messageNumber");
                                    String callNumber = document.getString("callNumber");

                                    System.out.println("printing sos");
                                    System.out.println(message);
                                    System.out.println(messageNumber);
                                    System.out.println(callNumber);

//                                    Toast.makeText(ShakeDetectionService.this,"shake detected",Toast.LENGTH_SHORT).show();
                                    System.out.println("shake detected");

                                    // Check if messageNumber and callNumber are not empty
                                    if (!(Objects.equals(messageNumber, "")) && !(Objects.equals(callNumber, ""))) {
                                        sendSms(messageNumber, message);
                                        makeCall(callNumber);
                                    } else if(!(Objects.equals(messageNumber, ""))){
                                        sendSms(messageNumber, message);
                                    } else if(!(Objects.equals(callNumber, ""))) {
                                        makeCall(callNumber);
                                    }

                                    // Do something with the retrieved data (e.g., display it)
                                } else {
                                    // Document does not exist for this user
                                    // You can handle this case as needed
                                    System.out.println("Document does not exist for this user");
                                }
                            } else {
                                // Handle errors
                                Exception e = task.getException();
                                if (e != null) {
                                    // Log or display the error
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        isNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            if(ShakeDetectionService.longitude != null && ShakeDetectionService.latitude != null) {
                String mapLink = "http://maps.google.com/maps?q=" + ShakeDetectionService.latitude  + "," + ShakeDetectionService.longitude;
                message = message + "\n" + mapLink;
            } else {
                Toast.makeText(this, "null location", Toast.LENGTH_SHORT).show();
            }

            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void makeCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Call initiation failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

//    private Notification createNotification() {
//        Intent notificationIntent = new Intent(this, Sos.class); // Replace YourActivity with your desired activity.
//
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//        // Create and configure your notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sos_channel")
//                .setContentTitle("Sos Service Channel")
//                .setContentText("Running in the background")
//                .setSmallIcon(R.drawable.notification)
//                .setContentIntent(pendingIntent);
//
//        // You can customize the notification further if needed
//
//        return builder.build();
//    }
    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "sos_channel";
            String channelName = "SOS Channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, Sos.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sos_channel")
                .setContentTitle("SOS Service")
                .setContentText("Running in the background")
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

}
