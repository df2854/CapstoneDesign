package com.cookandroid.aerobicapplication.Manager;

import java.util.ArrayList;
import java.util.List;

public class ExercisedataManager {

    // 진행 중인 운동 데이터를 저장하는 변수
    private double currentDistance;
    private long currentMinTime;
    private long currentSecTime;
    private double currentKcal;

    // 완료한 운동 데이터는 앞으로 SharedPreferences로 내부저장소에 저장합니다. Key는 월일distance/min/kcal

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

    public void setCurrentMinTime(long time) {
        this.currentMinTime = time;
    }
    public void setCurrentSecTime(long time) {this.currentSecTime = time;}


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
        currentKcal = weight * 1.2 * (currentMinTime / 15);
    }

    // 바깥에서 운동 데이터 가져오는 메서드
    public double getCurrentDistance() {
        return currentDistance;
    }
    public long getCurrentMinTime() {return currentMinTime;}

    public double getCurrentKcal() {
        calcKcal();
        return currentKcal;
    }

    // 데이터 초기화
    public void clearData() {
        currentDistance = 0;
        currentSecTime = 0;
        currentMinTime = 0;
        currentKcal = 0;
    }
}
