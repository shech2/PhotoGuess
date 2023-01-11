package com.example.photoguess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MenuFragment extends Fragment {

    View view;
    Button settingsBTN;
    Button joinGameBTN;
    Button createGameBTN;
    EditText nameET;
    Button testBTN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        nameET = view.findViewById(R.id.editTextTextPersonName);

        settingsBTN = view.findViewById(R.id.backButton);
        settingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SettingsFragment());
            }
        });

        createGameBTN = view.findViewById(R.id.createGameButton);
        createGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoom();
            }
        });

        joinGameBTN = view.findViewById(R.id.joinGameButton);
        joinGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRoom();
            }
        });
        testBTN = view.findViewById(R.id.test);
        testBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new GameFragment());
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

    private void joinRoom(){
        Bundle bundle = new Bundle();
        bundle.putString("name", nameET.getText().toString());
        joinGameFragment joinGameFragment = new joinGameFragment();
        joinGameFragment.setArguments(bundle);
        replaceFragment(new joinGameFragment());
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
                        if (room.getKey().equals("Room_"+roomPin[0]))
                            exists = true;
                    }
                    if (exists){
                        roomPin[0] = pinGenerator();
                    }
                    else{
                        myRef.child("Room_"+roomPin[0]).get();
                        String name = nameET.getText().toString();
                        myRef.child("Room_" + roomPin[0]).child("players").child(name).setValue(name);
                        gameLobbyFragment createFrag = new gameLobbyFragment();
                        Bundle lobbyBundle = new Bundle();
                        lobbyBundle.putString("name" , name);
                        lobbyBundle.putString("roomPin" , roomPin[0]);
                        createFrag.setArguments(lobbyBundle);
                        replaceFragment(createFrag);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String pinGenerator(){
        Random rand = new Random();
        int roomPIN = rand.nextInt(99999 - 10000 + 1) + 10000;
        String roomPINString = Integer.toString(roomPIN);
        return roomPINString;
    }
}