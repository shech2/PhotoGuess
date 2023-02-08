package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.SystemClock;
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

public class rouletteFragment extends Fragment {

    String roomPin;
    View view;
    FragmentRouletteBinding binding;

    ArrayList<User> playersList = new ArrayList<>();
    int playerCount;
    int playerPosition;
    int arrowPosition = 0;
    int prevArrowPosition = -1;

    int photoUploaderInt;
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

    // outer thread that updates main thread. maybe use canvas instead of imageview
    // check our Model, main handler
    // check surface view / canvas

    private void spinRoulette() {
//        SystemClock.sleep(1000);
        int quickTics = 3;
        int slowTics = quickTics * 2;
        float start = 0;
        while (start < slowTics){
            while (start < quickTics){
//                SystemClock.sleep(250);
                incArrowPosition();
                start += 0.5;
            }
//            SystemClock.sleep(500);
            incArrowPosition();
            start += 0.5;
        }
//        SystemClock.sleep(1000);
        while (arrowPosition != photoUploaderInt)
            incArrowPosition();

        SystemClock.sleep(5000);
        // Grab the host name from players list player1 is the host


    }

    private void showArrow(int pos) {
        view = binding.roomListView.getChildAt(pos);
        if (view != null) {
            View arrow = view.findViewById(R.id.arrow);
            if (arrow != null) {
                arrow.setVisibility(View.VISIBLE);

            }
        }
    }

    private void hideArrow(int pos){
        view = binding.roomListView.getChildAt(pos);
        if (view != null) {
            View arrow = view.findViewById(R.id.arrow);
            if (arrow != null) {
                arrow.setVisibility(View.INVISIBLE);

            }
        }

    }
    private void toggleArrowVisibility(int pos, int prevPos) {
        if (prevArrowPosition >= 0)
            hideArrow(prevPos);
        showArrow(pos);
    }

    public void incArrowPosition(){
        if (arrowPosition > 0)
            prevArrowPosition = arrowPosition;
        if (arrowPosition == playerCount)
            arrowPosition = 1;
        else
            arrowPosition++;
        System.out.println("Arrow position: " + arrowPosition);
        binding.selectedTest.setText(String.valueOf(arrowPosition));
        toggleArrowVisibility(arrowPosition, prevArrowPosition);
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
                photoUploaderInt = Integer.parseInt(Objects.requireNonNull(snapshot.child("PhotoUploader").getValue()).toString().charAt(6) + "");
                System.out.println("PhotoUploader: " + photoUploaderInt);
                ListAdapter adapter = new ListAdapter(getContext(), playersList);
                binding.roomListView.setAdapter(adapter);
                spinRoulette();
                nextFragment();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

    private void nextFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("roomPin", roomPin);
        if (playerPosition == photoUploaderInt) {
            PhotoPickerFragment photoPickerFragment = new PhotoPickerFragment();
            photoPickerFragment.setArguments(bundle);
            replaceFragment(photoPickerFragment);
        }else{
            WaitingRoomFragment waitingRoomFragment = new WaitingRoomFragment();
            waitingRoomFragment.setArguments(bundle);
            replaceFragment(waitingRoomFragment);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = rouletteFragment.this.requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }


}