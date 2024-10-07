package com.cookandroid.aerobicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.aerobicapplication.userdata.CompleteListener;
import com.cookandroid.aerobicapplication.userdata.SignupWithData;

public class SignupActivity extends AppCompatActivity {
    EditText name,id,pw,pw2,birthyear,birthmonth,birthdate;
    Button pwcheck, submit;
    boolean pwc = false;                          //패스워드 체크 변수
    public static boolean signupCheck = false;
    private SignupWithData signupWithData;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupWithData = new SignupWithData();

        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        name=findViewById(R.id.signName);
        id=findViewById(R.id.signID);
        pw=findViewById(R.id.signPW);
        pw2=findViewById(R.id.signPW2);
        birthyear=findViewById(R.id.signBirth);
        birthmonth=findViewById(R.id.signBirth2);
        birthdate=findViewById(R.id.signBirth3);

        pwcheck = findViewById(R.id.pwcheckbutton);
        pwcheck.setOnClickListener(v -> {
            if(pw.getText().toString().equals(pw2.getText().toString())){
                pwcheck.setText("일치");
                pwc = true;
            }else{
                Toast.makeText(SignupActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });

        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(v -> {
            //일단 pw 체크만 조건으로, 추후 정보가 정확하게 입력되었는지 체크하는 조건문 필요
            if(pwc){
                //singup
                signupWithData.EditToString(name,id,pw,birthyear,birthmonth,birthdate);
                signupWithData.SignupToFirebase(new CompleteListener(){
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure() {
                        Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(this, "정보를 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
