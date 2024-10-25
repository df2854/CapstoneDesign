package com.cookandroid.aerobicapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.aerobicapplication.userdata.CompleteListener;
import com.cookandroid.aerobicapplication.userdata.LoginWithData;

public class LoginActivity extends AppCompatActivity {
    EditText id,pw;
    private LoginWithData loginWithData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id=findViewById(R.id.editID);
        pw=findViewById(R.id.editPassword);

        loginWithData = new LoginWithData();

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
//                loginWithData.LogintoFirebase(id, pw, new CompleteListener() {
//                    @Override
//                    public void onSuccess() {
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
//                    }
//                });

               // 로그인 없이 실험 할때
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }

}