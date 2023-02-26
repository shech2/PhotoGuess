package com.example.photoguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentActivePlayersBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActivePlayersFragment extends Fragment {

    FragmentActivePlayersBinding binding;
    View view;
    String roomPin;
    DatabaseReference roomRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        binding = FragmentActivePlayersBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
        storageRef = storage.getReference().child("Room_" + roomPin);
        String imageName = "Room_"+roomPin+".jpg";
        StorageReference imageRef = storageRef.child(imageName);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.displayedImage.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });


        return view;
    }
}