package com.example.photoguess;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class gameLobbyFragment extends Fragment {

    View view;
    Button backBTN;
    ListView listView;
    TextView roomPinDisplay;
    ArrayList<String> players = new ArrayList<>();
    String name;

    int playerPosition;
    String roomPin;
    ValueEventListener eventListener;
    ValueEventListener eventListener2;
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
        view = inflater.inflate(R.layout.fragment_game_lobby, container, false);
        backBTN = view.findViewById(R.id.backButton2);
        listView = view.findViewById(R.id.roomList);
        roomPinDisplay = view.findViewById(R.id.pinDisplay);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        playersRef = roomRef.child("Players");
        countRef = roomRef.child("Counter");
        eventListener = new ValueEventListener() {
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
        eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Players").getChildrenCount() != 0){
                    playersCount = (int) snapshot.child("Players").getChildrenCount();
                    countRef.setValue(playersCount);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };
        playersRef.addValueEventListener(eventListener);
        roomRef.addValueEventListener(eventListener2);

        String string = getString(R.string.roomPin, roomPin);
        roomPinDisplay.setText(string);

        backBTN.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        playersRef.child("Player"+playerPosition).removeValue();
        playersCount--;
        if (playersCount <= 0){
            roomRef.removeValue();
        }
        else {
            countRef.setValue(playersCount);
        }
        playersRef.removeEventListener(eventListener);
        countRef.removeEventListener(eventListener2);
    }
}