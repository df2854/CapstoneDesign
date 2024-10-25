package com.cookandroid.aerobicapplication.route;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.aerobicapplication.MainActivity;
import com.cookandroid.aerobicapplication.R;


public class WorkoutResultActivity extends AppCompatActivity {

    private TextView resultText, workoutTimeText;
    private Button backToMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cookandroid.aerobicapplication.R.layout.activity_workout_result);

        resultText = findViewById(R.id.result_text);
        workoutTimeText = findViewById(R.id.workout_time_text);
        backToMainButton = findViewById(R.id.back_to_main_button);

        // Intent로부터 운동 시간 데이터를 받음
        Intent intent = getIntent();
        int mins = intent.getIntExtra("mins", 0);
        int secs = intent.getIntExtra("secs", 0);
        int milliseconds = intent.getIntExtra("milliseconds", 0);

        // 운동 시간을 표시할 텍스트뷰에 설정
        workoutTimeText.setText(String.format("운동 시간: %02d:%02d:%02d", mins, secs, milliseconds / 10));

        // "처음으로" 버튼 클릭 시 MainActivity로 돌아가기
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(WorkoutResultActivity.this, MainActivity.class);
                // MainActivity로 돌아가면서 기존 액티비티들을 제거
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
