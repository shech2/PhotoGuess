package com.example.photoguess.view;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentHowToPlayBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class HowToPlayFragment extends BaseFragment {

    FragmentHowToPlayBinding binding;
    ArrayList<Integer> list = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Binding the layout to the fragment
        binding = FragmentHowToPlayBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.homeButton.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        binding.PhotoIV.setImageResource(R.drawable.one);
        list.add(R.drawable.one);
        list.add(R.drawable.two);
        list.add(R.drawable.three);
        list.add(R.drawable.four);
        list.add(R.drawable.five);
        list.add(R.drawable.six);
        list.add(R.drawable.seven);
        ListIterator<Integer> listIterator = list.listIterator();

        // Next button iterates through the list of images
        binding.NextBTN.setOnClickListener(view ->{
            if(listIterator.hasNext())
                binding.PhotoIV.setImageResource(listIterator.next());
        });
        binding.PrevBTN.setOnClickListener(view ->{
            if(listIterator.hasPrevious())
                binding.PhotoIV.setImageResource(listIterator.previous());
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


}