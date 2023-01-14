package com.example.photoguess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.photoguess.databinding.FragmentMenuBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class MenuFragment extends Fragment {

    View view;
    FragmentMenuBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.SettingsBTN.setOnClickListener(view -> replaceFragment(new SettingsFragment()));

        binding.createGameButton.setOnClickListener(view -> createRoom());

        binding.joinGameButton.setOnClickListener(view -> joinRoom());
        binding.test.setOnClickListener(view -> replaceFragment(new GameFragment()));


        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

    private void joinRoom(){
        Bundle bundle = new Bundle();
        String name = binding.editTextTextPersonName.getText().toString().trim();
        if(name.isEmpty()){
            binding.editTextTextPersonName.setError("Please enter a name");
            binding.editTextTextPersonName.requestFocus();
        }else {
            bundle.putString("name", name);
            joinGameFragment joinFrag = new joinGameFragment();
            joinFrag.setArguments(bundle);
            replaceFragment(joinFrag);
        }
    }

    private void createRoom(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Rooms");
        final String[] roomPin = {pinGenerator()};
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
                        }else {
                                myRef.child("Room_" + roomPin[0]).child("Players").child(name).setValue(name);
                                gameLobbyFragment createFrag = new gameLobbyFragment();
                                Bundle lobbyBundle = new Bundle();
                                lobbyBundle.putString("name", name);
                                lobbyBundle.putString("roomPin", roomPin[0]);
                                createFrag.setArguments(lobbyBundle);
                                replaceFragment(createFrag);
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