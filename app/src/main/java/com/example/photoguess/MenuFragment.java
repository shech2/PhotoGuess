package com.example.photoguess;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MenuFragment extends Fragment {

    View view;
    Button settingsBTN;
    Button joinGameBTN;
    Button createGameBTN;
    EditText joinGameET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        joinGameET = view.findViewById(R.id.editTextTextPersonName);

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
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
                DatabaseReference myRef = database.getReference("CurrentRoom");
                String name = joinGameET.getText().toString();
                myRef.setValue(name);
                createGameFragment createFrag = new createGameFragment();
                Bundle createBundle = new Bundle();
                createBundle.putString("name" , name);
                createFrag.setArguments(createBundle);
                replaceFragment(createFrag);
            }
        });

        joinGameBTN = view.findViewById(R.id.joinGameButton);
        joinGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new joinGameFragment());
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