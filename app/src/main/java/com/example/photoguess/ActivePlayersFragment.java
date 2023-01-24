package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentActivePlayersBinding;

public class ActivePlayersFragment extends Fragment {

    FragmentActivePlayersBinding binding;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActivePlayersBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        return view;
    }
}