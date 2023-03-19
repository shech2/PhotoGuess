package com.example.photoguess.view;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentGameLobbyBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class gameLobbyFragment extends BaseFragment {

    boolean gameStarted = false;
    boolean startSequence = false;
    FragmentGameLobbyBinding binding;
    ListView listView;
    TextView roomPinDisplay;
    ArrayList<String> playersArray = new ArrayList<>();
    String myName;

    int playerPosition;
    String roomPin;
    ValueEventListener playersEventListener;
    ValueEventListener roomEventListener;
    DatabaseReference playersRef;

    int playersCount;
    DatabaseReference roomRef;
    DatabaseReference countRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myName = gameController.getName();
        roomPin = gameController.getRoomPin();
        gameController.setStorageRef(storage.getReference().child("Room_" + roomPin));
        playersArray.add(myName);

        binding = FragmentGameLobbyBinding.inflate(inflater, container, false);
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
        binding.backButton2.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.startGameButton.setOnClickListener(view -> gameController.startGame(playerPosition, playersCount, playersArray));
        listView = view.findViewById(R.id.roomList);
        roomPinDisplay = view.findViewById(R.id.pinDisplay);
        roomRef = gameModel.getRoomRef();
        playersRef = roomRef.child("Players");
        countRef = roomRef.child("Counter");
        playersEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playersArray.clear();
                for (DataSnapshot player : snapshot.getChildren()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        for (DataSnapshot playerName : player.getChildren()) {
                            if (playerName.getValue() != null) {
                                playersArray.add(playerName.getValue().toString());
                                if (playerName.getValue().toString().equals(myName)){
                                    playerPosition = Integer.parseInt(Objects.requireNonNull(player.getKey()).substring(player.getKey().length() - 1));
                                    if (playerPosition == 1){
                                        binding.startGameButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.fragment_item2, playersArray);
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
                if (snapshot.child("GameStarted").getValue() != null && !startSequence){
                    gameStarted = true;
                    startSequence = true;
                    gameController.startGame(playerPosition, playersCount, playersArray);
                    replaceFragment(new RouletteFragment());
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
}