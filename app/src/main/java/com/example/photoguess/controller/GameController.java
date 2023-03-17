package com.example.photoguess.controller;

import com.example.photoguess.model.GameModel;
import com.example.photoguess.model.MyModelSingleton;

import java.util.ArrayList;

public class GameController {

    GameModel gameModel;

    public GameController() {
        gameModel = MyModelSingleton.getInstance().getModel();
    }

    public void startGame(int playerPosition, int playersCount,ArrayList<String> playersArray) {
        gameModel.setPlayerPosition(playerPosition);
        gameModel.setPlayerCount(playersCount);
        gameModel.setPlayersArray(playersArray);
        gameModel.startGame();
    }

    public void setRoomPin(String pin){
        gameModel.setRoomPin(pin);
    }

    public String getRoomPin() {
        return gameModel.getRoomPin();
    }
    public void setName(String name){
        gameModel.setMyName(name);
    }

    public String getName(){
        return gameModel.getMyName();
    }

    public void setPosition(int playerPosition) {
        gameModel.setPlayerPosition(playerPosition);
    }

    public int getPosition() {
        return gameModel.getPlayerPosition();
    }

    public void setPlayersCount(int playerCount) {
        gameModel.setPlayerCount(playerCount);
    }

    public int getPlayersCount() {
        return gameModel.getPlayerCount();
    }

    public void setPlayersArray(ArrayList<String> playersArray){
        gameModel.setPlayersArray(playersArray);
    }

    public ArrayList<String> getPlayersArray(){
        return gameModel.getPlayersArray();
    }

    public void setPhotoUploader(String photoUploader) {
        gameModel.setPhotoUploader(photoUploader);
    }
    public String getPhotoUploader() {
        return gameModel.getPhotoUploader();
    }

}
