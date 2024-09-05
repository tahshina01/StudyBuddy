package com.example.frenbot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {

    private List<FileItem> fileItems;

    public FileAdapter(List<FileItem> fileItems) {
        this.fileItems = fileItems;
        fetchDataFromFirestore();
    }

    public FileAdapter(Course_Files courseFiles, List<FileItem> fileItems, Course_Files courseFiles1) {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FileItem item = fileItems.get(position);
        holder.titleTextView.setText(item.getTitle());

        // Set an onClickListener to open the link in a browser
        holder.itemView.setOnClickListener(v -> {
//            String url = item.getLink();
//            openLinkInBrowser(v.getContext(), url);
            if (item.getFileType() != null) {
                switch (item.getFileType()) {
                    case "pdf":
                        openPdfFile(v.getContext(), item.getDownloadUri());
                        break;
                    case "txt":
                        openTextFile(v.getContext(), item.getDownloadUri());
                        break;
                    case "img":
                        openImageFile(v.getContext(), item.getDownloadUri());
                        break;
                    case "doc":
                        openDocFile(v.getContext(), item.getDownloadUri()); // Set the image icon
                        break;
                }
            }
        });

        // Set an onClickListener for the delete button
        holder.delete.setOnClickListener(v -> {
            deleteItem(position);
        });

        if (item.getFileType() != null) {
            switch (item.getFileType()) {
                case "pdf":
                    holder.icon.setImageResource(R.drawable.pdf); // Set the PDF icon
                    break;
                case "txt":
                    holder.icon.setImageResource(R.drawable.txt); // Set the TXT icon
                    break;
                case "img":
                    holder.icon.setImageResource(R.drawable.image); // Set the image icon
                    break;
                case "doc":
                    holder.icon.setImageResource(R.drawable.doc); // Set the image icon
                    break;
            }
        }
    }

    private void openPdfFile(Context context, String fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fileUri), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where a PDF viewer app is not installed
            // You can prompt the user to install a PDF viewer or provide an alternative action
        }
    }

    private void openImageFile(Context context, String fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fileUri), "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where an image viewer app is not installed
            // You can prompt the user to install an image viewer or provide an alternative action
        }
    }

    private void openTextFile(Context context, String fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fileUri), "text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where a text file viewer app is not installed
            // You can prompt the user to install a text file viewer or provide an alternative action
        }
    }

    private void openDocFile(Context context, String fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fileUri), "application/msword");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where a DOC file viewer app is not installed
            // You can prompt the user to install a DOC file viewer or provide an alternative action
        }
    }



    @Override
    public int getItemCount() {
        return fileItems.size();
    }

    private void fetchDataFromFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(userId);
            CollectionReference coursesCollection = userDocument.collection("Course");
            DocumentReference courseDocument = coursesCollection.document(Course_Details.courseUUID);
            CollectionReference courseFileCollection = courseDocument.collection("courseFileCollection");

            courseFileCollection.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            fileItems.clear(); // Clear existing data before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("title");
                                String downloadUri = document.getString("downloadUri");
                                String uuid = document.getString("uuid");
                                String fileType = document.getString("fileType");

                                FileItem fileItem = new FileItem(title,downloadUri,uuid,fileType);
                                fileItems.add(fileItem);
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
            CollectionReference courseFileCollection = courseDocument.collection("courseFileCollection");
            DocumentReference fileDocument = courseFileCollection.document(fileItems.get(position).getUuid());
            System.out.println(fileDocument);

            fileDocument.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Document was successfully deleted
                            fileItems.remove(position);
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
        ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.fileTitleTextView);
            delete = itemView.findViewById(R.id.delete); // Add your delete button id
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
