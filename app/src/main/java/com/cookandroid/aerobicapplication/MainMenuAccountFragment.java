package com.cookandroid.aerobicapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;

public class MainMenuAccountFragment extends Fragment {
    private TextView showUserName, showUserBirthday, showUserHeight, showUserWeight,
            showUserDisease, showUserExperience, showUserBmi, showUserAge, showUserScore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_account, container, false);

        // TextView 초기화
        showUserName = view.findViewById(R.id.showUserName);
        showUserBirthday = view.findViewById(R.id.showUserBirthday);
        showUserHeight = view.findViewById(R.id.showUserHeight);
        showUserWeight = view.findViewById(R.id.showUserWeight);
        showUserDisease = view.findViewById(R.id.showUserDisease);
        showUserExperience = view.findViewById(R.id.showUserExperience);
        showUserBmi = view.findViewById(R.id.showUserBmi);
        showUserAge = view.findViewById(R.id.showUserAge);
        showUserScore = view.findViewById(R.id.showUserScore);

        // UserdataManager 인스턴스를 가져와서 데이터를 설정
        UserdataManager userManager = UserdataManager.getInstance();
        showUserName.setText("이름: " + userManager.getUserName());
        showUserBirthday.setText("생년월일: " + userManager.getUserBirthday());
        showUserHeight.setText("키: " + userManager.getUserHeight());
        showUserWeight.setText("몸무게: " + userManager.getUserWeight());
        showUserDisease.setText("질병: " + userManager.getUserDisease());
        showUserExperience.setText("운동 경험: " + userManager.getUserExperience());
        showUserBmi.setText("BMI: " + String.format("%.2f", userManager.getUserBmi()));
        showUserAge.setText("나이: " + userManager.getUserAge());
        showUserScore.setText("종합 점수: " + userManager.getUserScore());

        return view;
    }
}