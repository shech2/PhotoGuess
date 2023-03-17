package com.example.photoguess.controller;
public class MyControllerSingleton {
    private static MyControllerSingleton instance;
    private GameController controller;

    private MyControllerSingleton() {
        controller = new GameController();
    }

    public static synchronized MyControllerSingleton getInstance() {
        if (instance == null) {
            instance = new MyControllerSingleton();
        }
        return instance;
    }

    public GameController getController() {
        return controller;
    }
}
