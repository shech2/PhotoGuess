package com.example.photoguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.photoguess.databinding.FragmentActivePlayersBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActivePlayersFragment extends Fragment {

    FragmentActivePlayersBinding binding;
    View view;
    String roomPin;

    String photoCaptionText;
    char[] photoCaptionArray;
    char[] guessingArray;
    char[] usedLetters;
    String guessingArrayString;
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
        binding.displayedImage.setImageResource(R.drawable.questionmark);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        roomRef.child("Caption").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoCaptionText = snapshot.getValue(String.class);
                assert photoCaptionText != null;
                photoCaptionArray = photoCaptionText.toCharArray();
                guessingArray = new char[photoCaptionArray.length];
                underscoreCreator();
                guessingArrayString = new String(guessingArray);
                binding.hangmanText.setText(guessingArrayString);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
        storageRef = storage.getReference().child("Room_" + roomPin);

        storageRef.getBytes(1024 * 1024).addOnSuccessListener(bytes -> {
            SystemClock.sleep(1000);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.displayedImage.setImageBitmap(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                binding.displayedImage.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
            } else {
                binding.displayedImage.setAlpha(0.1f);
            }
        }).addOnFailureListener(exception -> Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show());
        binding.letterGuessButton.setOnClickListener(v -> {
            String letter = binding.guessText.getText().toString();
            if (letter.length() == 1) {
                letterChecker(letter.charAt(0));
            } else {
                Toast.makeText(getContext(), "Please enter a single letter", Toast.LENGTH_SHORT).show();
            }
        });
        binding.fullGuessButton.setOnClickListener(v -> {
            String guess = binding.guessText.getText().toString();
            fullGuessChecker(guess);
        });


        return view;
    }

    public void underscoreCreator(){
        for (int i = 0; i < photoCaptionArray.length; i++) {
            if (Character.isLetter(photoCaptionArray[i])) {
                guessingArray[i] = '_';
            } else {
                guessingArray[i] = ' ';
            }
        }
    }

    public void letterChecker(char letter){
        if (!Character.isLetter(letter)) {
            Toast.makeText(getContext(), "Please enter a letter", Toast.LENGTH_SHORT).show();
            return;
        }
        letter = Character.toUpperCase(letter);
        if (usedLetters != null) {
            for (char usedLetter : usedLetters) {
                if (usedLetter == letter) {
                    Toast.makeText(getContext(), "You have already used this letter", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        boolean letterFound = false;
        for (int i = 0; i < photoCaptionArray.length; i++) {
            if (photoCaptionArray[i] == letter) {
                letterFound = true;
                guessingArray[i] = letter;
            }
        }
        if (letterFound) {
            if (usedLetters == null)
                usedLetters = new char[1];
            else
                usedLetters = new char[usedLetters.length + 1]; // Test this line
            usedLetters[usedLetters.length - 1] = letter;
            guessingArrayString = new String(guessingArray);
            binding.hangmanText.setText(guessingArrayString);
        } else {
            // TODO: Next player turn, weaken blur
        }
    }

    public void fullGuessChecker(String guess) {
        guess = guess.toUpperCase().trim();
        if (isAlphabeticalEnglish(guess)){
            if (guess.equals(photoCaptionText)) {
                guessingArray = photoCaptionArray;
                guessingArrayString = new String(guessingArray);
                binding.hangmanText.setText(guessingArrayString);
            }
        }
    }

    public boolean isAlphabeticalEnglish(String text) {
        if (text == null || text.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a caption", Toast.LENGTH_SHORT).show();
            binding.guessText.setError("Please enter a caption");
            binding.guessText.requestFocus();
            return false;
        }
        if (text.trim().length() > 30){
            binding.guessText.setError("Caption is too long");
            binding.guessText.requestFocus();
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                binding.guessText.setError("Please enter an alphabetical-only caption");
                binding.guessText.requestFocus();
                return false;
            }
        }
        return true;
    }

    //TODO: Add sub-folder "progress" once a game takes place.
    // The sub-folder will hold information such as used letters, guesses,
    // board messages, player turn, turn time, etc.
    // Add guessingArrayString event listener to update the board
}