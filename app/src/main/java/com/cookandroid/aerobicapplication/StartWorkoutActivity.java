package com.cookandroid.aerobicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StartWorkoutActivity extends AppCompatActivity {

    private TextView stopwatchDisplay;
    private Button pauseResumeButton, endButton;
    private Handler handler = new Handler();

    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isPaused = false;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updateTime % 1000);

            stopwatchDisplay.setText(String.format("%02d:%02d:%02d", mins, secs, milliseconds / 10));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        stopwatchDisplay = findViewById(R.id.stopwatch_display);
        pauseResumeButton = findViewById(R.id.pause_resume_button);
        endButton = findViewById(R.id.end_button);

        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimerThread, 0);

        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPaused) {
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);
                    pauseResumeButton.setText("운동재개");
                    isPaused = true;
                } else {
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimerThread, 0);
                    pauseResumeButton.setText("일시정지");
                    isPaused = false;
                }
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(updateTimerThread);

                // 운동 시간 데이터 계산
                int secs = (int) (updateTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                int milliseconds = (int) (updateTime % 1000);

                // 운동 시간 데이터를 Intent에 추가
                Intent intent = new Intent(StartWorkoutActivity.this, WorkoutResultActivity.class);
                intent.putExtra("mins", mins);
                intent.putExtra("secs", secs);
                intent.putExtra("milliseconds", milliseconds);
                startActivity(intent);
                finish();
            }
        });
    }
}