package com.cookandroid.aerobicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;
import com.cookandroid.aerobicapplication.userdata.CommentData;

public class MainMenuChallengeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_challenge, container, false);

        return view;
    }

}