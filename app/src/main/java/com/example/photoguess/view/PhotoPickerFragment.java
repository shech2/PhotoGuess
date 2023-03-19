package com.example.photoguess.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentPhotoPickerBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PhotoPickerFragment extends BaseFragment {

    FragmentPhotoPickerBinding binding;
    ActivityResultLauncher<String> gallery;
    DatabaseReference roomRef;
    FirebaseStorage storage;
    StorageReference storageRef;
    String roomPin;
    ValueEventListener timeLeftEventListener;
    boolean photoChanged = false;
    boolean gameStarting = false;
    int counter = 30;

    Thread firstCounterThread;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        roomPin = gameController.getRoomPin();
        roomRef = gameModel.getRoomRef();
        binding = FragmentPhotoPickerBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        if (gameController.isMusicOn())
            binding.musicToggleButton.setImageResource(R.drawable.volume);
        else
            binding.musicToggleButton.setImageResource(R.drawable.mute);
        binding.musicToggleButton.setOnClickListener(view -> {
            if(gameController.isMusicOn()){
                gameController.stopBackgroundMusic();
                binding.musicToggleButton.setImageResource(R.drawable.mute);
            }else{
                gameController.startBackgroundMusic();
                binding.musicToggleButton.setImageResource(R.drawable.volume);
            }
        });
        ActivityResultContracts.GetContent getContentContract = new ActivityResultContracts.GetContent();
        gallery = registerForActivityResult(getContentContract, result -> {
            binding.uploadPhotoImage.setImageURI(result);
            photoChanged = true;
        });

        binding.uploadPhotoImage.setOnClickListener(view -> gallery.launch("image/*"));
        binding.uploadPhotoToFirebase.setOnClickListener(view -> {
                    String captionText = binding.captionText.getText().toString().trim();
                    if (photoChanged && isAlphabeticalEnglish(captionText)) {
                        Bitmap bitmap = ((BitmapDrawable) binding.uploadPhotoImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
                        storageRef = storage.getReference().child("Room_" + roomPin);
                        UploadTask uploadTask = storageRef.putBytes(data);
                        uploadTask.addOnFailureListener(exception -> Toast.makeText(getContext(), "Upload unsuccessful", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            roomRef.child("PhotoUploaded").setValue(true);
                            roomRef.child("Caption").setValue(captionText.toUpperCase());
                            roomRef.child("GameProgress").child("MessageBoard").setValue("Loading");
                            roomRef.child("GameProgress").child("BlurLevel").setValue(100);
                            gameStarting = true;
                            binding.Timer.setText("Game will start in: ");
                            binding.TimerTV.setText("4");
                            runGameStartThread();
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
                if (timeLeft == 0 && !gameStarting){
                    binding.Timer.setText("Jesus Christ man, hurry up!");
                    binding.TimerTV.setText("");
                }
                if (timeLeft == 0 && gameStarting){
                    replaceFragment(new PhotoPickerActiveGameFragment());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        roomRef.child("Time Left").addValueEventListener(timeLeftEventListener);

        return view;
    }

    // Runs a 30 second timer
    public void runThread() {
        firstCounterThread = new Thread(() -> {
            counter = 30;
            while (counter > 0) {
                try {
                    Thread.sleep(1000);
                    counter--;
                    roomRef.child("Time Left").setValue(counter);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        firstCounterThread.start();
    }

    // Runs a 4 second timer
    public void runGameStartThread(){
        firstCounterThread.interrupt();
        new Thread(() -> {
            counter = 4;
            while (counter > 0) {
                try {
                    Thread.sleep(1000);
                    counter--;
                    roomRef.child("Time Left").setValue(counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            firstCounterThread.interrupt();
        }).start();
    }

    public boolean isAlphabeticalEnglish(String text) {
        if (text == null || text.isEmpty()) {
            binding.captionText.setError("Please enter a caption");
            binding.captionText.requestFocus();
            return false;
        }
        if (text.trim().length() > 30){
            binding.captionText.setError("Caption is too long");
            binding.captionText.requestFocus();
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                binding.captionText.setError("Please enter an alphabetical-only caption");
                binding.captionText.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomRef.child("Time Left").removeEventListener(timeLeftEventListener);
        roomRef.child("Time Left").setValue(30);
        roomRef.child("PhotoUploaded").removeValue();
    }

}