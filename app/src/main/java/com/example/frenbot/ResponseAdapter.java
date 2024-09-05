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

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.MyViewHolder> {

    List<ListItem> listItems;

    public ResponseAdapter(List<ListItem> listItems) {
        this.listItems = listItems;
        fetchDataFromFirestore();
    }

    public ResponseAdapter(ResponseList responseList, List<ListItem> listItems, ResponseList responseList1) {
    }

    @NonNull
    @Override
    public ResponseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ResponseAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ListItem item = listItems.get(position);
        holder.titleTextView.setText(item.name);
        if (!Objects.equals(listItems.get(position).profilePic, "") && listItems.get(position).profilePic != null) {
            Picasso.get().load(listItems.get(position).profilePic).into(holder.icon);
        }

        // Set an onClickListener to open the link in a browser

        holder.itemView.setOnClickListener(v -> {

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
            CollectionReference eventCollection = db.collection("Events");
            DocumentReference noticeGp = eventCollection.document(Event_Details.selectedEvent);
            CollectionReference allMember = noticeGp.collection("Interested");
            allMember.get()
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