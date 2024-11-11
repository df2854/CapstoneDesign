package com.cookandroid.aerobicapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;

public class MainMenuAccountFragment extends Fragment {
    private TextView showUserName, showUserBirthday, showUserHeight, showUserWeight,
            showUserDisease, showUserExperience, showUserBmi, showUserAge, showUserScore;
    private EditText editUserName, editUserBirthday, editUserHeight, editUserWeight,
            editUserDisease, editUserExperience;
    private Button editButton, saveButton;
    private ImageView accountIvProfile;
    private ImageView accountIvProfileCamera;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_account, container, false);

        // TextView 및 EditText 초기화
        showUserName = view.findViewById(R.id.showUserName);
        showUserBirthday = view.findViewById(R.id.showUserBirthday);
        showUserHeight = view.findViewById(R.id.showUserHeight);
        showUserWeight = view.findViewById(R.id.showUserWeight);
        showUserDisease = view.findViewById(R.id.showUserDisease);
        showUserExperience = view.findViewById(R.id.showUserExperience);
        showUserBmi = view.findViewById(R.id.showUserBmi);
        showUserAge = view.findViewById(R.id.showUserAge);
        showUserScore = view.findViewById(R.id.showUserScore);

        editUserName = view.findViewById(R.id.editUserName);
        editUserBirthday = view.findViewById(R.id.editUserBirthday);
        editUserHeight = view.findViewById(R.id.editUserHeight);
        editUserWeight = view.findViewById(R.id.editUserWeight);
        editUserDisease = view.findViewById(R.id.editUserDisease);
        editUserExperience = view.findViewById(R.id.editUserExperience);

        // 버튼 초기화
        editButton = view.findViewById(R.id.editButton);
        saveButton = view.findViewById(R.id.saveButton);

        // 프로필 이미지뷰 초기화
        accountIvProfile = view.findViewById(R.id.account_iv_profile);
        accountIvProfileCamera = view.findViewById(R.id.account_iv_profile_camera);

        // 저장된 프로필 이미지 URI를 불러와 설정
        loadProfileImage();

        // ActivityResultLauncher 초기화 (프로필 이미지 선택)
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri imageUri = data.getData();
                            accountIvProfile.setImageURI(imageUri);
                            saveProfileImageUri(imageUri); // 선택한 이미지 URI를 저장
                        }
                    }
                }
        );

        // 카메라 아이콘 클릭 시 갤러리 열기 (프로필 사진 수정 전용)
        accountIvProfileCamera.setOnClickListener(v -> openImageChooser());

        // UserdataManager 인스턴스를 가져와서 데이터를 설정
        UserdataManager userManager = UserdataManager.getInstance();
        setUserInfo(userManager);

        // 카드뷰 내 정보 수정 버튼 클릭 시, EditText로 전환
        editButton.setOnClickListener(v -> switchToEditMode(true));

        // 카드뷰 내 정보 저장 버튼 클릭 시, 데이터 저장 후 TextView로 전환
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

    // 이미지 선택기 열기 (프로필 사진용)
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // 선택된 이미지 URI를 SharedPreferences에 저장
    private void saveProfileImageUri(Uri imageUri) {
        if (imageUri != null) {
            getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE)
                    .edit()
                    .putString(KEY_PROFILE_IMAGE_URI, imageUri.toString())
                    .apply();
        }
    }

    // 저장된 이미지 URI를 불러와서 프로필 이미지에 설정
    private void loadProfileImage() {
        String savedImageUri = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE)
                .getString(KEY_PROFILE_IMAGE_URI, null);

        if (savedImageUri != null) {
            Uri imageUri = Uri.parse(savedImageUri);
            accountIvProfile.setImageURI(imageUri);
        }
    }

    // 카드뷰 내 정보 수정 모드를 전환하는 메서드
    private void switchToEditMode(boolean isEditMode) {
        int textViewVisibility = isEditMode ? View.GONE : View.VISIBLE;
        int editTextVisibility = isEditMode ? View.VISIBLE : View.GONE;

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

        // 버튼 가시성 설정: 수정 모드에서는 editButton 숨기고 saveButton 표시
        editButton.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    // 카드뷰 내 유저 정보를 TextView에 설정하는 메서드
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

        showUserBmi.setText("BMI   " + String.format("%.2f", userManager.getUserBmi()));
        showUserAge.setText("나이   " + userManager.getUserAge());
        showUserScore.setText("종합 점수   " + userManager.getUserScore());
    }
}
