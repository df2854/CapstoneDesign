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

    private TextView workoutDistanceText, workoutTimeText, workoutCaltext;
    private Button backToMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_result);

        workoutDistanceText = findViewById(R.id.workout_distance_text);
        workoutTimeText = findViewById(R.id.workout_time_text);
        workoutCaltext = findViewById(R.id.workout_kcal_text);
        backToMainButton = findViewById(R.id.back_to_main_button);

        // 운동 결과 표시
        double distance = ExercisedataManager.getInstance().getCurrentDistance();
        long minTime = ExercisedataManager.getInstance().getCurrentMinTime();
        double cal = ExercisedataManager.getInstance().getCurrentKcal();

        workoutDistanceText.setText(String.format("운동 거리 : %.2f km", distance));
        workoutTimeText.setText(String.format("소요 시간 : %d 분", minTime));
        workoutCaltext.setText(String.format("소모 칼로리 : %.2f kcal", cal));

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
