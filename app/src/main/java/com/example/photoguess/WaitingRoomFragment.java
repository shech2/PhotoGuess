package com.example.photoguess;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentPhotoPickerBinding;
import com.example.photoguess.databinding.FragmentWaitingRoomBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WaitingRoomFragment extends Fragment {

    FragmentWaitingRoomBinding binding;
    View view;
    DatabaseReference roomRef;
    FirebaseDatabase database;
    int timeLeft = 30;
    boolean gameStarting = false;
    String roomPin;
    String name;
    ValueEventListener timeLeftEventListener;
    ValueEventListener gameReady;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        name = savedInstanceState.getString("name");
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        view = binding.getRoot();
        timeLeftEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeLeft = snapshot.getValue(Integer.class);
                if (timeLeft <= 3 && gameStarting) {
                    binding.Timer.setText("Game will start in: ");
                }
                binding.TimerTV.setText(String.valueOf(timeLeft));
                if (timeLeft == 0 && gameStarting) {
                    Bundle bundle = new Bundle();
                    bundle.putString("roomPin", roomPin);
                    bundle.putString("name", name);
                    ActivePlayersFragment activePlayersFragment = new ActivePlayersFragment();
                    activePlayersFragment.setArguments(bundle);
                    replaceFragment(activePlayersFragment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        gameReady = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    gameStarting = true;
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        // The event listener is ON THE CHILD!!!!
        roomRef.child("Time Left").addValueEventListener(timeLeftEventListener);
        roomRef.child("Photo Uploaded").addValueEventListener(gameReady);
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = WaitingRoomFragment.this.requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomRef.child("Time Left").removeEventListener(timeLeftEventListener);
        roomRef.child("Photo Uploaded").removeEventListener(gameReady);
    }
}