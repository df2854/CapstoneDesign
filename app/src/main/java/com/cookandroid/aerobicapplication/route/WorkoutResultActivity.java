// WorkoutResultActivity.java
package com.cookandroid.aerobicapplication.route;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.aerobicapplication.MainActivity;
import com.cookandroid.aerobicapplication.Manager.ExercisedataManager;
import com.cookandroid.aerobicapplication.R;

import java.util.Calendar;

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
        double estimatedCalories = intent.getDoubleExtra("estimatedCalories", 0); // 예상 칼로리

        // 시간 포맷팅 (경과 시간 -> 분 단위로 표시)
        long minutes = elapsedTimeInMillis / 60000; // 밀리초 -> 분
        long seconds = (elapsedTimeInMillis % 60000) / 1000; // 나머지 초

        // 결과 화면에 데이터 표시
        workoutDistanceText.setText(String.format("%.3f", totalDistance));
        workoutTimeText.setText(String.format("%02d:%02d", minutes, seconds));
        workoutPaceText.setText(String.format("%.1f", averageSpeed));
        workoutCaltext.setText(String.format("%.2f", estimatedCalories)); // 칼로리 표시

        // 운동 기록 저장 및 초기화
        Calendar calendar = Calendar.getInstance(); // 현재 날짜와 시간 가져오기
        int month = calendar.get(Calendar.MONTH) + 1; // 월 가져오기 (0부터 시작하므로 +1)
        int day = calendar.get(Calendar.DAY_OF_MONTH); // 일 가져오기
        String key = String.valueOf(month) + String.valueOf(day);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key+"distance", Double.toString(totalDistance));
        editor.putString(key+"min", Long.toString(minutes));
        editor.putString(key+"kcal", Double.toString(estimatedCalories));

        float totalDis = sharedPreferences.getFloat("total", 0);
        editor.putFloat("total", totalDis+(float)totalDistance);
        editor.putBoolean("first",true);

        editor.apply();

        ExercisedataManager.getInstance().clearData();

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
