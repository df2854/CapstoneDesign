package com.cookandroid.aerobicapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;
import com.cookandroid.aerobicapplication.route.RouteMain;
import com.cookandroid.aerobicapplication.userdata.CommentData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainMenuHomeFragment extends Fragment {
    private TextView recommendText;
    private int userScore = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_home, container, false);

        recommendText = view.findViewById(R.id.tv_recommendation);
        userScore = UserdataManager.getInstance().getUserScore();
        setRecommendText();

        return view;
    }

    private void setRecommendText(){
        if(userScore > 100)
            recommendText.setText(CommentData.over100);

        else if(userScore == 100)
            recommendText.setText(CommentData.standard);

        else if(userScore > 50)
            recommendText.setText(CommentData.over50);

        else
            recommendText.setText(CommentData.under50);

    }
}