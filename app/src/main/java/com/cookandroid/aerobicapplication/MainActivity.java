package com.cookandroid.aerobicapplication;

import static androidx.fragment.app.FragmentManagerKt.commit;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cookandroid.aerobicapplication.route.RouteMain;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MainMenuHomeFragment fragmentHome = new MainMenuHomeFragment();
    private MainMenuChallengeFragment fragmentChallenge = new MainMenuChallengeFragment();
    private MainMenuRecordFragment fragmentRecord = new MainMenuRecordFragment();
    private MainMenuAccountFragment fragmentAccount = new MainMenuAccountFragment();
    private MainMenuSettingFragment fragmentSetting = new MainMenuSettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 제거
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
            toolbar.setTitle("홈");
        }

        // 네비게이션 바 아이템 클릭 이벤트
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (menuItem.getItemId() == R.id.nav_home) {
                    transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                    toolbar.setTitle("홈");
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_profile) {
                    transaction.replace(R.id.menu_frame_layout, fragmentAccount).commitAllowingStateLoss();
                    toolbar.setTitle("마이페이지");
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_start) {
                    Intent intent = new Intent(getApplicationContext(), RouteMain.class);
                    intent.putExtra("startGps", true); // 인텐트에 GPS 시작 플래그 추가
                    startActivity(intent);
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_challenge) {
                    transaction.replace(R.id.menu_frame_layout, fragmentChallenge).commitAllowingStateLoss();
                    toolbar.setTitle("챌린지");
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_records) {
                    transaction.replace(R.id.menu_frame_layout, fragmentRecord).commitAllowingStateLoss();
                    toolbar.setTitle("기록");
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
