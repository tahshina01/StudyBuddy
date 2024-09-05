package com.example.frenbot;

import android.annotation.SuppressLint;
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

public class Notice extends AppCompatActivity implements RCViewInterface {

    FloatingActionButton add;
    ArrayList<GroupItem> groupItems = new ArrayList<>();
    private static final int finish_code = 436;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ImageView back=findViewById(R.id.back);
        add=findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a file picker dialog to allow the user to select files
                Intent intent = new Intent(Notice.this, CreateGroup.class);
                startActivityForResult(intent, finish_code);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Find the RecyclerView and set its adapter
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.rcview);
        GroupAdapter rVadapter =new GroupAdapter(this,groupItems,this);
        recyclerView.setAdapter(rVadapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == finish_code) {
            if (resultCode == RESULT_OK) {
                // The child activity has finished, so finish the parent activity
                finish();
            }
        }
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent =new Intent(Notice.this,NoticeGroup.class);
        intent.putExtra("groupName", groupItems.get(position).groupName);
        intent.putExtra("adminId", groupItems.get(position).adminID);
        intent.putExtra("groupId", groupItems.get(position).groupID);
        startActivityForResult(intent, finish_code);
    }
}
