package com.example.surveyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private LinearLayout leaderboardContainer;
    private DatabaseReference usersRef;

    // A simple container to hold user data while we sort it
    private static class UserScore {
        String name;
        int points;
        UserScore(String n, int p) { name = n; points = p; }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        leaderboardContainer = view.findViewById(R.id.leaderboardContainer);

        // Connect to your Firebase Database "Users" node
        usersRef = FirebaseDatabase.getInstance("https://surveyapp-f1065-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        fetchLeaderboardData(inflater);

        return view;
    }

    private void fetchLeaderboardData(LayoutInflater inflater) {
        usersRef.orderByChild("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderboardContainer.removeAllViews(); // Clear old data
                List<UserScore> scoreList = new ArrayList<>();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String name = userSnap.child("name").getValue(String.class);
                    Integer points = userSnap.child("points").getValue(Integer.class);

                    if (name != null && points != null) {
                        scoreList.add(new UserScore(name, points));
                    }
                }

                // Firebase orderByChild sorts Ascending (Lowest to Highest).
                // We must reverse it so the highest points are at the top!
                Collections.reverse(scoreList);

                // Build the premium cards dynamically
                int rank = 1;
                for (UserScore user : scoreList) {
                    // Inflate the blueprint card we designed
                    View cardView = inflater.inflate(R.layout.item_leaderboard, leaderboardContainer, false);

                    TextView tvRank = cardView.findViewById(R.id.tvRank);
                    TextView tvName = cardView.findViewById(R.id.tvPlayerName);
                    TextView tvPoints = cardView.findViewById(R.id.tvPoints);

                    tvRank.setText("#" + rank);
                    tvName.setText(user.name);
                    tvPoints.setText(user.points + " pts");

                    // Add the finished card to the screen
                    leaderboardContainer.addView(cardView);
                    rank++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load rankings", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}