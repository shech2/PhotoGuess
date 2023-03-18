package com.example.photoguess.view;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentJoinGameBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class JoinGameFragment extends BaseFragment {

    String myName;
    FragmentJoinGameBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myName = gameController.getName();
        binding = FragmentJoinGameBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.HowToPlayBTN.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.joinGameButton.setOnClickListener(view -> {
            String roomPin = binding.editTextGamePIN.getText().toString().trim();
            if(roomPin.length() != 5 || !roomPin.matches("\\d+")){
                binding.editTextGamePIN.setError("RoomPin must be 5 digits long and only contain numbers");
                binding.editTextGamePIN.requestFocus();
            }
            else{
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("Room_" + roomPin)){
                            myRef.child("Room_"+roomPin).child("Players")
                                    .child("Player"+(snapshot.child("Room_"+roomPin)
                                            .child("Players").getChildrenCount()+1)).child(myName).setValue(myName);
                            gameController.setRoomPin(roomPin);
                            replaceFragment(new gameLobbyFragment());
                        }
                        else{
                            binding.editTextGamePIN.setError("RoomPin does not exist");
                            binding.editTextGamePIN.requestFocus();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        return view;
    }


}