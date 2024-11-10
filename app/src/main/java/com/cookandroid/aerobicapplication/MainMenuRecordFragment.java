// MainMenuRecordFragment.java
package com.cookandroid.aerobicapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.cookandroid.aerobicapplication.Manager.ExercisedataManager;
import com.cookandroid.aerobicapplication.Manager.ExercisedataManager.WorkoutData;
import java.util.List;

public class MainMenuRecordFragment extends Fragment {

    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 프래그먼트 레이아웃을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_main_menu_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("운동 기록");

        MaterialCalendarView calendarView = view.findViewById(R.id.calendarView);
        CalendarDay currentDate = CalendarDay.today();
        calendarView.setSelectedDate(currentDate);

        // Persistent Bottom Sheet 설정
        View bottomSheet = view.findViewById(R.id.persistent_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // 날짜 선택 시 리스너 설정
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            // 운동 데이터 불러와서 Bottom Sheet에 표시
            TextView selectedDateText = view.findViewById(R.id.selected_date_text);
            selectedDateText.setText("선택한 날짜: " + date.getMonth() + "월 " + date.getDay() + "일");

            TextView workoutHistoryText = view.findViewById(R.id.workout_history_text);
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
}