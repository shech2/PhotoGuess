package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentRouletteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class rouletteFragment extends Fragment {

    String roomPin;
    View view;
    FragmentRouletteBinding binding;

    ArrayList<User> playersList = new ArrayList<>();
    int playerCount;
    int playerPosition;
    int arrowPosition;
    int playerSelected;

    String sPTest;
    FirebaseDatabase database;
    DatabaseReference roomRef;

    ValueEventListener spinListener;

    ValueEventListener selectorListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        playerPosition = savedInstanceState.getInt("playerPosition");
        binding = FragmentRouletteBinding.inflate(inflater, container, false);

        view = binding.getRoot();
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        initialize();

        spinListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    System.out.println("Player selected!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error);
            }
        };
        selectorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("Change!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        roomRef.child("DoneSpinning").addValueEventListener(spinListener);
        roomRef.child("SelectedPlayer").addValueEventListener(selectorListener);
        arrowPosition = 0;
        return view;
    }

    private void spinRoulette() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random rand = new Random();
        int quickTics = rand.nextInt(3) + 3;
        int slowTics = quickTics * 2;
        float start = 0;
        while (start < slowTics){
            while (start < quickTics){
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                incArrowPosition();
                start += 0.25;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            incArrowPosition();
            start += 0.5;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        incArrowPosition();
        playerSelected = arrowPosition;
        System.out.println("DoneSpinning");
        roomRef.child("DoneSpinning").setValue(true);
    }

    public void incArrowPosition(){
        if (arrowPosition == playerCount)
            arrowPosition = 1;
        else
            arrowPosition++;
        System.out.println("Arrow Position: " + arrowPosition);
        roomRef.child("SelectedPlayer").setValue(arrowPosition);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        roomRef.removeEventListener(spinListener);
        roomRef.removeEventListener(selectorListener);
    }

    public void initialize(){
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("Data Change");
                playerCount = (int) snapshot.child("Players").getChildrenCount();
                for (DataSnapshot pNumber : snapshot.child("Players").getChildren()) {
                    for (DataSnapshot pName : pNumber.getChildren()) {
                        playersList.add(new User(Objects.requireNonNull(pName.getValue()).toString()));
                    }
                }
                ListAdapter adapter = new ListAdapter(getContext(), playersList);
                binding.roomListView.setAdapter(adapter);
                if (playerPosition == 1)
                    spinRoulette();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}