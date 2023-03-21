package com.example.photoguess.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentHowToPlayBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class HowToPlayFragment extends BaseFragment {

    FragmentHowToPlayBinding binding;
    ArrayList<Integer> list = new ArrayList<>();
    int listIterator;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listIterator = 0;
        binding = FragmentHowToPlayBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.homeButton.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.PhotoIV.setImageResource(R.drawable.images);
        list.add(R.drawable.images);
        list.add(R.drawable.screenshot1);
        list.add(R.drawable.screenshot2);
        list.add(R.drawable.screenshot3);
        list.add(R.drawable.screenshot4);
        list.add(R.drawable.screenshot5);
        list.add(R.drawable.screenshot6);
        list.add(R.drawable.screenshot7);
        updatePageIndex();

        binding.NextBTN.setOnClickListener(view ->{
            if (listIterator < list.size()-1)
                binding.PhotoIV.setImageResource(list.get(++listIterator));
            else{
                listIterator = 0;
                binding.PhotoIV.setImageResource(list.get(0));
            }
            updatePageIndex();
        });

        binding.PrevBTN.setOnClickListener(view ->{
            if (listIterator > 0)
                binding.PhotoIV.setImageResource(list.get(--listIterator));
            else{
                listIterator = list.size()-1;
                binding.PhotoIV.setImageResource(list.get(listIterator));
            }
            updatePageIndex();
        });

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


        return view;
    }

    @SuppressLint("SetTextI18n")
    public void updatePageIndex(){
        binding.pageIndex.setText((listIterator+1) + "/" + list.size());
    }


}