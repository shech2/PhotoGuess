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
import android.widget.Toast;

import java.util.ArrayList;

public class createGameFragment extends Fragment {

    View view;
    Button backBTN;
    ListView listView;
    ArrayList<String> items = new ArrayList<>();
    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_game, container, false);
        backBTN = view.findViewById(R.id.backButton2);
        listView = view.findViewById(R.id.roomList);

        savedInstanceState = this.getArguments();
        if(savedInstanceState != null){
            String name = savedInstanceState.getString("name");
        }

        savedInstanceState = this.getArguments();
        if(savedInstanceState != null){
            name = savedInstanceState.getString("name");
            items.add(name);
        }
        // console
        System.out.println(name);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
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