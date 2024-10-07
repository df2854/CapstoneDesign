package com.cookandroid.aerobicapplication;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.aerobicapplication.databinding.ActivityLoginBinding;
import com.cookandroid.aerobicapplication.userdata.CompleteListener;
import com.cookandroid.aerobicapplication.userdata.LoginWithData;

public class LoginActivity extends AppCompatActivity {
    EditText id,pw;
    public static boolean signCheck = false;
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
                loginWithData.LogintoFirebase(id, pw, new CompleteListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
                /*
                로그인 없이 실험 할때
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                 */
            }
        });
    }

}