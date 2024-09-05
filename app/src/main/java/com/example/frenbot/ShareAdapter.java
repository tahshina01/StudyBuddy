package com.example.frenbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.MyViewHolder> {

    List<ListItem> listItems;

    public ShareAdapter(List<ListItem> listItems) {
        this.listItems = listItems;
        fetchDataFromFirestore();
    }

    public ShareAdapter(ShareList responseList, List<ListItem> listItems, ShareList responseList1) {
    }

    @NonNull
    @Override
    public ShareAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ShareAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ListItem item = listItems.get(position);
        holder.titleTextView.setText(item.name);
        if (!Objects.equals(listItems.get(position).profilePic, "") && listItems.get(position).profilePic != null) {
            Picasso.get().load(listItems.get(position).profilePic).into(holder.icon);
        }

        // Set an onClickListener to open the link in a browser

        holder.itemView.setOnClickListener(v -> {
            String courseId = UUID.randomUUID().toString();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            Map<String, Object> course = new HashMap<>();
            course.put("title", ShareList.title);
            course.put("description", ShareList.desc);
            course.put("instructor", ShareList.ins);
            course.put("id", ShareList.courseId);
            course.put("sharedBy", user.getUid());
            course.put("archive", false);
            course.put("uuid", courseId);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(listItems.get(position).getUserId());
            CollectionReference coursesCollection = userDocument.collection("Course");

            coursesCollection.document(courseId)
                    .set(course)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // User data has been successfully added.
                            String userId = user.getUid();

                            DocumentReference userDocument1 = db.collection("Users").document(userId);
                            CollectionReference coursesCollection2 = userDocument1.collection("Course");

                            DocumentReference courseDocument2 = coursesCollection2.document(ShareList.uuId);
                            CollectionReference courseLinkCollection = courseDocument2.collection("courseLinkCollection");
                            courseLinkCollection.get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String title = document.getString("title");
                                                String link = document.getString("link");
                                                String linkId = document.getString("linkId");
                                                Map<String, String> courseLink = new HashMap<>();
                                                courseLink.put("title", title);
                                                courseLink.put("link", link);
                                                courseLink.put("linkId", linkId);

                                                DocumentReference sharedRes = coursesCollection.document(courseId);
                                                CollectionReference sharedLinks = sharedRes.collection("courseLinkCollection");
                                                sharedLinks.document(linkId).set(courseLink)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        });
                                            }

                                            CollectionReference courseFileCollection = courseDocument2.collection("courseFileCollection");
                                            courseFileCollection.get()
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task2.getResult()) {
                                                                String ftitle = document.getString("title");
                                                                String downloadUri = document.getString("downloadUri");
                                                                String fuuid = document.getString("uuid");
                                                                String fileType = document.getString("fileType");

                                                                Map<String, String> courseFile = new HashMap<>();
                                                                courseFile.put("title", ftitle);
                                                                courseFile.put("downloadUri", downloadUri);
                                                                courseFile.put("uuid", fuuid);
                                                                courseFile.put("fileType", fileType);

                                                                DocumentReference sharedRes = coursesCollection.document(courseId);
                                                                CollectionReference sharedLinks = sharedRes.collection("courseFileCollection");
                                                                sharedLinks.document(fuuid).set(courseFile)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                            }
                                                                        });
                                                            }
                                                            listItems.remove(position);
                                                            notifyDataSetChanged();
                                                        }

                                                    });
                                        }
                                    });

                        }
                });
            });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private void fetchDataFromFirestore() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventCollection = db.collection("Users");
        eventCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listItems.clear(); // Clear existing data before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String profilePic = document.getString("profilePic");
                            String uuid = document.getString("userId");
                            ListItem fileItem = new ListItem(uuid, name, profilePic);
                            listItems.add(fileItem);
                            notifyDataSetChanged();
                        }
                        // Notify the RecyclerView to refresh
                    } else {
                        // Handle the error
                    }
                });
        notifyDataSetChanged();


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}