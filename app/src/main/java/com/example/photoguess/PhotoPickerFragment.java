package com.example.photoguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.photoguess.databinding.FragmentPhotoPickerBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PhotoPickerFragment extends Fragment {

    FragmentPhotoPickerBinding binding;
    View view;
    ActivityResultLauncher<String> gallery;
    DatabaseReference roomRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;
    String roomPin;
    ValueEventListener timeLeftEventListener;
    boolean photoChanged = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        binding = FragmentPhotoPickerBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        view = binding.getRoot();


        ActivityResultContracts.GetContent getContentContract = new ActivityResultContracts.GetContent();
        gallery = registerForActivityResult(getContentContract, result -> {
            binding.uploadPhotoImage.setImageURI(result);
            photoChanged = true;
        });

        binding.uploadPhotoImage.setOnClickListener(view -> gallery.launch("image/*"));
        binding.uploadPhotoToFirebase.setOnClickListener(view -> {
                    if (photoChanged) {
                        Bitmap bitmap = ((BitmapDrawable) binding.uploadPhotoImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
                        storageRef = storage.getReference().child("Room_" + roomPin);
                        UploadTask uploadTask = storageRef.putBytes(data);
                        uploadTask.addOnFailureListener(exception -> Toast.makeText(getContext(), "Upload unsuccessful", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            roomRef.child("Photo Uploaded").setValue(true);
                        });
                    }
                    else {
                        Toast.makeText(getContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                    }
                }
            );
        runThread();
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

    public void runThread() {
        new Thread(() -> {
            int counter = 30;
            while (counter > 0) {
                try {
                    Thread.sleep(1000);
                    counter--;
                    roomRef.child("Time Left").setValue(counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}