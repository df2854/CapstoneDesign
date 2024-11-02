package com.cookandroid.aerobicapplication.route;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest; // Android의 Manifest 클래스
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.cookandroid.aerobicapplication.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class RouteMain extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private TMapView tMapView;
    private ArrayList<TMapPoint> markerPoints = new ArrayList<>();  // 마커 위치 저장
    private boolean isStartPointSet = false;  // 출발지 설정 여부
    private boolean isMarkerAddEnabled = false;
    private TMapMarkerItem currentMarker;  // 현재 마커 객체 저장
    ArrayList<TMapMarkerItem> makerList = new ArrayList<>();
    private TMapGpsManager tMapGpsManager;
    int currentImage = R.drawable.startmarker;
    // 레이아웃에 포함된 텍스트 뷰 초기화
    private TextView estimatedDistanceTextView;
    private TextView estimatedTimeTextView;
    private TextView estimatedCalorieTextView;
    private LinearLayout infoLayout;
    private LinearLayout buttonLayout;

    // 클래스 필드로 선언
    private double totalDistance = 0; // 총 거리
    private double totalDistanceInKm = 0; // 총 거리 (km)
    private int estimatedTimeInMinutes = 0; // 예상 시간 (분)
    private int estiimatedCalrorie = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cookandroid.aerobicapplication.R.layout.activity_start_workout);

        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("iiXmtmSmi66hsWmupLjKUKVteYjWcvI1nrms8BZa");
        linearLayoutTmap.addView(tMapView);
        // 뷰 초기화
        estimatedDistanceTextView = findViewById(R.id.estimatedDistanceTextView);
        estimatedTimeTextView = findViewById(R.id.estimatedTimeTextView);
        estimatedCalorieTextView = findViewById(R.id.estimatedCalorieTextView);
        infoLayout = findViewById(R.id.infoLayout);
        buttonLayout = findViewById(R.id.buttonLayout);
        // 줌 인/아웃 버튼 추가
        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        Button btnSetStartPoint = findViewById(R.id.btnSetStart); // 출발지 설정 버튼
        Button btnEndSet = findViewById(R.id.btnEndSet);
        Button btnReset = findViewById(R.id.btnReset);
        // 확대 버튼 클릭 리스너
        btnZoomIn.setOnClickListener(v -> tMapView.MapZoomIn()); // 지도 확대

        // 축소 버튼 클릭 리스너
        btnZoomOut.setOnClickListener(v -> tMapView.MapZoomOut()); // 지도 축소

        // 위치 권한 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            initializeGps();
        }

        // 현재 위치 버튼 클릭 시 실행
        ImageButton btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(v -> {
            if (tMapGpsManager != null) {
                tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
                tMapGpsManager.OpenGps();
                Toast.makeText(this, "현재 위치를 가져옵니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 출발지 설정 버튼 클릭 리스너 추가
        btnSetStartPoint.setOnClickListener(v -> {
            if (markerPoints.isEmpty()) {
                Toast.makeText(this, "마커를 먼저 추가해 주세요.", Toast.LENGTH_SHORT).show();
            }else if (!isStartPointSet) {
                isStartPointSet = true;  // 출발지 설정이 완료되었으므로 여러 개의 마커를 찍을 수 있도록 변경
                currentImage = R.drawable.middlemarker;
                btnEndSet.setVisibility(View.VISIBLE);  // "지정 완료" 버튼 보이기
                btnSetStartPoint.setVisibility(View.GONE);  // "출발지 설정" 버튼 숨기기
                btnReset.setVisibility(View.VISIBLE);  // "다시 설정" 버튼 보이기
            }
        });

        // 경로 지정 완료 버튼 클릭 리스너 추가
        btnEndSet.setOnClickListener(v -> {
            if (markerPoints.size() < 2) {
                Toast.makeText(this, "경로를 계산하기 위해 최소 2개의 마커가 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {

                if (!makerList.isEmpty()) {
                    TMapMarkerItem lastMarker = makerList.get(makerList.size() - 1);
                    updateMarkerIcon(lastMarker,R.drawable.finishmarker);
                    tMapView.invalidate();  // 마커 이미지 변경 후 강제 갱신
                }
                // 총 거리 초기화
                totalDistance = 0;

                // 모든 마커 간의 거리 계산
                for (int i = 0; i < markerPoints.size() - 1; i++) {
                    TMapPoint startPoint = markerPoints.get(i);
                    TMapPoint endPoint = markerPoints.get(i + 1);
                    totalDistance += calculateDistance(startPoint, endPoint);
                    calculateRoute(startPoint, endPoint);
                }

                // 총 거리 킬로미터로 변환 및 소수점 한 자리 수로 표현
                totalDistanceInKm = totalDistance / 1000.0;
                totalDistanceInKm = Math.round(totalDistanceInKm * 10) / 10.0;

                // 예상 시간 계산
                double speed = 4;
                double estimatedTimeInHours = totalDistanceInKm / speed;
                estimatedTimeInMinutes = (int) (estimatedTimeInHours * 60);

                // 예상 칼로리 계싼
                estiimatedCalrorie = (int) totalDistanceInKm * 40;

                // 결과 표시
                runOnUiThread(() -> {
                    estimatedDistanceTextView.setText(String.format("%.1fkm", totalDistanceInKm));
                    estimatedTimeTextView.setText(String.format("%d분", estimatedTimeInMinutes));
                    estimatedCalorieTextView.setText(String.format("%dkcal", estiimatedCalrorie));
                    infoLayout.setVisibility(View.VISIBLE);
                    buttonLayout.setVisibility(View.GONE);
                });
            }
        });

        // 마커 추가 버튼 참조 및 클릭 리스너 설정
        ImageButton toggleMarkerButton = findViewById(R.id.toggleMarkerButton);
        toggleMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMarkerAddEnabled) {
                    isMarkerAddEnabled = true;
                    Toast.makeText(RouteMain.this, "마커를 추가할 위치를 탭하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    isMarkerAddEnabled = false;
                }
            }
        });


        // "다시 설정" 버튼 클릭 리스너
        btnReset.setOnClickListener(v -> {
            // 모든 마커, 경로 폴리라인 제거
            tMapView.removeAllMarkerItem();
            tMapView.removeAllTMapPolyLine();
            currentImage = R.drawable.startmarker;
            // 거리, 시간 초기화
            markerPoints.clear();
            totalDistance = 0;
            totalDistanceInKm = 0;
            estimatedTimeInMinutes = 0;
            isStartPointSet = false;

            // 레이아웃 및 버튼 상태 초기화
            estimatedDistanceTextView.setText("");
            estimatedTimeTextView.setText("");
            infoLayout.setVisibility(View.GONE);  // 거리 및 시간 정보 숨기기
            btnSetStartPoint.setVisibility(View.VISIBLE); // 출발지 설정 버튼 다시 보이기
            btnEndSet.setVisibility(View.GONE);
            btnReset.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            Toast.makeText(this, "경로가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // TMapView에 클릭 리스너 설정
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<com.skt.Tmap.poi_item.TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                if(isStartPointSet && isMarkerAddEnabled){
                    addMarker(point);
                }else if(isMarkerAddEnabled){
                    removeCurrentMarker(); // 기존 마커 제거
                    addMarker(point); // 새로운 마커 추가
                }
                return true; // 이벤트 처리 완료

            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<com.skt.Tmap.poi_item.TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                // 추가적인 처리 필요 시 여기 구현
                return true; // 이벤트 처리 완료
            }
        });
    }
    // 기존 마커를 제거하는 메서드
    private void removeCurrentMarker() {
        if (currentMarker != null) {
            // 기존 마커를 지도에서 제거
            tMapView.removeMarkerItem(currentMarker.getID());
            currentMarker = null; // 마커 정보 초기화
        }
    }

    private void updateMarkerIcon(TMapMarkerItem marker, int resourceId) {
        // 새 이미지 로드 및 크기 조정
        Bitmap icon = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, 165, 190, false); // 원하는 크기로 조정

        // 마커 아이콘 설정
        marker.setIcon(resizedIcon);

    }


    // 클릭한 위치에 마커를 추가하는 메서드
    private void addMarker(TMapPoint point) {
       if(isMarkerAddEnabled) { // 기존 마커 삭제
           if (isStartPointSet == false) {
               removeCurrentMarker();
           }

           TMapMarkerItem markerItem = new TMapMarkerItem();

           // 마커의 위치 설정
           markerItem.setTMapPoint(point);
           markerItem.setName("클릭한 위치");

           // 마커의 핀 설정 (중앙에 위치하도록)
           markerItem.setPosition(0.5f, 1.0f);

           updateMarkerIcon(markerItem, currentImage);
           // 마커 ID 설정
           String markerId = "marker_" + point.toString();
           markerItem.setID(markerId);

           // 새로운 마커를 currentMarker로 설정
           if (isStartPointSet == false) {
               currentMarker = markerItem;
           }


           // 기존 마커와의 거리 계산
           for (TMapPoint existingPoint : markerPoints) {
               if (calculateDistance(point, existingPoint) < 30 && isStartPointSet == true) { // 30m 이하인 경우
                   highlightMarker(existingPoint); // 기존 마커 강조
                   markerPoints.add(point);
                   return; // 추가할 수 없음
               }
           }

           // 지도에 마커 추가
           tMapView.addMarkerItem(markerId, markerItem);
           makerList.add(markerItem); // 모든 마커를 리스트에 추가

           // 마커 추가 메시지 표시
//        Toast.makeText(this, "마커가 추가되었습니다!", Toast.LENGTH_SHORT).show();
           // 마커의 위치를 리스트에 저장
           markerPoints.add(point);
           // 출발지가 설정된 경우에만 경로 계산
           if (isStartPointSet && markerPoints.size() >= 2) {
               TMapPoint lastPoint = markerPoints.get(markerPoints.size() - 2); // 마지막에서 두 번째 마커
               calculateRoute(lastPoint, point); // 마지막 두 점 간의 경로 계산
           }
       }
    }

    // 두 TMapPoint 간의 거리를 계산하는 메서드
    private float calculateDistance(TMapPoint point1, TMapPoint point2) {
        // 위도와 경도를 사용하여 거리 계산
        double lat1 = point1.getLatitude();
        double lon1 = point1.getLongitude();
        double lat2 = point2.getLatitude();
        double lon2 = point2.getLongitude();

        // Haversine 공식을 사용한 거리 계산
        final int R = 6371; // 지구 반지름 (킬로미터 단위)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (R * c * 1000); // 결과를 미터 단위로 변환
    }

    // 기존 마커를 강조하는 메서드
    private void highlightMarker(TMapPoint existingPoint) {
        if (!isStartPointSet) {
            return; // 출발지가 설정되지 않았다면 메서드 종료
        }
        String markerId = "marker_" + existingPoint.toString();
        TMapMarkerItem markerItem = tMapView.getMarkerItemFromID(markerId);

        // 마커가 존재하는 경우
        if (markerItem != null) {
            // 마커 이미지 로드 및 크기 조정
            Bitmap icon = BitmapFactory.decodeResource(getResources(), currentImage);  // 강조된 마커 이미지 로드
            Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, 180, 205, false);  // 크기 조정 (예: 120x120)

            // 마커의 아이콘을 강조된 아이콘으로 설정
            markerItem.setIcon(resizedIcon);

            // 지도에 마커 업데이트 (기존 마커 아이템을 삭제하고 새로 추가)
            tMapView.removeMarkerItem(markerId);
            tMapView.addMarkerItem(markerId, markerItem);

            // 경로를 계산할 때 사용할 마커 위치
            TMapPoint startPoint = markerPoints.get(markerPoints.size() - 1); // 마지막으로 추가된 마커
            calculateRoute(existingPoint, startPoint); // 경로 계산

            // 일정 시간 후 원래 크기로 복구 (여기서는 1초 후)
            new Handler().postDelayed(() -> {
                Bitmap originalIcon = BitmapFactory.decodeResource(getResources(), currentImage);
                Bitmap originalResizedIcon = Bitmap.createScaledBitmap(originalIcon, 165, 190, false);
                markerItem.setIcon(originalResizedIcon);
                tMapView.removeMarkerItem(markerId);
                tMapView.addMarkerItem(markerId, markerItem);
            }, 600); // 1000ms = 1초
        }
    }

    private void calculateRoute(TMapPoint startPoint, TMapPoint endPoint) {
        TMapData tMapData = new TMapData();
        // 경로 타입을 CAR_PATH에서 PEDESTRIAN_PATH로 변경
        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.rgb(67, 155, 255)); // 경로 라인 색상 설정
                polyLine.setOutLineColor(Color.WHITE);
                polyLine.setLineWidth(30); // 경로 라인의 두께 설정
                polyLine.setOutLineWidth(40);

                // 지도에 경로 추가
                tMapView.addTMapPolyLine("line_" + startPoint.toString() + "_" + endPoint.toString(), polyLine);
            }
        });

    }

   ////////////////////////Gps///////////////////////////

    private void initializeGps() {
        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setMinTime(1000);
        tMapGpsManager.setMinDistance(5);
        tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        tMapGpsManager.setLocationCallback(); // 위치 변경 콜백 설정
    }

    @Override
    public void onLocationChange(@NonNull Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        TMapPoint currentLocation = new TMapPoint(lat, lon);

        // 지도 중심을 현재 위치로 이동
        tMapView.setCenterPoint(lon, lat);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setTMapPoint(currentLocation);
        markerItem.setName("현재 위치");

        // drawable에서 아이콘 설정
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.realcircle);
        // 비트맵 크기 조정
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        markerItem.setIcon(resizedBitmap); // 조정된 크기의 비트맵 설정

        // 마커 추가
        tMapView.addMarkerItem("currentLocationMarker", markerItem);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeGps();
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
