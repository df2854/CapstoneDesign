package com.cookandroid.aerobicapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;

public class MainMenuAccountFragment extends Fragment {
    private TextView userNameLabel, userBirthdayLabel, userHeightLabel,
            userWeightLabel, userDiseaseLabel, userExperienceLabel;
    private TextView showUserName, showUserBirthday, showUserHeight, showUserWeight,
            showUserDisease, showUserExperience, showUserBmi, showUserAge, showUserScore;
    private EditText editUserName, editUserBirthday, editUserHeight, editUserWeight,
            editUserDisease, editUserExperience;
    private Button editButton, saveButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_account, container, false);

        // Label TextView 초기화
        userNameLabel = view.findViewById(R.id.userNameLabel);
        userBirthdayLabel = view.findViewById(R.id.userBirthdayLabel);
        userHeightLabel = view.findViewById(R.id.userHeightLabel);
        userWeightLabel = view.findViewById(R.id.userWeightLabel);
        userDiseaseLabel = view.findViewById(R.id.userDiseaseLabel);
        userExperienceLabel = view.findViewById(R.id.userExperienceLabel);

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
        // EditView 초기화
        editUserName = view.findViewById(R.id.editUserName);
        editUserBirthday = view.findViewById(R.id.editUserBirthday);
        editUserHeight = view.findViewById(R.id.editUserHeight);
        editUserWeight = view.findViewById(R.id.editUserWeight);
        editUserDisease = view.findViewById(R.id.editUserDisease);
        editUserExperience = view.findViewById(R.id.editUserExperience);
        // 버튼 초기화
        editButton = view.findViewById(R.id.editButton);
        saveButton = view.findViewById(R.id.saveButton);

        // UserdataManager 인스턴스를 가져와서 데이터를 설정
        UserdataManager userManager = UserdataManager.getInstance();
        setUserInfo(userManager);

        // 편집 버튼 클릭 시, EditText로 전환
        editButton.setOnClickListener(v -> switchToEditMode(true));

        // 저장 버튼 클릭 시, 데이터 저장 후 TextView로 전환
        saveButton.setOnClickListener(v -> {
            userManager.setUserData(
                    userManager.getUserId(),
                    editUserName.getText().toString(),
                    editUserBirthday.getText().toString(),
                    editUserHeight.getText().toString(),
                    editUserWeight.getText().toString(),
                    editUserDisease.getText().toString(),
                    editUserExperience.getText().toString()
            );
            setUserInfo(userManager);
            switchToEditMode(false);
        });

        return view;
    }

    // 편집 모드를 전환하는 메서드
    private void switchToEditMode(boolean isEditMode) {
        int textViewVisibility = isEditMode ? View.GONE : View.VISIBLE;
        int editTextVisibility = isEditMode ? View.VISIBLE : View.GONE;

        userNameLabel.setVisibility(View.VISIBLE);
        userBirthdayLabel.setVisibility(View.VISIBLE);
        userHeightLabel.setVisibility(View.VISIBLE);
        userWeightLabel.setVisibility(View.VISIBLE);
        userDiseaseLabel.setVisibility(View.VISIBLE);
        userExperienceLabel.setVisibility(View.VISIBLE);

        showUserName.setVisibility(textViewVisibility);
        editUserName.setVisibility(editTextVisibility);
        showUserBirthday.setVisibility(textViewVisibility);
        editUserBirthday.setVisibility(editTextVisibility);
        showUserHeight.setVisibility(textViewVisibility);
        editUserHeight.setVisibility(editTextVisibility);
        showUserWeight.setVisibility(textViewVisibility);
        editUserWeight.setVisibility(editTextVisibility);
        showUserDisease.setVisibility(textViewVisibility);
        editUserDisease.setVisibility(editTextVisibility);
        showUserExperience.setVisibility(textViewVisibility);
        editUserExperience.setVisibility(editTextVisibility);

        // 버튼 가시성 설정: 편집 모드에서는 editButton 숨기고 saveButton 표시
        editButton.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    // 유저 정보를 TextView에 설정하는 메서드
    private void setUserInfo(UserdataManager userManager) {
        showUserName.setText(userManager.getUserName());
        editUserName.setText(userManager.getUserName());
        showUserBirthday.setText(userManager.getUserBirthday());
        editUserBirthday.setText(userManager.getUserBirthday());
        showUserHeight.setText(userManager.getUserHeight() + " cm");
        editUserHeight.setText(userManager.getUserHeight());
        showUserWeight.setText(userManager.getUserWeight() + " kg");
        editUserWeight.setText(userManager.getUserWeight());
        showUserDisease.setText(userManager.getUserDisease());
        editUserDisease.setText(userManager.getUserDisease());
        showUserExperience.setText(userManager.getUserExperience());
        editUserExperience.setText(userManager.getUserExperience());

        showUserBmi.setText("BMI: " + String.format("%.2f", userManager.getUserBmi()));
        showUserAge.setText("나이: " + userManager.getUserAge());
        showUserScore.setText("종합 점수: " + userManager.getUserScore());
    }
}