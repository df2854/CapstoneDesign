package com.cookandroid.aerobicapplication.Manager;

import java.util.Calendar;

public class UserdataManager {
    // 유저 데이터를 저장하는 변수
    private String userId;
    private String userName;
    private String userBirthday;
    private String userHeight;
    private String userWeight;
    private String userDisease;             // 0 or 1
    private String userExperience;          // High or Normal or Low

    private double userBmi;
    private int userAge;
    private int userScore = 100;


    // 싱글턴 인스턴스
    private static UserdataManager instance;

    // private 생성자로 외부에서 인스턴스 생성을 막음
    private UserdataManager() {
    }

    // 인스턴스를 얻는 메서드
    public static synchronized UserdataManager getInstance() {
        if (instance == null) {
            instance = new UserdataManager();
        }
        return instance;
    }

    // 유저 데이터 설정 메서드
    public void setUserData(String id, String name, String birthday) {
        this.userId = id;
        this.userName = name;
        this.userBirthday = birthday;
        /*
        * this.userHeight = height;
        * this.userWeight = weight;
        * this.userDisease = disease;
        * this.userExperience = experience;
        *
        * setUserScore();
        * */
    }

    // 바깥에서 유저 데이터 가져오는 메서드
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserBirthday() {
        return userBirthday;
    }
    public String getUserHeight() {
        return userHeight;
    }
    public String getUserWeight() {
        return userWeight;
    }
    public String getUserDisease() {
        return userDisease;
    }
    public String getUserExperience() {
        return userExperience;
    }
    public Double getUserBmi() {
        return userBmi;
    }
    public int getUserAge(){
        return userAge;
    }
    public int getUserScore(){
        return userScore;
    }



    // 로그아웃 시 데이터 초기화
    public void clearData() {
        userId = null;
        userName = null;
        userBirthday = null;
        userHeight = null;
        userWeight = null;
        userDisease = null;
        userExperience = null;
        userBmi = 0;
        userAge = 0;
        userScore = 0;
    }

    // 유저 점수 계산
    private void setUserScore(){
        calcAge();
        calcBMI();

        if(userAge >= 65)
            userScore -= 10;

        if(userBmi >= 30)
            userScore -= 10;

        if(userDisease.equals("O"))
            userScore -= 10;

        if(userExperience.equals("High"))
            userScore += 10;
        else if (userExperience.equals("Low"))
            userScore -= 10;
    }

    // 나이 계산
    private void calcAge(){
        String[] parts = userBirthday.split("-");
        int birthYear = Integer.parseInt(parts[0]);
        int birthMonth = Integer.parseInt(parts[1]);
        int birthDay = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar의 월은 0부터 시작하므로 1을 더함
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        userAge = currentYear - birthYear;

        // 생일이 아직 안 지났으면 나이에서 1을 뺌
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            userAge--;
        }
    }

    // Bmi 게산
    private void calcBMI(){
        double weight = Double.parseDouble(userWeight);
        double height = Double.parseDouble(userHeight)/100;

        userBmi = weight / (height * height);
    }
}
