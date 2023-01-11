package com.example.photoguess;

import java.util.ArrayList;

public class Room {
    ArrayList<String> players;
    String roomPIN;

    // Constructor
    public Room(String roomPIN) {
        this.roomPIN = roomPIN;
        players = new ArrayList<>();
    }
}
