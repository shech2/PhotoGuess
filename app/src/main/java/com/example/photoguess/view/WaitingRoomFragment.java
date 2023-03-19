package com.example.photoguess.view;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentWaitingRoomBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitingRoomFragment extends BaseFragment {

    FragmentWaitingRoomBinding binding;
    DatabaseReference roomRef;
    int timeLeft = 30;
    boolean gameStarting = false;
    String roomPin;
    String name;
    ValueEventListener timeLeftEventListener;
    ValueEventListener gameReady;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        roomPin = gameController.getRoomPin();
        name = gameController.getName();
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);
        roomRef = gameModel.getRoomRef();
        view = binding.getRoot();
        if (gameController.isMusicOn())
            binding.musicToggleButton.setImageResource(R.drawable.volume);
        else
            binding.musicToggleButton.setImageResource(R.drawable.mute);
        binding.musicToggleButton.setOnClickListener(view -> {
            if(gameController.isMusicOn()){
                gameController.stopBackgroundMusic();
                binding.musicToggleButton.setImageResource(R.drawable.mute);
            }else{
                gameController.startBackgroundMusic();
                binding.musicToggleButton.setImageResource(R.drawable.volume);
            }
        });
        timeLeftEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeLeft = snapshot.getValue(Integer.class);
                if (timeLeft <= 3 && gameStarting) {
                    binding.Timer.setText("Game will start in: ");
                }
                binding.TimerTV.setText(String.valueOf(timeLeft));
                if (timeLeft == 0 && gameStarting) {
                    replaceFragment(new ActivePlayersFragment());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        gameReady = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    gameStarting = true;
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        roomRef.child("Time Left").addValueEventListener(timeLeftEventListener);
        roomRef.child("PhotoUploaded").addValueEventListener(gameReady);
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomRef.child("Time Left").removeEventListener(timeLeftEventListener);
        roomRef.child("PhotoUploaded").removeEventListener(gameReady);
    }
}