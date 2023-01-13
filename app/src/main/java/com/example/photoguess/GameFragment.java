package com.example.photoguess;

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

import com.example.photoguess.databinding.FragmentGameBinding;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding;
    ActivityResultLauncher<Void> takePicture;
    ActivityResultLauncher<String> Gallery;
    View view;

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

}