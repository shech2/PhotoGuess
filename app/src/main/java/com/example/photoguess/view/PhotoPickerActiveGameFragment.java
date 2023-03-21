package com.example.photoguess.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentPhotoPickerActiveGameBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoPickerActiveGameFragment extends BaseFragment {
    FragmentPhotoPickerActiveGameBinding binding;
    String roomPin;
    Thread gameThread;
    DatabaseReference roomRef;
    DatabaseReference gameProgressRef;
    StorageReference storageRef;
    String messageBoardText;
    ValueEventListener gameProgressListener;
    ValueEventListener skipTurnListener;

    String currentPlayerTurn;
    String guessingArrayString;

    String photoCaptionText;
    String winner;
    char[] photoCaptionArray;
    char[] guessingArray;
    List<String> usedLetters = new ArrayList<>();
    ImageView displayedImage;
    String[][] activePlayersArray;
    int playerCount = 4;
    int playersArrayIterator = 0;
    int blurLevel = 100;

    int messageStage = 0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        messageStage = 0;
        roomPin = gameController.getRoomPin();
        roomRef = gameModel.getRoomRef();
        gameProgressRef = roomRef.child("GameProgress");
        binding = FragmentPhotoPickerActiveGameBinding.inflate(inflater, container, false);
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
        displayedImage = binding.getRoot().findViewById(R.id.displayedImage);
        storageRef = gameController.getStorageRef();
        storageRef.getBytes(1024 * 1024).addOnSuccessListener(bytes -> {
            SystemClock.sleep(1000);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            displayedImage.setImageBitmap(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (blurLevel <= 0) {
                    displayedImage.setRenderEffect(null);
                } else {
                    displayedImage.setRenderEffect(RenderEffect.createBlurEffect(blurLevel, blurLevel, Shader.TileMode.MIRROR));


                }
            } else {
                displayedImage.setAlpha(0.1f);
            }
            playSound(R.raw.gamestart);
        }).addOnFailureListener(exception -> Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show());

        displayedImage.setImageResource(R.drawable.questionmark);
        binding.deblurButton.setOnClickListener(v -> {
            if (blurLevel > 0){
                blurLevel -= 5;
            }
            gameProgressRef.child("BlurLevel").setValue(blurLevel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (blurLevel <= 0) {
                    displayedImage.setRenderEffect(null);
                } else {
                    displayedImage.setRenderEffect(RenderEffect.createBlurEffect(blurLevel, blurLevel, Shader.TileMode.MIRROR));
                }
            } else {
                displayedImage.setAlpha(0.1f);
            }
        });
        binding.giveHintButton.setOnClickListener(v -> {
            String letter = binding.enterHintText.getText().toString();
            if (letter.length() == 1) {
                letterChecker(letter.charAt(0));
            } else {
                Toast.makeText(getContext(), "Please enter a single letter", Toast.LENGTH_SHORT).show();
            }
        });
        binding.skipTurnButton.setOnClickListener(v -> {
            gameProgressRef.child("SkipTurn").setValue(true);
        });
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                int j = 0;
                playerCount = gameController.getPlayersCount();
                activePlayersArray = new String[2][playerCount - 1];
                for (int i = 0; i < playerCount; i++) {
                    if (!Objects.equals(gameController.getPlayersArray().get(i), gameController.getPhotoUploader())){
                        activePlayersArray[0][j] = "Player" + (i + 1);
                        activePlayersArray[1][j] = gameController.getPlayersArray().get(i);
                        j++;
                    }
                }
                playerCount--;
                messageBoardText = "Game Started";
                binding.MessageBoard.setText(messageBoardText);
                gameProgressRef.child("MessageBoard").setValue(messageBoardText);
                currentPlayerTurn = activePlayersArray[1][0];
                gameProgressRef.child("CurrentPlayerTurn").setValue(currentPlayerTurn);
                gameProgressRef.child("BlurLevel").setValue(100);
                gameProgressRef.child("Timer").setValue(30);
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
                        if (blurLevel <= 0) {
                            displayedImage.setRenderEffect(null);
                        } else {
                            displayedImage.setRenderEffect(RenderEffect.createBlurEffect(blurLevel, blurLevel, Shader.TileMode.MIRROR));
                        }
                    } else {
                        displayedImage.setAlpha(0.1f);
                    }
                }

                if (snapshot.child("UsedLetters").getValue() != null) {
                    usedLetters = (List<String>) snapshot.child("UsedLetters").getValue();
                    binding.updatingUsedLettersText.setText(usedLetters.toString());
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
                    winner = Objects.requireNonNull(snapshot.child("Winner").getValue()).toString();
                    endGameSequence();
                }

                if (snapshot.child("Restart").getValue() != null){
                    replaceFragment(new WaitingRoomFragment());
                }

                if (snapshot.child("GameOver").getValue() != null){
                    SystemClock.sleep(2000);
                    gameProgressRef.child("GameOver").removeValue();
                    replaceFragment(new MenuFragment());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        gameProgressRef.addValueEventListener(gameProgressListener);
        gameStart();
        return view;
    }

    public void endGameSequence(){


        gameThread.interrupt();
        playSound(R.raw.gamewinner);
        gameController.setPhotoUploader(winner);
        gameThread = new Thread(() -> {
            if (messageStage == 0){
                gameProgressRef.child("BlurLevel").setValue(0);
                gameProgressRef.child("CurrentGuess").setValue(photoCaptionText);
                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("WinnerList").getValue() != null && snapshot.child("WinnerList").hasChild(winner)){
                                gameProgressRef.child("MessageBoard")
                                        .setValue(winner + " has won the Game!");
                                messageStage = 4;


                        } else {

                            gameProgressRef.child("MessageBoard")
                                    .setValue(winner + " has won the round!");
                            for (int i = 0; i < playerCount; i++) {
                                if (activePlayersArray[1][i].equals(winner)){
                                    roomRef.child("PhotoUploader").setValue(activePlayersArray[0][i]);
                                }
                            }
                            messageStage = 1;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else if (messageStage == 1){
                SystemClock.sleep(3000);
                roomRef.child("WinnerList").child(winner).setValue(winner);
                gameProgressRef.child("MessageBoard")
                        .setValue("Next round will begin momentarily");
                messageStage = 2;
            }
            else if (messageStage == 2){
                SystemClock.sleep(2000);
                gameProgressRef.child("Restart").setValue(true);
                messageStage = 3;
                gameThread.interrupt();
            }
            else if (messageStage == 4){
                SystemClock.sleep(5000);
                gameProgressRef.child("GameOver").setValue(true);
                gameThread.interrupt();
            }
        });
        gameThread.start();
    }
    public void gameStart(){
        gameThread = new Thread(new Runnable() {

            int counter = 30;
            @Override
            public void run() {
                skipTurnListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("SkipTurn").getValue() != null){
                            if (snapshot.child("SkipTurn").getValue().toString().equals("true") && counter > 1){
                                counter = 1;
                                gameProgressRef.child("SkipTurn").setValue(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                gameProgressRef.addValueEventListener(skipTurnListener);
                while (true){
                    counter = 30;
                    while (counter > 0) {
                        try {
                            Thread.sleep(1000);
                            counter--;
                            gameProgressRef.child("Timer").setValue(counter);
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
        currentPlayerTurn = activePlayersArray[1][playersArrayIterator];
        gameProgressRef.child("CurrentPlayerTurn").setValue(currentPlayerTurn);
        if (blurLevel > 0)
            blurLevel -= 10;
        gameProgressRef.child("BlurLevel").setValue(blurLevel);


    }

    public void updateMessageBoard(int counter){
            gameProgressRef.child("MessageBoard")
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

    public void letterChecker(char letter){
        if (!Character.isLetter(letter)) {
            Toast.makeText(getContext(), "Please enter a letter", Toast.LENGTH_SHORT).show();
            return;
        }
        letter = Character.toUpperCase(letter);
        if (usedLetters != null) {
            for (String usedLetter : usedLetters) {
                if (usedLetter.charAt(0) == letter) {
                    Toast.makeText(getContext(), "Letter already used", Toast.LENGTH_SHORT).show();
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
        usedLetters.add(String.valueOf(letter));
        gameProgressRef.child("UsedLetters").setValue(usedLetters);
        if (letterFound) {
            guessingArrayString = new String(guessingArray);
            binding.hangmanText.setText(guessingArrayString);
            gameProgressRef.child("CurrentGuess").setValue(guessingArrayString);
        }


    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        gameProgressRef.removeEventListener(gameProgressListener);
        gameProgressRef.removeEventListener(skipTurnListener);
        gameProgressRef.removeValue();

    }

}