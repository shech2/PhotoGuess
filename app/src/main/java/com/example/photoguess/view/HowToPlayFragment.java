package com.example.photoguess.view;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentHowToPlayBinding;

import java.util.ArrayList;

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
        binding.PhotoIV.setImageResource(R.drawable.lobbyscreenshot);
        list.add(R.drawable.lobbyscreenshot);
        list.add(R.drawable.roulettescreenshot);
        binding.NextBTN.setOnClickListener(view -> {
            if (binding.PhotoIV.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.lobbyscreenshot).getConstantState()) {
                binding.PhotoIV.setImageResource(R.drawable.roulettescreenshot);
            } else if (binding.PhotoIV.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.roulettescreenshot).getConstantState()) {
                binding.PhotoIV.setImageResource(R.drawable.lobbyscreenshot);
            }
        });
        binding.PrevBTN.setOnClickListener(view -> {
            if (binding.PhotoIV.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.lobbyscreenshot).getConstantState()) {
                binding.PhotoIV.setImageResource(R.drawable.roulettescreenshot);
            } else if (binding.PhotoIV.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.roulettescreenshot).getConstantState()) {
                binding.PhotoIV.setImageResource(R.drawable.lobbyscreenshot);
            }
        });


        return view;
    }


}