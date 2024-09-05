package com.example.frenbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private List<GroupItem> groupItems;
    private Context context;
    private RCViewInterface rcViewInterface;

//    public GroupAdapter(List<GroupItem> groupItems) {
//        this.groupItems = groupItems;
//        fetchDataFromFirestore();
//    }

    public GroupAdapter(Context context, ArrayList<GroupItem> groupItemArrayList, RCViewInterface rcViewInterface) {
        this.context = context;
        this.groupItems = groupItemArrayList;
        this.rcViewInterface=rcViewInterface;
        fetchDataFromFirestore(); // Fetch data from Firestore when the adapter is created
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.group_rview, parent, false);
        return new MyViewHolder(view,rcViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GroupItem item = groupItems.get(position);
        holder.title.setText(item.groupName);
        holder.duration.setText(item.adminName);
    }


    @Override
    public int getItemCount() {
        return groupItems.size();
    }

    private void fetchDataFromFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(userId);
            CollectionReference noticeCollection = userDocument.collection("NoticeGroup");

            noticeCollection.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            groupItems.clear(); // Clear existing data before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String groupId = document.getString("uuid");
                                String groupName = document.getString("groupName");
                                String adminId = document.getString("admin");

                                DocumentReference userDocument2 = db.collection("Users").document(adminId);

                                // Assuming you have a Firebase Firestore instance (db) and a valid DocumentReference (userDocument2)

                                userDocument2.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    // Access fields from the DocumentSnapshot
                                                    String userName = documentSnapshot.getString("name");
                                                    GroupItem groupItem = new GroupItem(groupId, groupName, adminId, userName);
                                                    System.out.println(groupItem);
                                                    // You can also perform further operations with the data
                                                    groupItems.add(groupItem);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                            notifyDataSetChanged(); // Notify the RecyclerView to refresh
                        } else {
                            // Handle the error
                        }
                    });
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;

        public MyViewHolder(@NonNull View itemView, RCViewInterface rcViewInterface) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            duration = itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(rcViewInterface!=null){
                        int pos=getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){
                            rcViewInterface.OnItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}