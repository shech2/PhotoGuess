package com.example.photoguess.controller;

import android.os.Bundle;

import com.example.photoguess.model.GameModel;
import com.example.photoguess.model.MyModelSingleton;

public class GameController {

    GameModel gameModel;

    public GameController() {
        gameModel = MyModelSingleton.getInstance().getModel();
    }

    public void startGame() {
            gameModel.startGame();
    }

    public void setRoomPin(String pin){
        gameModel.setRoomPin(pin);
    }

}
