package com.example.frenbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShareList extends AppCompatActivity {

    public static String title, courseId, ins, uuId, desc;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ImageView back=findViewById(R.id.back);
        title = getIntent().getStringExtra("title");
        courseId = getIntent().getStringExtra("id");
        ins = getIntent().getStringExtra("instructor");
        uuId = getIntent().getStringExtra("uuid");
        desc = getIntent().getStringExtra("desc");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        List<ListItem> listItems = new ArrayList<>();

        // Find the RecyclerView and set its adapter
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShareAdapter rVadapter =new ShareAdapter(this,listItems,this);
        recyclerView.setAdapter(rVadapter);
        recyclerView.setAdapter(new ShareAdapter(listItems));
    }
}
