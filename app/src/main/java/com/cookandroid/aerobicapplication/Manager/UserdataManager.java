package com.cookandroid.aerobicapplication.Manager;

public class UserdataManager {
    // 유저 데이터를 저장하는 변수
    private String userId;
    private String userName;
    private String userBirthday;

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
    }

    // 유저 데이터 가져오는 메서드
    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    // 로그아웃 시 데이터 초기화
    public void clearData() {
        userId = null;
        userName = null;
        userBirthday = null;
    }
}
