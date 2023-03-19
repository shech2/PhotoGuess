package com.example.photoguess.view;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentRouletteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import kotlin.jvm.Volatile;

public class RouletteFragment extends BaseFragment {

    FragmentRouletteBinding binding;
    ArrayList<User> playersList = new ArrayList<>();
    int playerCount;
    int playerPosition;
    static volatile int arrowPosition = 0;
    static volatile int prevArrowPosition = 1;
    DatabaseReference roomRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        playerPosition = gameController.getPosition();
        playerCount = gameController.getPlayersCount();
        roomRef = gameModel.getRoomRef();
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("PhotoUploader").getValue() != null) {
                    String photoUploader = Objects.requireNonNull(snapshot.child("PhotoUploader").getValue()).toString();
                    gameController.setPhotoUploader(photoUploader);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding = FragmentRouletteBinding.inflate(inflater, container, false);
        view = binding.getRoot();
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

        initialize();
        return view;
    }

    private void spinRoulette() {



        arrowPosition = 0;
        prevArrowPosition = -1;
        final boolean[] stop = {false};
        final int[] delay = {100};
        final int[] jumps = {1};
        Handler mHandler = new Handler();
        Runnable mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                incArrowPosition();
                if (stop[0]){
                    nextFragment();
                }
                else{
                    toggleArrowVisibility(arrowPosition, prevArrowPosition);
                    view = binding.roomListView.getChildAt(arrowPosition);
                    MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.dingcut);
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                    if (jumps[0] < 70){
                        mHandler.postDelayed(this, delay[0]);
                        if (jumps[0] % 58 == 0){
                            delay[0] += 100;
                        }
                        else if (jumps[0] > 58 && jumps[0] % 5 == 0){
                            delay[0] += 100;
                        }
                        jumps[0]++;
                    }
                    else{
                        if (Objects.equals(playersList.get(arrowPosition).getName(), gameController.getPhotoUploader())){
                            stop[0] = true;
                            mHandler.postDelayed(this, delay[0] + 2000);
                            mediaPlayer = MediaPlayer.create(getContext(), R.raw.pickerchosen);
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                        }
                        else
                            mHandler.postDelayed(this, delay[0]);
                    }
                }
                }
        };

        mHandler.postDelayed(mUpdateRunnable,500);
    }

    private void showArrow(int pos) {
        view = binding.roomListView.getChildAt(pos);
        if (view != null) {
            View arrow = view.findViewById(R.id.arrow);
            if (arrow != null) {
                arrow.setVisibility(View.VISIBLE);

            }
        }
    }

    private void hideArrow(int pos){
        view = binding.roomListView.getChildAt(pos);
        if (view != null) {
            View arrow = view.findViewById(R.id.arrow);
            if (arrow != null) {
                arrow.setVisibility(View.INVISIBLE);

            }
        }

    }
    private void toggleArrowVisibility(int pos, int prevPos) {
        hideArrow(prevPos);
        showArrow(pos);
    }

    public void incArrowPosition(){
        prevArrowPosition = arrowPosition;
        if (arrowPosition == playerCount - 1)
            arrowPosition = 0;
        else
            arrowPosition++;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void initialize(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gameController.getPlayersArray().forEach(player -> playersList.add(new User(player)));
        }
        ListAdapter adapter = new ListAdapter(getContext(), playersList);
        binding.roomListView.setAdapter(adapter);
        spinRoulette();
    }

    private void nextFragment() {
        if (Objects.equals(gameController.getName(), gameController.getPhotoUploader())) {
            replaceFragment(new PhotoPickerFragment());
        }else{
            replaceFragment(new WaitingRoomFragment());
        }
    }



}