package com.example.photoguess.view;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.photoguess.controller.GameController;
import com.example.photoguess.controller.MyControllerSingleton;

import com.example.photoguess.R;
import com.example.photoguess.model.GameModel;
import com.example.photoguess.model.MyModelSingleton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseFragment extends Fragment {
    View view;
    GameModel gameModel;
    GameController gameController;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameController = MyControllerSingleton.getInstance().getController();
        gameModel = MyModelSingleton.getInstance().getModel();
        database = gameModel.getDatabase();
        myRef = database.getReference("Rooms");
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }
}
