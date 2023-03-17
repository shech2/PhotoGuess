package com.example.photoguess.model;

public class MyModelSingleton {
    private static MyModelSingleton instance;
    private GameModel model;

    private MyModelSingleton() {
        model = new GameModel();
    }

    public static synchronized MyModelSingleton getInstance() {
        if (instance == null) {
            instance = new MyModelSingleton();
        }
        return instance;
    }

    public GameModel getModel() {
        return model;
    }
}
