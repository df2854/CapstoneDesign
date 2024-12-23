package com.cookandroid.aerobicapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.aerobicapplication.userdata.CompleteListener;
import com.cookandroid.aerobicapplication.userdata.SignupWithData;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SignupActivity extends AppCompatActivity {
    EditText name,id,pw,pw2,birthyear,birthmonth,birthdate,height,weight;
    Button pwcheck, submit;
    RadioGroup diseaseRG, experienceRG;
    String[] disease = {"Yes", "No"};
    String[] experience = {"High", "Normal", "Low"};
    int diseaseInt, experienceInt;
    private SignupWithData signupWithData;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupWithData = new SignupWithData();
        LoadingDialog loadingDialog = new LoadingDialog();

        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        name=findViewById(R.id.signName);
        id=findViewById(R.id.signID);
        pw=findViewById(R.id.signPW);
        pw2=findViewById(R.id.signPW2);
        birthyear=findViewById(R.id.signBirth);
        birthmonth=findViewById(R.id.signBirth2);
        birthdate=findViewById(R.id.signBirth3);
        height=findViewById(R.id.signHeight);
        weight=findViewById(R.id.signWeight);

        diseaseRG=(RadioGroup)findViewById(R.id.signDiseaseRG);
        experienceRG=(RadioGroup)findViewById(R.id.signExperienceRG);

        pwcheck = findViewById(R.id.pwcheckbutton);
        pwcheck.setOnClickListener(v -> {
            if(pw.getText().toString().equals(pw2.getText().toString())){
                pwcheck.setText("일치");
                pwc = true;
            }else{
                Toast.makeText(SignupActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });

        diseaseRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.disease) diseaseInt = 0;
                else if(i == R.id.notDisease) diseaseInt = 1;
            }
        });

        experienceRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.high) experienceInt = 0;
                else if(i == R.id.normal) experienceInt = 1;
                else if(i == R.id.low) experienceInt = 2;
            }
        });

        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(v -> {
            loadingDialog.show(getSupportFragmentManager(), "loading"); // 로딩 화면 실행

            //일단 pw 체크만 조건으로, 추후 정보가 정확하게 입력되었는지 체크하는 조건문 필요
            if(pwc){
                //회원 가입 class 실행
                signupWithData.PushToString(name,id,pw,birthyear,birthmonth,birthdate,
                        height,weight,disease[diseaseInt],experience[experienceInt]);
                signupWithData.SignupToFirebase(new CompleteListener(){
                    @Override
                    public void onSuccess() {
                        loadingDialog.dismiss(); // 로딩화면 종료
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure() {
                        loadingDialog.dismiss(); // 로딩화면 종료
                        Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(this, "정보를 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    boolean pwc = false;                          //패스워드 체크 변수
    public static boolean signupCheck = false;
}
