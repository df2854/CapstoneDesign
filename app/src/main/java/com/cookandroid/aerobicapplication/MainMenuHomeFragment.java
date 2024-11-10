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

public class MainMenuHomeFragment extends Fragment {
    private TextView recommendText;
    private int userScore = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_home, container, false);

        recommendText = view.findViewById(R.id.recommendText);
        userScore = UserdataManager.getInstance().getUserScore();
        setRecommendText();
        Log.d("TAGGGG",String.valueOf(userScore));

        // '운동 시작' 버튼 클릭 시 StartWorkoutActivity로 이동
        Button startButton = view.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RouteMain.class);
//                intent.putExtra("startGps", true); // 인텐트에 GPS 시작 플래그 추가
                startActivity(intent);
            }
        });

        Button createStartButton = view.findViewById(R.id.workoutLog);
        createStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkoutLogActivity.class);
                startActivity(intent);
            }
        });

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