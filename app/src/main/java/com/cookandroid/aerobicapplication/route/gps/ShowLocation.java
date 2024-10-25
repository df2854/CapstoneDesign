// LocationHelper.java
package com.cookandroid.aerobicapplication.route.gps;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class ShowLocation {
    private Context context;
    private TMapView tMapView;

    public ShowLocation(Context context, TMapView tMapView) {
        this.context = context;
        this.tMapView = tMapView;
    }

    public void showCurrentLocation(ImageView currentLocationImage) {
        TMapPoint currentPoint = tMapView.getLocationPoint(); // 현재 위치 좌표 가져오기
        if (currentPoint != null) {
            // 지도 중앙을 현재 위치로 이동
            tMapView.setCenterPoint(currentPoint.getLongitude(), currentPoint.getLatitude());

            // 현재 위치 이미지를 보여줌
            currentLocationImage.setVisibility(View.VISIBLE);  // 이미지 보이기

            // 현재 위치 이미지의 위치를 설정 (지도 중앙)
            currentLocationImage.setX((tMapView.getWidth() - currentLocationImage.getWidth()) / 2);
            currentLocationImage.setY((tMapView.getHeight() - currentLocationImage.getHeight()) / 2);

            // 이미지를 맨 앞으로 가져옴
            currentLocationImage.bringToFront();

            // 일정 시간 후 이미지 숨기기
            new Handler().postDelayed(() -> {
                currentLocationImage.setVisibility(View.GONE);
            }, 1000);  // 1000ms = 1초 후에 사라짐
        } else {
            Toast.makeText(context, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
