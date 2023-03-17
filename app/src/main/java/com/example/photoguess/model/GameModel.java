package com.example.photoguess.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameModel {
    boolean gameStarted = false;
    String myName;
    String roomPin;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference roomRef;

    public GameModel(){
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Rooms");
        roomRef = database.getReference("Rooms");
    }
    public void startGame(){
        this.gameStarted = true;
        setRandomUploader();
        roomRef.child("Time Left").setValue(30);
        roomRef.child("GameStarted").setValue(true);
    }
    public void setRandomUploader(){
        roomRef.child("PhotoUploader").setValue("Player1");
//        int random = (int) (Math.random() * playersCount + 1);
//        roomRef.child("PhotoUploader").setValue("Player"+random);
    }

    public FirebaseDatabase getDatabase(){
        return database;
    }

    public void setRoomPin(String roomPin) {
        this.roomPin = roomPin;
        roomRef = myRef.child("Room_" + roomPin);
    }
}
