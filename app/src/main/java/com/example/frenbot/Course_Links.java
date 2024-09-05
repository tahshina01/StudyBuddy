package com.example.frenbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Course_Links extends AppCompatActivity {
    FloatingActionButton addLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_links);
        ImageView back=findViewById(R.id.back);
        String uuid = getIntent().getStringExtra("uuid");
        addLink=findViewById(R.id.add_Link);
        addLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Course_Links.this, AddCourseLink.class);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        List<LinkItem> linkItems = new ArrayList<>();

        // Find the RecyclerView and set its adapter
        RecyclerView recyclerViewLinks = findViewById(R.id.recyclerViewLinks);
        recyclerViewLinks.setLayoutManager(new LinearLayoutManager(this));
        LinkAdapter rVadapter =new LinkAdapter(this,linkItems,this);
        recyclerViewLinks.setAdapter(rVadapter);
        recyclerViewLinks.setAdapter(new LinkAdapter(linkItems));
    }
}
