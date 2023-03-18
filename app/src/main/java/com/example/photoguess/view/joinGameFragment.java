package com.example.photoguess.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentJoinGameBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class joinGameFragment extends Fragment {

    View view;
    String playerName;
    FragmentJoinGameBinding binding;

    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        savedInstanceState = this.getArguments();
        if(savedInstanceState != null){
            playerName = savedInstanceState.getString("name");
        }
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Rooms");
        binding = FragmentJoinGameBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.HowToPlayBTN.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.joinGameButton.setOnClickListener(view -> {
            String enteredRoomPin = binding.editTextGamePIN.getText().toString().trim();
            if(enteredRoomPin.length() != 5 || !enteredRoomPin.matches("\\d+")){
                binding.editTextGamePIN.setError("RoomPin must be 5 digits long and only contain numbers");
                binding.editTextGamePIN.requestFocus();
            }
            else{
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("Room_" + enteredRoomPin)){
                            myRef.child("Room_"+enteredRoomPin).child("Players")
                                    .child("Player"+(snapshot.child("Room_"+enteredRoomPin)
                                            .child("Players").getChildrenCount()+1)).child(playerName).setValue(playerName);
                            gameLobbyFragment createFrag = new gameLobbyFragment();
                            Bundle lobbyBundle = new Bundle();
                            lobbyBundle.putString("name", playerName);
                            lobbyBundle.putString("roomPin", enteredRoomPin);
                            createFrag.setArguments(lobbyBundle);
                            replaceFragment(createFrag);
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

}