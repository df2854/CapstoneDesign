package com.cookandroid.aerobicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;
import com.cookandroid.aerobicapplication.userdata.CommentData;

public class MainMenuChallengeFragment extends Fragment {

    public static boolean loginSuccess = false;
    public static boolean firstExercise = false;
    private ImageView challenge1True, challenge1False, challenge2True, challenge2False;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_challenge, container, false);

        challenge1False = view.findViewById(R.id.challenge1False);
        challenge1True = view.findViewById(R.id.challenge1True);
        if(loginSuccess){
            challenge1True.setVisibility(view.VISIBLE);
            challenge1False.setVisibility(view.INVISIBLE);
        }

        challenge2False = view.findViewById(R.id.challenge2False);
        challenge2True = view.findViewById(R.id.challenge2True);
        if(firstExercise){
            challenge2True.setVisibility(view.VISIBLE);
            challenge2False.setVisibility(view.INVISIBLE);
        }

        return view;
    }

}