package com.example.photoguess.view;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.photoguess.controller.GameController;
import com.example.photoguess.controller.MyControllerSingleton;
import com.example.photoguess.databinding.FragmentMenuBinding;


import com.example.photoguess.R;
import com.example.photoguess.model.GameModel;
import com.example.photoguess.model.MyModelSingleton;

public class BaseFragment extends Fragment {
    View view;
    String name;
    GameModel gameModel;
    GameController gameController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameController = MyControllerSingleton.getInstance().getController();
        gameModel = MyModelSingleton.getInstance().getModel();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, fragment);
        fragmentTransaction.commit();
    }
}
