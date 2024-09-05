package com.example.frenbot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.List;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.MyViewHolder> {

    private List<LinkItem> linkItems;

    public LinkAdapter(List<LinkItem> linkItems) {
        this.linkItems = linkItems;
        fetchDataFromFirestore();
    }

    public LinkAdapter(Course_Links courseLinks, List<LinkItem> linkItems, Course_Links courseLinks1) {

    }

//    @NonNull
//    @Override
//    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_link, parent, false);
//        return new LinkViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
//        LinkItem item = linkItems.get(position);
//        holder.titleTextView.setText(item.getTitle());
//
//        // Set an onClickListener to open the link in a browser
//        holder.itemView.setOnClickListener(v -> {
//            String url = item.getLink();
//            openLinkInBrowser(v.getContext(), url);
//        });
//    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_link, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LinkItem item = linkItems.get(position);
        holder.titleTextView.setText(item.getTitle());

        // Set an onClickListener to open the link in a browser
        holder.itemView.setOnClickListener(v -> {
            String url = item.getLink();
            openLinkInBrowser(v.getContext(), url);
        });

        // Set an onClickListener for the delete button
        holder.delete.setOnClickListener(v -> {
            deleteItem(position);
        });
    }

    private void openLinkInBrowser(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    @Override
    public int getItemCount() {
        return linkItems.size();
    }

//    public static class LinkViewHolder extends RecyclerView.ViewHolder {
//        TextView titleTextView;
//
//        public LinkViewHolder(@NonNull View itemView) {
//            super(itemView);
//            titleTextView = itemView.findViewById(R.id.linkTitleTextView);
//        }
//    }

    private void fetchDataFromFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(userId);
            CollectionReference coursesCollection = userDocument.collection("Course");
            DocumentReference courseDocument = coursesCollection.document(Course_Details.courseUUID);
            CollectionReference courseLinkCollection = courseDocument.collection("courseLinkCollection");

            courseLinkCollection.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            linkItems.clear(); // Clear existing data before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("title");
                                String link = document.getString("link");
                                String linkId = document.getString("linkId");

                                LinkItem linkItem = new LinkItem(title,link,linkId);
                                linkItems.add(linkItem);
                            }
                            notifyDataSetChanged(); // Notify the RecyclerView to refresh
                        } else {
                            // Handle the error
                        }
                    });
        }
    }


    public void deleteItem(int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(userId);
            CollectionReference coursesCollection = userDocument.collection("Course");
            DocumentReference courseDocument = coursesCollection.document(Course_Details.courseUUID);
            System.out.println(Course_Details.courseUUID);
            CollectionReference courseLinkCollection = courseDocument.collection("courseLinkCollection");
            System.out.println(linkItems.get(position).getLinkId());
            System.out.println(linkItems.get(position).getLink());
            DocumentReference linkDocument = courseLinkCollection.document(linkItems.get(position).getLinkId());
            System.out.println(linkDocument);

            linkDocument.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Document was successfully deleted
                            linkItems.remove(position);
                            notifyItemRemoved(position);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                        }
                    });
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView delete; // Add a delete button

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.linkTitleTextView);
            delete = itemView.findViewById(R.id.delete); // Add your delete button id
        }
    }
}

