package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_lobby, container, false);
        backBTN = view.findViewById(R.id.backButton2);
        listView = view.findViewById(R.id.roomList);
        roomPinDisplay = view.findViewById(R.id.pinDisplay);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference playersRef = database.getReference("rooms").child("Room_"+roomPin).child("players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot player : snapshot.getChildren()) {
                    players.add(player.getValue().toString());
                }
                ListAdapter listAdapter = new ArrayAdapter<>(getContext(), R.layout.fragment_item, players);
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        savedInstanceState = this.getArguments();
        if(savedInstanceState != null){
            name = savedInstanceState.getString("name");
            roomPin = savedInstanceState.getString("roomPin");
            players.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_item, players);
        roomPinDisplay.setText("Room " + roomPin);
        listView.setAdapter(adapter);

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new MenuFragment());
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