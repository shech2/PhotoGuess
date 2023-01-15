package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.example.photoguess.databinding.FragmentJoinGameBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class joinGameFragment extends Fragment {

    View view;
    String playerName;
    FragmentJoinGameBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        savedInstanceState = this.getArguments();
        if(savedInstanceState != null){
            playerName = savedInstanceState.getString("name");
        }
        binding = FragmentJoinGameBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.SettingsBTN.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.joinGameButton.setOnClickListener(view -> {
            String enteredRoomPin = binding.editTextGamePIN.getText().toString().trim();
            if(enteredRoomPin.length() != 5 || !enteredRoomPin.matches("[0-9]+")){
                binding.editTextGamePIN.setError("RoomPin must be 5 digits long and only contain numbers");
                binding.editTextGamePIN.requestFocus();
            }else{
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
                DatabaseReference myRef = database.getReference("Rooms");
                 myRef.child("Room_" + binding.editTextGamePIN.getText().toString()).child("Counter")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int counter = dataSnapshot.getValue(Integer.class);
                            counter++;
                            myRef.child("Room_"+binding.editTextGamePIN.getText().toString()).child("Counter").setValue(counter);
                            myRef.child("Room_"+binding.editTextGamePIN.getText().toString()).child("Players").child("Player"+counter).child(playerName).setValue(playerName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle error
                            System.out.println("Error: " + databaseError.getMessage());
                        }
                    });
                gameLobbyFragment createFrag = new gameLobbyFragment();
                Bundle lobbyBundle = new Bundle();
                lobbyBundle.putString("name", playerName);
                lobbyBundle.putString("roomPin", enteredRoomPin);
                createFrag.setArguments(lobbyBundle);
                replaceFragment(createFrag);
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