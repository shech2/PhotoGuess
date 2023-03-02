package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentPhotoPickerActiveGameBinding;
import com.example.photoguess.databinding.FragmentPhotoPickerBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class PhotoPickerActiveGameFragment extends Fragment {

    FragmentPhotoPickerActiveGameBinding binding;
    View view;
    String roomPin;

    DatabaseReference roomRef;
    DatabaseReference progressRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;
    ValueEventListener gameProgressListener;

    String[][] playersArray;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        binding = FragmentPhotoPickerActiveGameBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        progressRef = roomRef.child("gameProgress");
        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
        storageRef = storage.getReference().child("Room_" + roomPin);
        view = binding.getRoot();
        binding.displayedImage.setImageResource(R.drawable.questionmark);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                int j = 0;
                int playerCount = (int) snapshot.child("Players").getChildrenCount();
                playersArray = new String[2][playerCount - 1];
                for (int i = 0; i < playerCount; i++) {
                    if (!Objects.equals(snapshot.child("PhotoUploader").getValue(), snapshot.child("Players")
                            .child("Player" + (i + 1))
                            .getKey()))
                    {
                        playersArray[0][j] = "Player" + (i + 1);
                        for (DataSnapshot child : snapshot.child("Players")
                            .child("Player" + (i + 1))
                            .getChildren())
                        {
                            playersArray[1][j] = Objects.requireNonNull(child.getValue()).toString();
                        }
                        j++;
                    }
                }
                System.out.println("Players Array: " + Arrays.deepToString(playersArray));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



//        progressRef.addValueEventListener(gameProgressListener);
        return view;
    }
}