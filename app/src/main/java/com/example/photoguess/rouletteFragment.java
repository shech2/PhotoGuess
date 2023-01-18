package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentRouletteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class rouletteFragment extends Fragment {

    String roomPin;
    View view;
    FragmentRouletteBinding binding;

    ArrayList<User> playersList = new ArrayList<>();
    int playerCount;
    int playerPosition;
    int arrowPosition = 0;
    int prevArrowPosition = -1;
    int playerSelected;

    String sPTest;
    FirebaseDatabase database;
    DatabaseReference roomRef;

    ValueEventListener spinListener;

    ValueEventListener selectorListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        playerPosition = savedInstanceState.getInt("playerPosition");
        binding = FragmentRouletteBinding.inflate(inflater, container, false);

        view = binding.getRoot();
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        initialize();


        spinListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    System.out.println("Player selected!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error);
            }
        };
        selectorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    binding.selectedTest.setText(snapshot.getValue().toString());
                    arrowPosition = Integer.parseInt(snapshot.getValue().toString())-1;
                    toggleArrowVisibility(arrowPosition, prevArrowPosition);
                    prevArrowPosition = arrowPosition;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        roomRef.child("DoneSpinning").addValueEventListener(spinListener);
        roomRef.child("SelectedPlayer").addValueEventListener(selectorListener);
        return view;
    }


    private void spinRoulette() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random rand = new Random();
        int quickTics = rand.nextInt(3) + 3;
        int slowTics = quickTics * 2;
        float start = 0;
        while (start < slowTics){
            while (start < quickTics){
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                incArrowPosition();
                start += 0.1;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            incArrowPosition();
            start += 0.5;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        incArrowPosition();
        playerSelected = arrowPosition;
        System.out.println("DoneSpinning");
        roomRef.child("DoneSpinning").setValue(true);
        // Wait for 10 seconds and then transfer to GameFragment
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Grab the hot name from players list player1 is the host
        String hostName = playersList.get(playerSelected-1).getName();

        roomRef.child("PhotoUploader").setValue(hostName);

        // Transfer to GameFragment
        GameFragment gameFragment = new GameFragment();
        Bundle bundle = new Bundle();
        bundle.putString("roomPin", roomPin);
        bundle.putString("PhotoUploader", hostName);
        gameFragment.setArguments(bundle);
        replaceFragment(gameFragment);
    }

    private void showArrow(int pos) {
        view = binding.roomListView.getChildAt(pos);
        view.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
    }

    private void hideArrow(int pos){
        view = binding.roomListView.getChildAt(pos);
        view.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

    }
    private void toggleArrowVisibility(int pos, int prevPos) {
        if (prevArrowPosition >= 0)
            hideArrow(prevPos);
        showArrow(pos);
    }

    public void incArrowPosition(){
        if (arrowPosition == playerCount)
            arrowPosition = 1;
        else
            arrowPosition++;
        roomRef.child("SelectedPlayer").setValue(arrowPosition);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        roomRef.removeEventListener(spinListener);
        roomRef.removeEventListener(selectorListener);
    }

    public void initialize(){
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerCount = (int) snapshot.child("Players").getChildrenCount();
                for (DataSnapshot pNumber : snapshot.child("Players").getChildren()) {
                    for (DataSnapshot pName : pNumber.getChildren()) {
                        playersList.add(new User(Objects.requireNonNull(pName.getValue()).toString()));
                    }
                }
                ListAdapter adapter = new ListAdapter(getContext(), playersList);
                binding.roomListView.setAdapter(adapter);
                if (playerPosition == 1){
                    runFunctionInNewThread();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    // Create Random number between 1 and playerCount
    private int RandomNum() {
        Random rand = new Random();
        int PlayersCount = playersList.size();
        return rand.nextInt(PlayersCount) + 1;
    }


    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFragmentContainerView, fragment);
        transaction.commit();
    }

    public class MyRunnable implements Runnable {
        public void run() {
            arrowPosition = RandomNum();
            spinRoulette();
        }
    }

    public void runFunctionInNewThread() {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }
}