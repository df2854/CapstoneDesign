package com.cookandroid.aerobicapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.cookandroid.aerobicapplication.userdata.CompleteListener;
import com.cookandroid.aerobicapplication.userdata.LoginWithData;

public class LoginActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "LoginPrefs";
    EditText id,pw;
    CheckBox autoLoginCheckBox;
    private LoginWithData loginWithData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoadingDialog loadingDialog = new LoadingDialog();

        id=findViewById(R.id.editID);
        pw=findViewById(R.id.editPassword);
        autoLoginCheckBox = findViewById(R.id.autoLoginCheckBox);
        loginWithData = new LoginWithData();

        // 자동 로그인 여부 확인
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedId = preferences.getString("userId", null);
        String savedPw = preferences.getString("userPw", null);
        boolean isAutoLoginEnabled = preferences.getBoolean("autoLoginEnabled", false);

        if (isAutoLoginEnabled && savedId != null && savedPw != null) {
            startMainActivity(); // 자동 로그인
        }

        TextView sign = (TextView) findViewById(R.id.signup);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        Button login = (Button) findViewById(R.id.loginbutton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (TextUtils.isEmpty(id.getText().toString())) {
//                    Toast.makeText(LoginActivity.this, "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(pw.getText().toString())) {
//                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                loadingDialog.show(getSupportFragmentManager(), "loading"); // 로딩화면 실행
//                loginWithData.LogintoFirebase(id, pw, new CompleteListener() {
//                    @Override
//                    public void onSuccess() {
//                        loadingDialog.dismiss(); // 로딩화면 종료
//                        if (autoLoginCheckBox.isChecked()) {
//                            saveLoginInfo(id.getText().toString(), pw.getText().toString(), true); // 자동 로그인 저장
//                        } else {
//                            saveLoginInfo(null, null, false); // 자동 로그인 저장 안함
//                        }
//                        startMainActivity();
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        loadingDialog.dismiss(); // 로딩화면 종료
//                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
//                    }
//                });

                //로그인 없이 실험 할때
                startMainActivity();
            }
        });
    }

    private void saveLoginInfo(String userId, String userPw, boolean autoLoginEnabled) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (autoLoginEnabled) {
            editor.putString("userId", userId);
            editor.putString("userPw", userPw);
        } else {
            editor.remove("userId");
            editor.remove("userPw");
        }
        editor.putBoolean("autoLoginEnabled", autoLoginEnabled);
        editor.apply();
    }

    private void startMainActivity() {
        MainMenuChallengeFragment.loginSuccess = true;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish(); // 로그인 액티비티 종료
    }

}