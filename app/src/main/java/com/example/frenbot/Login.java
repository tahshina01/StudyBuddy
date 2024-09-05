package com.example.frenbot;

import static com.example.frenbot.Constants.TOPIC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.privacysandbox.ads.adservices.topics.Topic;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {

    Button signup,signin;
    FloatingActionButton fb,google,linkedin;
    TextInputEditText mail, password;
    LinearLayout ll;
    FirebaseAuth mAuth;
    float v=0;

    public static String Auth_Pref = "current_user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signup=findViewById(R.id.signup);
        fb = findViewById(R.id.fb);
        google = findViewById(R.id.google);
        linkedin = findViewById(R.id.linkedin);
        ll=findViewById(R.id.ll);
        signin = findViewById(R.id.signin);
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
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        Button forgetPass = findViewById(R.id.forgotPass);
        forgetPass.setVisibility(View.GONE);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pass;
                email = String.valueOf(mail.getText());
                pass = String.valueOf(password.getText());

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this,"Enter email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    Toast.makeText(Login.this,"Enter password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    SharedPreferences sharedPreferences = getSharedPreferences(Auth_Pref, 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn",true);
                                    editor.commit();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + user.getUid());
                                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

                                    Toast.makeText(Login.this, "Login successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, Navigation.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Login.this,"Logging in using facebook...",Toast.LENGTH_SHORT).show();
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this,"Logging in using google...",Toast.LENGTH_SHORT).show();
            }
        });
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this,"Logging in using linkedin...",Toast.LENGTH_SHORT).show();
            }
        });
    }
}