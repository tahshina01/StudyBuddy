package com.example.frenbot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    Button login,signup;
    FloatingActionButton fb,google,linkedin;
    LinearLayout ll;
    TextInputEditText mail, username, password, cnPassword;
    FirebaseAuth mAuth;
    float v=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        login = findViewById(R.id.login);
        fb = findViewById(R.id.fb);
        google = findViewById(R.id.google);
        linkedin = findViewById(R.id.linkedin);
        ll = findViewById(R.id.ll);
        signup = findViewById(R.id.signup);
        mail = findViewById(R.id.mail);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        cnPassword = findViewById(R.id.cnPassword);
        mAuth = FirebaseAuth.getInstance();

        fb.setTranslationY(300);
        google.setTranslationY(300);
        linkedin.setTranslationY(300);
        ll.setTranslationX(300);
        fb.setAlpha(v);
        google.setAlpha(v);
        linkedin.setAlpha(v);
        ll.setAlpha(v);
        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        linkedin.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        ll.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, userName, pass, confirmPass;
                email = String.valueOf(mail.getText());
                userName = String.valueOf(username.getText());
                pass = String.valueOf(password.getText());
                confirmPass = String.valueOf(cnPassword.getText());

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(Signup.this,"Enter email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(userName)) {
                    Toast.makeText(Signup.this,"Enter user name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    Toast.makeText(Signup.this,"Enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPass)) {
                    Toast.makeText(Signup.this,"Enter confirm password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pass.equals(confirmPass)) {
                    Toast.makeText(Signup.this,"Invalid confirm password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        CollectionReference usersCollection = db.collection("Users");

                                        DocumentReference userDocument = usersCollection.document(userId);

                                        // Store user data in the document.
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("name", userName);
                                        userData.put("email", email);
                                        userData.put("dob", "");
                                        userData.put("phone", "");
                                        userData.put("varsity", "");
                                        userData.put("dept", "");
                                        userData.put("session", "");
                                        userData.put("profilePic", "");
                                        userData.put("userId", userId);
                                        // Add other user-related data as needed.

                                        // Set the data in the document.
                                        userDocument.set(userData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // User data has been successfully added.
                                                        Toast.makeText(Signup.this, "Account created and database updated.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Signup.this, Navigation.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle the error.
                                                        Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e("DatabaseUpdate", "Failed to update database: " + task.getException());
                                                    }

                                                });



                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("UserCreation", "Failed to create user: " + task.getException());
                                }
                            }
                        });

            }
        });
        fb.setVisibility(View.GONE);
        google.setVisibility(View.GONE);
        linkedin.setVisibility(View.GONE);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Signup.this,"Logging in using facebook...",Toast.LENGTH_SHORT).show();
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Signup.this,"Logging in using google...",Toast.LENGTH_SHORT).show();
            }
        });
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Signup.this,"Logging in using linkedin...",Toast.LENGTH_SHORT).show();
            }
        });
    }
}