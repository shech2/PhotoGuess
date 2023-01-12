package com.example.photoguess;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class gameLobbyFragment extends Fragment {

    View view;
    Button backBTN;
    ListView listView;
    TextView roomPinDisplay;
    ArrayList<String> players = new ArrayList<>();
    String name;
    String roomPin;
    ValueEventListener eventListener;
    DatabaseReference playersRef;

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
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        playersRef = database.getReference("Rooms").child("Room_" + roomPin).child("Players");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.clear();
                for (DataSnapshot player : snapshot.getChildren()) {
                    System.out.println("Player name " + player.getValue().toString());
                    players.add(player.getValue().toString());
                }
                ListAdapter listAdapter = new ArrayAdapter<>(getContext(), R.layout.fragment_item, players);
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };
        playersRef.addValueEventListener(eventListener);

        roomPinDisplay.setText("Room " + roomPin);

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
        playersRef.removeEventListener(eventListener);

    }
}