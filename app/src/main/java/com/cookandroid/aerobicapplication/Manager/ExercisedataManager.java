package com.cookandroid.aerobicapplication.Manager;

import java.util.ArrayList;
import java.util.List;

public class ExercisedataManager {

    // 진행 중인 운동 데이터를 저장하는 변수
    private double currentDistance;
    private long startTime;
    private long endTime;
    private long currentMinTime;
    private double currentKcal;

    // 완료한 운동 데이터를 저장하는 리스트
    private List<WorkoutData> workoutHistory = new ArrayList<>();

    // 싱글턴 인스턴스
    private static ExercisedataManager instance;

    private ExercisedataManager() {}

    public static synchronized ExercisedataManager getInstance() {
        if (instance == null) {
            instance = new ExercisedataManager();
        }
        return instance;
    }

    // 운동 데이터 설정 메서드
    public void setCurrentDistance(double distance) {
        this.currentDistance = distance;
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

    public void setEndTime(long time) {
        this.endTime = time;
    }

    // 소요 시간 계산
    private void calcMinTime() {
        currentMinTime = (endTime - startTime) / 1000 / 60;
    }

    public void calcKcal() {
        String userWeightString = UserdataManager.getInstance().getUserWeight();
        int weight = 0;

        if (userWeightString != null && !userWeightString.isEmpty()) {
            try {
                weight = Integer.parseInt(userWeightString);
            } catch (NumberFormatException e) {
                weight = 0;
            }
        }

        // weight와 currentDistance 값을 사용하여 칼로리 계산
        currentKcal = weight * currentDistance;
    }

    // 운동 데이터 저장 메서드
    public void saveWorkoutData() {
        calcMinTime();
        calcKcal();
        WorkoutData workout = new WorkoutData(currentDistance, currentMinTime, currentKcal);
        workoutHistory.add(workout);
        clearData(); // 새 운동 기록을 위해 데이터 초기화
    }

    // 완료된 운동 기록 가져오기
    public List<WorkoutData> getWorkoutHistory() {
        return workoutHistory;
    }

    // 바깥에서 운동 데이터 가져오는 메서드
    public double getCurrentDistance() {
        return currentDistance;
    }

    public long getCurrentMinTime() {
        calcMinTime();
        return currentMinTime;
    }

    public double getCurrentKcal() {
        calcKcal();
        return currentKcal;
    }

    // 데이터 초기화
    public void clearData() {
        currentDistance = 0;
        startTime = 0;
        endTime = 0;
        currentMinTime = 0;
        currentKcal = 0;
    }

    // 운동 데이터 클래스
    public static class WorkoutData {
        private double distance;
        private long minTime;
        private double kcal;

        public WorkoutData(double distance, long minTime, double kcal) {
            this.distance = distance;
            this.minTime = minTime;
            this.kcal = kcal;
        }

        public double getDistance() {
            return distance;
        }

        public long getMinTime() {
            return minTime;
        }

        public double getKcal() {
            return kcal;
        }
    }
}
