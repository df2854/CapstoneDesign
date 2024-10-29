package com.cookandroid.aerobicapplication.Manager;

public class ExercisedataManager {

    // 진행 중인 운동 데이터를 저장하는 변수
    private double currentDistance;
    private long startTime;
    private long endTime;
    private long currentMinTime;
    private double currentKcal;

    // 완료한 운동 데이터를 저장하는 컨테이너


    // 싱글턴 인스턴스
    private static ExercisedataManager instance;

    // private 생성자로 외부에서 인스턴스 생성을 막음
    private ExercisedataManager() {
    }

    // 인스턴스를 얻는 메서드
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
    public void setStartTime(long time){
        this.startTime = time;
    }
    public void setEndTime(long time){
        this.endTime = time;
    }

    // 바깥에서 유저 데이터 가져오는 메서드
    // 사용할때는 ExercisedataManager.getInstance().get------()
    public double getCurrentDistance() {
        return currentDistance;
    }
    public long getCurrentMinTime(){
        calcMinTime();
        return currentMinTime;
    }
    public double getCurrentKcal(){
        calcKcal();
        return currentKcal;
    }


    private void calcMinTime(){
        currentMinTime = (endTime - startTime) / 1000 / 60;
    }

    private void calcKcal(){
        int weight = Integer.parseInt(UserdataManager.getInstance().getUserWeight());
        currentKcal = weight * (int)(currentDistance);
    }

    // 데이터 초기화
    public void clearData() {
        currentDistance = 0;
    }
}
