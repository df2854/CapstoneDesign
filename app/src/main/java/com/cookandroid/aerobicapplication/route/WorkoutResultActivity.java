// WorkoutResultActivity.java
package com.cookandroid.aerobicapplication.route;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.aerobicapplication.MainActivity;
import com.cookandroid.aerobicapplication.Manager.ExercisedataManager;
import com.cookandroid.aerobicapplication.R;

public class WorkoutResultActivity extends AppCompatActivity {

    private TextView workoutDistanceText, workoutTimeText, workoutPaceText, workoutCaltext;
    private Button backToMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_result);

        workoutDistanceText = findViewById(R.id.distance);
        workoutTimeText = findViewById(R.id.time);
        workoutPaceText = findViewById(R.id.pace);
        workoutCaltext = findViewById(R.id.calories);
        backToMainButton = findViewById(R.id.main_menu_button);

        // Intent로 전달된 데이터 받기
        Intent intent = getIntent();
        double totalDistance = intent.getDoubleExtra("totalDistance", 0); // 총 거리
        long elapsedTimeInMillis = intent.getLongExtra("elapsedTime", 0); // 총 시간
        double averageSpeed = intent.getDoubleExtra("averageSpeed", 0); // 평균 속도

        // 시간 포맷팅 (경과 시간 -> 분 단위로 표시)
        long minutes = elapsedTimeInMillis / 60000; // 밀리초 -> 분
        long seconds = (elapsedTimeInMillis % 60000) / 1000; // 나머지 초


        // 결과 화면에 데이터 표시
        workoutDistanceText.setText(String.format("운동 거리 : %.2f km", totalDistance));
        workoutTimeText.setText(String.format("소요 시간 : %02d:%02d", minutes, seconds));
        workoutPaceText.setText(String.format("평균 속도 : %.2f km/h", averageSpeed));


        // 운동 기록 저장
        ExercisedataManager.getInstance().saveWorkoutData();

        // "메인화면으로" 버튼 클릭 시 MainActivity로 돌아가기
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(WorkoutResultActivity.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });



    }
}
