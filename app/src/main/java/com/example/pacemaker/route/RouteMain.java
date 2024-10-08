package com.example.pacemaker.route;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pacemaker.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class RouteMain extends AppCompatActivity {
    private TMapView tMapView;
    private ArrayList<TMapPoint> markerPoints = new ArrayList<>();  // 마커 위치 저장
    private boolean isStartPointSet = false;  // 출발지 설정 여부
    private TMapMarkerItem currentMarker;  // 현재 마커 객체 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("iiXmtmSmi66hsWmupLjKUKVteYjWcvI1nrms8BZa");
        linearLayoutTmap.addView(tMapView);

        // 줌 인/아웃 버튼 추가
        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        ImageButton btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        Button btnSetStartPoint = findViewById(R.id.btnSetStart); // 출발지 설정 버튼
        Button btnEndSet = findViewById(R.id.btnEndSet);
        // 확대 버튼 클릭 리스너
        btnZoomIn.setOnClickListener(v -> tMapView.MapZoomIn()); // 지도 확대

        // 축소 버튼 클릭 리스너
        btnZoomOut.setOnClickListener(v -> tMapView.MapZoomOut()); // 지도 축소


        // 현재 위치 GPS 버튼 클릭 리스너 추가
        btnCurrentLocation.setOnClickListener(v -> showCurrentLocation());
        // 출발지 설정 버튼 클릭 리스너 추가
        btnSetStartPoint.setOnClickListener(v -> {
            if (!isStartPointSet) {
                isStartPointSet = true;  // 출발지 설정이 완료되었으므로 여러 개의 마커를 찍을 수 있도록 변경
//                btnSetStartPoint.setText("경로 지정"); // 버튼 텍스트 변경
//                Toast.makeText(this, "출발지가 설정되었습니다. 이제 여러 마커를 찍을 수 있습니다.", Toast.LENGTH_SHORT).show();
                btnEndSet.setVisibility(View.VISIBLE);  // 이미지 보이기

            } else {
//                Toast.makeText(this, "이미 출발지가 설정되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 경로 지정 완료 버튼 클릭 리스너 추가
        btnEndSet.setOnClickListener(v -> {
            if (markerPoints.size() < 2) {
//                Toast.makeText(this, "경로를 계산하기 위해 최소 2개의 마커가 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {
                TMapPoint startPoint = markerPoints.get(0); // 첫 번째 마커
                TMapPoint endPoint = markerPoints.get(markerPoints.size() - 1); // 마지막 마커
                calculateEstimatedTime(startPoint, endPoint); // 예상 시간 계산
            }
        });

        // TMapView에 클릭 리스너 설정
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<com.skt.Tmap.poi_item.TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                if(isStartPointSet){
                    addMarker(point);
                }else{
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
    // 클릭한 위치에 마커를 추가하는 메서드
    private void addMarker(TMapPoint point) {
        // 기존 마커 삭제
        if(isStartPointSet == false){
            removeCurrentMarker();
        }

        TMapMarkerItem markerItem = new TMapMarkerItem();

        // 마커의 위치 설정
        markerItem.setTMapPoint(point);
        markerItem.setName("클릭한 위치");

        // 마커의 핀 설정 (중앙에 위치하도록)
        markerItem.setPosition(0.5f, 1.0f);

        // 마커 이미지 로드 및 크기 조정
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);  // 마커 이미지 로드
        Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, 125, 125, false);  // 가로 세로 100x100으로 크기 조정
        markerItem.setIcon(resizedIcon);

        // 마커 ID 설정
        String markerId = "marker_" + point.toString();
        markerItem.setID(markerId);

        // 새로운 마커를 currentMarker로 설정
        if(isStartPointSet == false){
            currentMarker = markerItem;
        }

        // 기존 마커와의 거리 계산
        for (TMapPoint existingPoint : markerPoints) {
            if (calculateDistance(point, existingPoint) < 30) { // 30m 이하인 경우
                highlightMarker(existingPoint); // 기존 마커 강조
                markerPoints.add(point);
                return; // 추가할 수 없음
            }
        }

        // 지도에 마커 추가
        tMapView.addMarkerItem(markerId, markerItem);


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
    private void calculateEstimatedTime(TMapPoint startPoint, TMapPoint endPoint) {
        TMapData tMapData = new TMapData();
        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {

            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                // 경로 정보에서 소요 시간 가져오기
                double estimatedTime = polyLine.getDistance(); // 경로 길이 가져오기
                double speed = 1.5; // 평균 보행 속도 (km/h)
                double timeInHours = estimatedTime / 1000.0 / speed; // km로 변환 후 속도로 나누기
                int minutes = (int) (timeInHours * 60); // 분으로 변환

              // 예상 소요 시간 표시
                runOnUiThread(() -> {
                    // TextView를 사용하여 UI에 표시
                    TextView estimatedTimeTextView = findViewById(R.id.estimatedTimeTextView);
                    Button btnSetStartPoint = findViewById(R.id.btnSetStart); // 출발지 설정 버튼
                    Button btnEndSet = findViewById(R.id.btnEndSet);
                    btnEndSet.setVisibility(View.GONE);
                    btnSetStartPoint.setVisibility(View.GONE);
                    estimatedTimeTextView.setVisibility(View.VISIBLE);
                    estimatedTimeTextView.setText("예상 소요 시간: " + minutes + "분");
                }); }
        });
    }
    // 기존 마커를 강조하는 메서드
    private void highlightMarker(TMapPoint existingPoint) {
        String markerId = "marker_" + existingPoint.toString();
        TMapMarkerItem markerItem = tMapView.getMarkerItemFromID(markerId);

        // 마커가 존재하는 경우
        if (markerItem != null) {
            // 마커 이미지 로드 및 크기 조정
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);  // 강조된 마커 이미지 로드
            Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, 150, 150, false);  // 크기 조정 (예: 120x120)

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
                Bitmap originalIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
                Bitmap originalResizedIcon = Bitmap.createScaledBitmap(originalIcon, 125, 125, false);
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

                // 경로가 계산되었다는 메시지 표시
//                runOnUiThread(() -> Toast.makeText(RouteMain.this, "경로가 계산되었습니다!", Toast.LENGTH_SHORT).show());
            }
        });


    }

    private void showCurrentLocation() {
        TMapPoint currentPoint = tMapView.getLocationPoint(); // 현재 위치 좌표 가져오기
        if (currentPoint != null) {
            // 지도 중앙을 현재 위치로 이동
            tMapView.setCenterPoint(currentPoint.getLongitude(), currentPoint.getLatitude());

            // 현재 위치 이미지를 보여줌
            ImageView currentLocationImage = findViewById(R.id.currentLocationImage);
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
//            Toast.makeText(RouteMain.this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}