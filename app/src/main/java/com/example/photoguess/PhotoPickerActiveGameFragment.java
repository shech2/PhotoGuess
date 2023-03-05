package com.example.photoguess;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PhotoPickerActiveGameFragment extends Fragment {

    FragmentPhotoPickerActiveGameBinding binding;
    View view;
    String roomPin;
    Thread gameThread;
    DatabaseReference roomRef;
    DatabaseReference progressRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;
    String messageBoardText;
    ValueEventListener gameProgressListener;
    ValueEventListener skipTurnListener;

    String currentPlayerTurn;
    String guessingArrayString;

    String photoCaptionText;
    char[] photoCaptionArray;
    char[] guessingArray;
    List<String> usedLetters = new ArrayList<>();

    String[][] playersArray;
    int playerCount = 4;
    int playersArrayIterator = 0;
    int blurLevel = 100;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = this.getArguments();
        assert savedInstanceState != null;
        roomPin = savedInstanceState.getString("roomPin");
        binding = FragmentPhotoPickerActiveGameBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        progressRef = roomRef.child("GameProgress");
        view = binding.getRoot();
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

        binding.displayedImage.setImageResource(R.drawable.questionmark);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                int j = 0;
                playerCount = (int) snapshot.child("Players").getChildrenCount();
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
                playerCount--;
                messageBoardText = "Game Started";
                binding.MessageBoard.setText(messageBoardText);
                progressRef.child("MessageBoard").setValue(messageBoardText);
                currentPlayerTurn = playersArray[1][0];
                progressRef.child("CurrentPlayerTurn").setValue(currentPlayerTurn);
                progressRef.child("BlurLevel").setValue(100);
                progressRef.child("Timer").setValue(30);
                photoCaptionText = snapshot.child("Caption").getValue(String.class);
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

        gameProgressListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("MessageBoard").getValue() != null){
                    messageBoardText = snapshot.child("MessageBoard").getValue().toString();
                    binding.MessageBoard.setText(messageBoardText);
                }

                if (blurLevel != snapshot.child("BlurLevel").getValue(Integer.class)) {
                    blurLevel = snapshot.child("BlurLevel").getValue(Integer.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (blurLevel == 0) {
                            binding.displayedImage.setRenderEffect(null);
                        } else {
                            binding.displayedImage.setRenderEffect(RenderEffect.createBlurEffect(blurLevel, blurLevel, Shader.TileMode.MIRROR));
                        }
                    } else {
                        binding.displayedImage.setAlpha(0.1f);
                    }
                }

                if (snapshot.child("UsedLetters").getValue() != null) {
                    usedLetters = (List<String>) snapshot.child("UsedLetters").getValue();
                }
                if (snapshot.child("CurrentGuess").getValue() != null){
                    String cg = snapshot.child("CurrentGuess").getValue(String.class);
                    if (!Objects.equals(cg, guessingArrayString)){
                        guessingArrayString = snapshot.child("CurrentGuess").getValue(String.class);
                        guessingArray = guessingArrayString.toCharArray();
                        binding.hangmanText.setText(guessingArrayString);
                    }
                }

                if (snapshot.child("Winner").getValue() != null){
                    gameThread.interrupt();
                    String winner = Objects.requireNonNull(snapshot.child("Winner").getValue()).toString();
                    progressRef.child("BlurLevel").setValue(0);
                    progressRef.child("CurrentGuess").setValue(photoCaptionText);
                    progressRef.child("MessageBoard")
                            .setValue(winner + " has won the round!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        progressRef.addValueEventListener(gameProgressListener);
        gameStart();
        return view;
    }

    public void gameStart(){
        gameThread = new Thread(new Runnable() {

            int counter = 10;
            @Override
            public void run() {
                skipTurnListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("SkipTurn").getValue() != null){
                            if (snapshot.child("SkipTurn").getValue().toString().equals("true") && counter > 1){
                                counter = 1;
                                progressRef.child("SkipTurn").setValue(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                progressRef.addValueEventListener(skipTurnListener);
                while (true){
                    counter = 10;
                    while (counter > 0) {
                        try {
                            Thread.sleep(1000);
                            counter--;
                            progressRef.child("Timer").setValue(counter);
                            updateMessageBoard(counter);

                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    nextPlayerTurn();
                }
            }
        });
        gameThread.start();
    }

    public void nextPlayerTurn(){
        if (playersArrayIterator == playerCount - 1){
            playersArrayIterator = 0;
        } else {
            playersArrayIterator++;
        }
        currentPlayerTurn = playersArray[1][playersArrayIterator];
        progressRef.child("CurrentPlayerTurn").setValue(currentPlayerTurn);
        if (blurLevel > 0)
            blurLevel -= 10;
        progressRef.child("BlurLevel").setValue(blurLevel);


    }

    public void updateMessageBoard(int counter){
            progressRef.child("MessageBoard")
                .setValue("It is now " + currentPlayerTurn + "'s turn " + counter);
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
}