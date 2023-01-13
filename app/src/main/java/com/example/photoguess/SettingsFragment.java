package com.example.photoguess;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.photoguess.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    View view;
    FragmentSettingsBinding binding;
    AudioManager audioManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Binding the layout to the fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        binding.homeButton.setOnClickListener(view -> replaceFragment(new MenuFragment()));

        // AudioManger init
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // maxVolume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        // currentVolume
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

        // Set the max volume of the seekbar
        binding.MasterVolumeSeekbar.setMax(maxVolume);
        // Set the current volume of the seekbar
        binding.MasterVolumeSeekbar.setProgress(currentVolume);


        // SeekBar init
        binding.MasterVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // maxVolume
        int MusicMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // currentVolume
        int MusicCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // Set the max volume of the seekbar
        binding.MusicSeekbar.setMax(MusicMaxVolume);
        // Set the current volume of the seekbar
        binding.MusicSeekbar.setProgress(MusicCurrentVolume);


        // MusicSeekBar init
        binding.MusicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // maxVolume
        int SFXMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        // currentVolume
        int SFXCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        // Set the max volume of the seekbar
        binding.MusicSeekbar.setMax(SFXMaxVolume);
        // Set the current volume of the seekbar
        binding.MusicSeekbar.setProgress(SFXCurrentVolume);


        // MusicSeekBar init
        binding.SFXSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }



}