package com.example.photoguess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.photoguess.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
