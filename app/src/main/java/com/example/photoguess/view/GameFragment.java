package com.example.photoguess.view;

import android.graphics.Bitmap;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.photoguess.R;
import com.example.photoguess.databinding.FragmentGameBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class GameFragment extends Fragment {

    private FragmentGameBinding binding;
    ActivityResultLauncher<Void> takePicture;
    ActivityResultLauncher<String> Gallery;
    ArrayList<User> playersList = new ArrayList<>();
    View view;
    int currentPos = 0;
    FirebaseDatabase database;
    DatabaseReference roomRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bind the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater, container, false);

        // Get the root view of the layout
        view = binding.getRoot();

        // ActivityLauncher init
        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
          binding.Logo.setImageBitmap(result);
          // Save the result to the Gallery
                addImageToGallery(result);
            // Blur the image (Play with the radius for a better result) --> for SDK 30+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                binding.Logo.setRenderEffect(RenderEffect.createBlurEffect(20, 20, Shader.TileMode.MIRROR));
            }
        });
        // Open camera with the launcher
        binding.cameraLaunchBTN.setOnClickListener(view -> takePicture.launch(null));
        // ActivityLauncher init
        Gallery = registerForActivityResult(new ActivityResultContracts.GetContent() , result -> binding.Logo.setImageURI(result));
        // Open gallery with the launcher
        binding.galleryLaunchBTN.setOnClickListener(view -> Gallery.launch("image/*"));

        // Return the root view of the layout
        binding.BackBTN.setOnClickListener(view -> replaceFragment(new MenuFragment()));
        savedInstanceState = this.getArguments();
        if (savedInstanceState != null) {
            String roomPin = savedInstanceState.getString("roomPin");
            System.out.println("roomPin " + roomPin);
            database = FirebaseDatabase.getInstance("https://photoguess-6deb1-default-rtdb.europe-west1.firebasedatabase.app/");
            roomRef = database.getReference("Rooms").child("Room_" + roomPin);
        }
        initialize();

        binding.roomShuffle.setOnItemClickListener((parent, view, position, id) -> {
            currentPos = position;
        });
        // Return the root view
        return view;
    }

    // This method is called when the fragment is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Replace the current fragment with the given fragment
    private void replaceFragment(Fragment fragment) {
        // Get the fragment manager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        // Get the fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the current fragment with the given fragment
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void addImageToGallery(final Bitmap picture) {
        // Save the image to the gallery
        MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), picture, "PhotoGuess", "PhotoGuess");
    }

    public void initialize(){
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               int playerCount = (int) snapshot.child("Players").getChildrenCount();
                for (DataSnapshot pNumber : snapshot.child("Players").getChildren()) {
                    for (DataSnapshot pName : pNumber.getChildren()) {
                        playersList.add(new User(Objects.requireNonNull(pName.getValue()).toString()));
                    }
                }
                ListAdapter adapter = new ListAdapter(getContext(), playersList);
                binding.roomShuffle.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



}