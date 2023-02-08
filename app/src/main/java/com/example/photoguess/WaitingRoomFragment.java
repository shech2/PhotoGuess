package com.example.photoguess;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
    String roomPin;
    ValueEventListener timeLeftEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        view = binding.getRoot();
        timeLeftEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int timeLeft = snapshot.getValue(Integer.class);
                binding.TimerTV.setText(String.valueOf(timeLeft));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        // The event listener is ON THE CHILD!!!!
        roomRef.child("Time Left").addValueEventListener(timeLeftEventListener);
        return view;
    }
}