package com.example.frenbot;

import static com.example.frenbot.Constants.TOPIC;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class DashboardFragment extends Fragment {

    CardView financeCard,academiaCard,eventCard,sosCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root= (ViewGroup) inflater.inflate(R.layout.activity_main,container,false);

        financeCard = root.findViewById(R.id.finacecard);
        academiaCard = root.findViewById(R.id.academiacard);
        eventCard = root.findViewById(R.id.eventcard);
        sosCard = root.findViewById(R.id.soscard);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + user.getUid());
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

        // Set click listeners for the CardViews
        financeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the FinanceActivity when financeCard is clicked
                Intent intent = new Intent(getActivity(), Notice.class);
                startActivity(intent);
            }
        });

        academiaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the AcademiaActivity when academiaCard is clicked
                Intent intent = new Intent(getActivity(), Academia.class);
                intent.putExtra("isArchive", false);
                startActivity(intent);
            }
        });

        eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the EventActivity when eventCard is clicked
                Intent intent = new Intent(getActivity(), Events.class);
                intent.putExtra("flag", "one");
                startActivity(intent);
            }
        });

        sosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the SOSActivity when sosCard is clicked
                Intent intent = new Intent(getActivity(), Sos.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
