package com.example.photoguess.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.photoguess.R;
import com.example.photoguess.controller.GameController;
import com.example.photoguess.controller.MyControllerSingleton;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GameController gameController = MyControllerSingleton.getInstance().getController();
        gameController.setContext(getApplicationContext());
        gameController.startBackgroundMusic();
    }

}
