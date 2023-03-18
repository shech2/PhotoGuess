package com.example.photoguess.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentMenuBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class MenuFragment extends BaseFragment {

    FragmentMenuBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.HowToPlayBTN.setOnClickListener(view -> replaceFragment(new HowToPlayFragment()));
        binding.createGameButton.setOnClickListener(view -> createRoom());
        binding.joinGameButton.setOnClickListener(view -> joinRoom());
        binding.musicToggleButton.setOnClickListener(view -> {
            System.out.println("Music button pressed");
            if(gameController.isMusicOn()){
                gameController.stopBackgroundMusic();
                binding.musicToggleButton.setImageResource(R.drawable.mute);
                System.out.println("Music Mute");
            }else{
                gameController.startBackgroundMusic();
                binding.musicToggleButton.setImageResource(R.drawable.volume);
                System.out.println("Music On");

            }
        });
        return view;
    }

    private void joinRoom(){
        String name = binding.editTextTextPersonName.getText().toString().trim();
        if(name.isEmpty()){
            binding.editTextTextPersonName.setError("Please enter a name");
            binding.editTextTextPersonName.requestFocus();
        }else {
            gameController.setName(name);
            replaceFragment(new JoinGameFragment());
        }
    }

    private void createRoom(){
        final String[] roomPin = {pinGenerator()};
        gameController.setRoomPin(roomPin[0]);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = true;
                while (exists) {
                    exists = false;
                    for (DataSnapshot room : snapshot.getChildren()) {
                        if (Objects.equals(room.getKey(), "Room_" + roomPin[0]))
                            exists = true;
                    }
                    if (exists){
                        roomPin[0] = pinGenerator();
                    }
                    else{
                        String name = binding.editTextTextPersonName.getText().toString().trim();
                        if(name.isEmpty()){
                            binding.editTextTextPersonName.setError("Please enter a name");
                            binding.editTextTextPersonName.requestFocus();
                        }else{
                            myRef.child("Room_"+roomPin[0]).child("Counter").setValue(1);
                            myRef.child("Room_"+roomPin[0]).child("Players").child("Player1").child(name).setValue(name);
                            myRef.child("Room_"+roomPin[0]).child("Players").child("Player2").child("David").setValue("David");
                            myRef.child("Room_"+roomPin[0]).child("Players").child("Player3").child("Moshe").setValue("Moshe");

                            gameController.setName(name);
                            gameController.setRoomPin(roomPin[0]);
                            replaceFragment(new gameLobbyFragment());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });
    }

    private String pinGenerator(){
        Random rand = new Random();
        int roomPIN = rand.nextInt(99999 - 10000 + 1) + 10000;
        return Integer.toString(roomPIN);
    }
}