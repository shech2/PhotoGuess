package com.example.photoguess;

import android.annotation.SuppressLint;
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
    Thread gameThread;
    DatabaseReference roomRef;
    DatabaseReference progressRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;
    String messageBoardText;
    ValueEventListener gameProgressListener;

    String currentPlayerTurn;

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
        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
        storageRef = storage.getReference().child("Room_" + roomPin);
        view = binding.getRoot();
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
                System.out.println("Players Array: " + Arrays.deepToString(playersArray));

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
            int counter = 5;
            @Override
            public void run() {
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
}