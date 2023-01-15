package com.example.photoguess;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.photoguess.databinding.FragmentRouletteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class rouletteFragment extends Fragment {

    String roomPin;
    View view;
    FragmentRouletteBinding binding;

    ArrayList<User> playersList = new ArrayList<>();
    int playerCount;
    FirebaseDatabase database;
    DatabaseReference roomRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        roomPin = getArguments().getString("roomPin");
        binding = FragmentRouletteBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerCount = (int) snapshot.child("Players").getChildrenCount();
                for (DataSnapshot pNumber : snapshot.child("Players").getChildren()) {
                    for (DataSnapshot pName : pNumber.getChildren()) {
                        playersList.add(new User(pName.getValue().toString()));
                    }
                }
                ListAdapter adapter = new ListAdapter(getContext(), playersList);
                binding.roomListView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}