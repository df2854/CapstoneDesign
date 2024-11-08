// WorkoutLogActivity.java
package com.cookandroid.aerobicapplication;

import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import android.graphics.Color;

import com.cookandroid.aerobicapplication.Manager.ExercisedataManager;
import com.cookandroid.aerobicapplication.Manager.ExercisedataManager.WorkoutData;

import java.util.List;

public class WorkoutLogActivity extends AppCompatActivity {

    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);
        setTitle("운동 기록");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        CalendarDay currentDate = CalendarDay.today();
        calendarView.setSelectedDate(currentDate);

        // Persistent Bottom Sheet 설정
        View bottomSheet = findViewById(R.id.persistent_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // 날짜 선택 시 리스너 설정
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            // 운동 데이터 불러와서 Bottom Sheet에 표시
            TextView selectedDateText = findViewById(R.id.selected_date_text);
            selectedDateText.setText("선택한 날짜: " + date.getMonth() + "월 " + date.getDay() + "일");

            TextView workoutHistoryText = findViewById(R.id.workout_history_text);
            StringBuilder historyBuilder = new StringBuilder();

            List<WorkoutData> workoutHistory = ExercisedataManager.getInstance().getWorkoutHistory();
            for (WorkoutData workout : workoutHistory) {
                historyBuilder.append(String.format("거리: %.2f km, 시간: %d 분, 칼로리: %.2f kcal\n",
                        workout.getDistance(), workout.getMinTime(), workout.getKcal()));
            }

            workoutHistoryText.setText(historyBuilder.toString());
            bottomSheet.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
