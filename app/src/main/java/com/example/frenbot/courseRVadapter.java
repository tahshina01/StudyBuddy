package com.example.frenbot;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import android.content.Context;
//
//public class courseRVadapter extends RecyclerView.Adapter<courseRVadapter.MyViewHolder> {
//
//    Context context;
//    ArrayList<coursemodel> coursemodels;
//
//    public courseRVadapter(Context context, ArrayList<coursemodel> coursemodels){
//        this.context=context;
//        this.coursemodels=coursemodels;
//    }
//    @NonNull
//    @Override
//    public courseRVadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater= LayoutInflater.from(context);
//        View view=inflater.inflate(R.layout.courses_rview,parent,false);
//
//        return new courseRVadapter.MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull courseRVadapter.MyViewHolder holder, int position) {
//
//        holder.course.setText(coursemodels.get(position).getcourse());
//        holder.id.setText(coursemodels.get(position).getid());
//        holder.instructor.setText(coursemodels.get(position).getinstructor());
//    }
//
//    @Override
//    public int getItemCount() {
//        return coursemodels.size();
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//
//        TextView course,id,instructor;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            course=itemView.findViewById(R.id.course);
//            id=itemView.findViewById(R.id.id);
//            instructor=itemView.findViewById(R.id.instructor);
//        }
//    }
//}

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class courseRVadapter extends RecyclerView.Adapter<courseRVadapter.MyViewHolder> {

    private final RCViewInterface rcViewInterface;
    private Context context;
    static ArrayList<coursemodel> coursemodels;

    public courseRVadapter(Context context, ArrayList<coursemodel> coursemodels,RCViewInterface rcViewInterface) {
        this.context = context;
        this.coursemodels = coursemodels;
        this.rcViewInterface=rcViewInterface;
        fetchDataFromFirestore(); // Fetch data from Firestore when the adapter is created
    }

    private void fetchDataFromFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocument = db.collection("Users").document(userId);
            CollectionReference coursesCollection = userDocument.collection("Course");

            coursesCollection.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            coursemodels.clear(); // Clear existing data before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String course = document.getString("title");
                                String id = document.getString("id");
                                String instructor = document.getString("instructor");
                                String uuid = document.getString("uuid");
                                String desc = document.getString("description");
                                String sharedBy = document.getString("sharedBy");
                                boolean archive = Boolean.TRUE.equals(document.getBoolean("archive"));

                                if(Academia.isArchive) {
                                    if(archive) {
                                        coursemodel courseModel = new coursemodel(course, id, instructor, uuid, desc, archive, sharedBy);
                                        coursemodels.add(courseModel);
                                    }
                                } else {
                                    if(!archive) {
                                        coursemodel courseModel = new coursemodel(course, id, instructor, uuid, desc, archive, sharedBy);
                                        coursemodels.add(courseModel);
                                    }
                                }
                                notifyDataSetChanged();
                            }
                            notifyDataSetChanged(); // Notify the RecyclerView to refresh
                        } else {
                            // Handle the error
                        }
                    });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.courses_rview, parent, false);
        return new MyViewHolder(view,rcViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.course.setText(coursemodels.get(position).getcourse());
        holder.id.setText(coursemodels.get(position).getid());
        holder.instructor.setText(coursemodels.get(position).getinstructor());
    }

    @Override
    public int getItemCount() {
        return coursemodels.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnCreateContextMenuListener,View.OnLongClickListener {

        TextView course, id, instructor;
        public static int position;

        public MyViewHolder(@NonNull View itemView, RCViewInterface rcViewInterface) {
            super(itemView);
            course = itemView.findViewById(R.id.course);
            id = itemView.findViewById(R.id.id);
            instructor = itemView.findViewById(R.id.instructor);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0, 1, 0, "Edit");
            contextMenu.add(0, 2, 0, "Delete");
            contextMenu.add(0, 3, 0, "Share");
            if(Academia.isArchive) {
                contextMenu.add(0, 4, 0, "Unarchive");
            } else {
                contextMenu.add(0, 4, 0, "Archive");
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MyViewHolder.position = position;
                System.out.println(position);
                return false; // Consume the long click event
            }
            return false;
        }
    }

}
