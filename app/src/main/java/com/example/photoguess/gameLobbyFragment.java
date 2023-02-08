package com.example.photoguess;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.photoguess.databinding.FragmentGameLobbyBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class gameLobbyFragment extends Fragment {

    View view;

    boolean gameStarted = false;
    FragmentGameLobbyBinding binding;
    ListView listView;
    TextView roomPinDisplay;
    ArrayList<String> players = new ArrayList<>();
    String name;

    int playerPosition;
    String roomPin;
    ValueEventListener playersEventListener;
    ValueEventListener roomEventListener;
    DatabaseReference playersRef;

    int playersCount;
    DatabaseReference roomRef;
    DatabaseReference countRef;
    FirebaseDatabase database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name");
            roomPin = savedInstanceState.getString("roomPin");
            players.add(name);
        }
        binding = FragmentGameLobbyBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.backButton2.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.startGameButton.setOnClickListener(view -> startGame());
        listView = view.findViewById(R.id.roomList);
        roomPinDisplay = view.findViewById(R.id.pinDisplay);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        playersRef = roomRef.child("Players");
        countRef = roomRef.child("Counter");
        playersEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.clear();
                for (DataSnapshot player : snapshot.getChildren()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        for (DataSnapshot playerName : player.getChildren()) {
                            if (playerName.getValue() != null) {
                                players.add(playerName.getValue().toString());
                                if (playerName.getValue().toString().equals(name)){
                                    playerPosition = Integer.parseInt(Objects.requireNonNull(player.getKey()).substring(player.getKey().length() - 1));
                                }
                            }
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.fragment_item2, players);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };
        roomEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Players").getChildrenCount() != 0){
                    playersCount = (int) snapshot.child("Players").getChildrenCount();
                    countRef.setValue(playersCount);
                }
                if(Objects.requireNonNull(snapshot.child("Counter").getValue()).toString().equals("1")){
                    binding.wfpTV.setVisibility(View.VISIBLE);
                }else{
                    binding.wfpTV.setVisibility(View.INVISIBLE);
                }
                if (snapshot.child("GameStarted").getValue() != null){
                    gameStarted = true;
                    roomRef.child("GameStarted").removeValue();
                    Bundle bundle = new Bundle();
                    bundle.putString("roomPin", roomPin);
                    bundle.putInt("playerPosition", playerPosition);
                    rouletteFragment rouletteFrag = new rouletteFragment();
                    rouletteFrag.setArguments(bundle);
                    replaceFragment(rouletteFrag);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };

        playersRef.addValueEventListener(playersEventListener);
        roomRef.addValueEventListener(roomEventListener);

        String string = getString(R.string.roomPin, roomPin);
        roomPinDisplay.setText(string);
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = gameLobbyFragment.this.requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!gameStarted) {
            playersRef.child("Player"+playerPosition).removeValue();
            playersCount--;
            if (playersCount <= 0){
                roomRef.removeValue();
            }
            else {
                countRef.setValue(playersCount);
            }
        }
        playersRef.removeEventListener(playersEventListener);
        countRef.removeEventListener(roomEventListener);
    }

    public void startGame(){
        setRandomUploader();
        roomRef.child("Time Left").setValue(30);
        roomRef.child("GameStarted").setValue(true);
    }

    public void setRandomUploader(){
        roomRef.child("PhotoUploader").setValue("Player1");
//        int random = (int) (Math.random() * playersCount + 1);
//        roomRef.child("PhotoUploader").setValue("Player"+random);
    }
}