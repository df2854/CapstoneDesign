package com.example.pacemaker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MainMenuHomeFragment fragmentHome = new MainMenuHomeFragment();
    private MainMenuAccountFragment fragmentAccount = new MainMenuAccountFragment();
    private MainMenuSettingFragment fragmentSetting = new MainMenuSettingFragment();

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 처음 실행 시 기본으로 HomeFragment를 표시
        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
            toolbar.setTitle("홈");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (menuItem.getItemId() == R.id.fragment_home) {
                    transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                    toolbar.setTitle("홈"); // Home Fragment일 때 툴바 제목 설정
                    return true;
                } else if (menuItem.getItemId() == R.id.fragment_account) {
                    transaction.replace(R.id.menu_frame_layout, fragmentAccount).commitAllowingStateLoss();
                    toolbar.setTitle("계정"); // Account Fragment일 때 툴바 제목 설정
                    return true;
                } else if (menuItem.getItemId() == R.id.fragment_settings) {
                    transaction.replace(R.id.menu_frame_layout, fragmentSetting).commitAllowingStateLoss();
                    toolbar.setTitle("설정"); // Settings Fragment일 때 툴바 제목 설정
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
