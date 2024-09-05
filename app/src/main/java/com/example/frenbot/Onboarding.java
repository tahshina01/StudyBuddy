package com.example.frenbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Onboarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dots;
    SliderAdapter sliderAdapter;
    TextView[] dot;
    Button startbtn;

    Button next;
    int curpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.slider);
        dots = findViewById(R.id.dots);
        sliderAdapter= new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        adddots(0);
        viewPager.addOnPageChangeListener(changeListener);
        startbtn=findViewById(R.id.startbtn);
        next=findViewById(R.id.next);

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip(view);
            }
        });
    }
    public void skip(View view){
        Intent homeIntent = new Intent(getApplicationContext(), Navigation.class);
        Intent loginIntent = new Intent(getApplicationContext(), Login.class);
        SharedPreferences sharedPreferences =  getSharedPreferences(Login.Auth_Pref, 0);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if(!isLoggedIn) {
            startActivity(loginIntent);
        }else {
            startActivity(homeIntent);
        }
        finish();
    }
    public void next(View view){

        viewPager.setCurrentItem(curpos+1);

    }

    private void adddots(int position){

        dot = new TextView[4];
        dots.removeAllViews();
        for(int i=0;i<dot.length;i++){
            dot[i] =new TextView(this);
            dot[i].setText(Html.fromHtml("&#8226"));
            dot[i].setTextSize(35);

            dots.addView(dot[i]);
        }
        if(dot.length>0){
            dot[position].setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        }
    }

    ViewPager.OnPageChangeListener changeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            adddots(position);
            curpos=position;

            if(position==0){

                startbtn.setVisibility(View.INVISIBLE);

            } else if (position==1) {
                startbtn.setVisibility(View.INVISIBLE);

            } else if (position==2) {
                startbtn.setVisibility(View.INVISIBLE);

            }
            else{
                startbtn.setVisibility(View.VISIBLE);

            }
            if(position == 3) {
                next.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}