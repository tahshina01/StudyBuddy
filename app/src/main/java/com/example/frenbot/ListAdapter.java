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
import java.util.stream.Collectors;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    List<ListItem> listItems;

    public ListAdapter(List<ListItem> listItems) {
        this.listItems = listItems;
        fetchDataFromFirestore();
    }

    public ListAdapter(ListClass courseFiles, List<ListItem> fileItems, ListClass courseFiles1) {

    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ListItem item = listItems.get(position);
        holder.titleTextView.setText(item.name);
        if (!Objects.equals(listItems.get(position).profilePic, "") && listItems.get(position).profilePic != null) {
            Picasso.get().load(listItems.get(position).profilePic).into(holder.icon);
        }

        // Set an onClickListener to open the link in a browser

        holder.itemView.setOnClickListener(v -> {
            if(NoticeGroup.flag == 3) {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference users = db.collection("Users");
                CollectionReference noticeCollection = db.collection("Notice");
                DocumentReference noticeGp = noticeCollection.document(NoticeGroup.staticGroup);
                CollectionReference allMember = noticeGp.collection("Member");
                Map<String, String> userData = new HashMap<>();
                userData.put("userId", listItems.get(position).userId );
                userData.put("name", listItems.get(position).name);
                userData.put("profilePic", listItems.get(position).profilePic);
                DocumentReference newDoc = allMember.document(listItems.get(position).userId);

                newDoc.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DocumentReference userDoc = users.document(listItems.get(position).getUserId());
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
                                                listItems.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        });
                            }
                        });

            } else if(NoticeGroup.flag == 2) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference users = db.collection("Users");
                CollectionReference noticeCollection = db.collection("Notice");
                DocumentReference noticeGp = noticeCollection.document(NoticeGroup.staticGroup);
                CollectionReference allMember = noticeGp.collection("Member");
                Map<String, String> userData = new HashMap<>();
                userData.put("userId", listItems.get(position).userId );
                userData.put("name", listItems.get(position).name);
                userData.put("profilePic", listItems.get(position).profilePic);
                DocumentReference newDoc = allMember.document(listItems.get(position).userId);

                newDoc.set(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DocumentReference userDoc = users.document(listItems.get(position).getUserId());
                                CollectionReference userNotice = userDoc.collection("NoticeGroup");
                                Map<String, String> groupData = new HashMap<>();
                                groupData.put("admin", NoticeGroup.gpAdmin);
                                groupData.put("groupName", NoticeGroup.gpName);
                                groupData.put("uuid", NoticeGroup.staticGroup);
                                DocumentReference groupDoc = userNotice.document(NoticeGroup.staticGroup);

                                groupDoc.set(groupData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                listItems.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private void fetchDataFromFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference users = db.collection("Users");
            CollectionReference noticeCollection = db.collection("Notice");
            DocumentReference noticeGp = noticeCollection.document(NoticeGroup.staticGroup);
            CollectionReference allMember = noticeGp.collection("Member");
            if(NoticeGroup.flag == 1 || NoticeGroup.flag == 3) {
                allMember.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                listItems.clear(); // Clear existing data before adding new data
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String name = document.getString("name");
                                    String profilePic = document.getString("profilePic");
                                    String uuid = document.getString("userId");
                                    if(!Objects.equals(uuid, NoticeGroup.gpAdmin)) {
                                        ListItem fileItem = new ListItem(uuid, name, profilePic);
                                        listItems.add(fileItem);
                                        notifyDataSetChanged();
                                    }
                                }
                                // Notify the RecyclerView to refresh
                            } else {
                                // Handle the error
                            }
                        });
                notifyDataSetChanged();
            }
            else {
                users.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Clear existing data before adding new data
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String name = document.getString("name");
                                    String profilePic = document.getString("profilePic");
                                    String uuid = document.getString("userId");
                                    System.out.println(uuid);
                                    if(!Objects.equals(uuid, NoticeGroup.gpAdmin)) {
                                        ListItem fileItem = new ListItem(uuid, name, profilePic);
                                        listItems.add(fileItem);
                                        notifyDataSetChanged();
                                    }
                                }
                                // Notify the RecyclerView to refresh
                                System.out.println("Outside loop");
                            }
                            System.out.println("outside if");

                        });
                notifyDataSetChanged();
            }
        }

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
