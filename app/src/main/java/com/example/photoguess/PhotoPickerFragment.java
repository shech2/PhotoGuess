package com.example.photoguess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.databinding.FragmentPhotoPickerBinding;

public class PhotoPickerFragment extends Fragment {

    FragmentPhotoPickerBinding binding;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPhotoPickerBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        return view;
    }
}