package com.example.photoguess.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GameModel {
    boolean gameStarted = false;
    String myName;
    String roomPin;
    String photoUploader;
    ArrayList<String> playersArray;

    int playerPosition;
    int playerCount;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference roomRef;
    FirebaseStorage storage;
    StorageReference storageRef;


    public GameModel(){
        database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Rooms");
        roomRef = database.getReference("Rooms");
        storage = FirebaseStorage.getInstance("gs://photoguess-6deb1.appspot.com");
    }
    public void startGame(){
        this.gameStarted = true;
        setRandomUploader();
        roomRef.child("Time Left").setValue(30);
        roomRef.child("GameStarted").setValue(true);
    }
    public void setRandomUploader(){
        int random = (int) (Math.random() * playerCount);
//        roomRef.child("PhotoUploader").setValue(playersArray.get(random));
        roomRef.child("PhotoUploader").setValue("Liron");
        photoUploader = "Liron";
//        photoUploader = playersArray.get(random);
    }

    public FirebaseDatabase getDatabase(){
        return database;
    }

    public void setRoomPin(String roomPin) {
        this.roomPin = roomPin;
        roomRef = myRef.child("Room_" + roomPin);
    }

    public String getRoomPin() {
        return roomPin;
    }
    public void setMyName(String name) {
        this.myName = name;
    }

    public String getMyName() {
        return myName;
    }

    public DatabaseReference getRoomRef() {
        return roomRef;
    }

    public void setPlayerPosition(int playerPosition) {
        this.playerPosition = playerPosition;
    }

    public int getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayersArray(ArrayList<String> playersArray) {
        this.playersArray = playersArray;
    }

    public ArrayList<String> getPlayersArray() {
        return playersArray;
    }
    public void setPhotoUploader(String photoUploader) {
        this.photoUploader = photoUploader;
    }

    public String getPhotoUploader() {
        return photoUploader;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void setStorageRef(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }
}
