package com.example.photoguess;

import android.content.Context;
import android.os.Bundle;

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
import android.widget.Toast;

import java.util.ArrayList;

public class createGameFragment extends Fragment {

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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_game, container, false);
        backBTN = view.findViewById(R.id.backButton2);
        listView = view.findViewById(R.id.roomList);
        roomPinDisplay = view.findViewById(R.id.pinDisplay);

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