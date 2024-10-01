package com.cookandroid.aerobicapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainMenuHomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_home, container, false);

        // '운동 시작' 버튼 클릭 시 StartWorkoutActivity로 이동
        Button startButton = view.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StartWorkoutActivity.class);
                startActivity(intent);
            }
        });

        Button createStartButton = view.findViewById(R.id.createStart);
        createStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePathActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}