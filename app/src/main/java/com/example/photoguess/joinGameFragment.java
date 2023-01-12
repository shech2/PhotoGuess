package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class joinGameFragment extends Fragment {

    View view;
    Button joinGameBTN;
    Button backBTN;
    String playerName;
    EditText gamePinET;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        savedInstanceState = this.getArguments();
        if(savedInstanceState != null){
            playerName = savedInstanceState.getString("name");
        }

        view = inflater.inflate(R.layout.fragment_join_game, container, false);

        gamePinET = view.findViewById(R.id.editTextGamePIN);

        backBTN = view.findViewById(R.id.backButton);
        backBTN.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        joinGameBTN = view.findViewById(R.id.joinGameButton);
        joinGameBTN.setOnClickListener(view -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference myRef = database.getReference("Rooms");
            myRef.child("Room_"+gamePinET.getText().toString()).child("Players").child(playerName).setValue(playerName);
            gameLobbyFragment createFrag = new gameLobbyFragment();
            Bundle lobbyBundle = new Bundle();
            lobbyBundle.putString("name" , playerName);
            lobbyBundle.putString("roomPin" , gamePinET.getText().toString());
            createFrag.setArguments(lobbyBundle);
            replaceFragment(createFrag);
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