package com.cookandroid.aerobicapplication.route;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
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


import com.cookandroid.aerobicapplication.Manager.ExercisedataManager;
import com.cookandroid.aerobicapplication.R;
import com.cookandroid.aerobicapplication.SignupActivity;
import com.cookandroid.aerobicapplication.TTSManager;
import com.cookandroid.aerobicapplication.userdata.CommentData;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RouteMain extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    //TTS 관련
    private TTSManager ttsManager;
    private CommentData commentData;
    private Handler handlerTTS;
    private Runnable ttsRunnable;
    private Random random;
    private final int MIN_DELAY_MS = 30000; // 최소 30초     // 랜덤 시간 간격 설정 (예: 5초 ~ 15초 사이)
    private final int MAX_DELAY_MS = 60000; // 최대 60초

    //Tmap
    private TMapView tMapView;
    private ArrayList<TMapPoint> markerPoints = new ArrayList<>();  // 마커 위치 저장
    private boolean isStartPointSet = false;  // 출발지 설정 여부
    private boolean isMarkerAddEnabled = false;
    private TMapMarkerItem currentMarker;  // 현재 마커 객체 저장
    ArrayList<TMapMarkerItem> makerList = new ArrayList<>();
    private TMapGpsManager tMapGpsManager;

    private int marker = R.drawable.markermm;

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
    private double estiimatedCalrorie = 0;


    // Summary 필드 시간, 거리, 속도

    private TextView timeText, summaryTimeText;
    private Button btnStartRoute,btnSummaryResume, btnResume;
    private long startTime; // 타이머 시작 시간
    private long pausedTime; // 일시 정지된 시간
    private Handler handler = new Handler(); // 타이머를 업데이트하는 핸들러
    private boolean isRunning = false;

    // 필요한 변수 선언
    private boolean isTracking = false;
    private int currentCount = 0; // 갱신 횟수
    private static final double METER_TO_KM_CONVERSION = 0.001; // m를 km로 변환
    private long elapsedTime = 0; // 경과 시간
    // 속도 기록
    private List<Double> speedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cookandroid.aerobicapplication.R.layout.activity_start_workout);

        //TTS
        commentData = new CommentData();
        ttsManager = new TTSManager(this, commentData);
        handlerTTS = new Handler();
        random = new Random();
        // Runnable 설정
        ttsRunnable = new Runnable() {
            @Override
            public void run() {
                ttsManager.speakRandomTip(); // 팁을 랜덤으로 재생

                // 다음 실행을 위해 랜덤 지연 시간 설정
                int randomDelay = MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS);
                handler.postDelayed(ttsRunnable, randomDelay);
            }
        };

        // 위치 권한 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            initializeGps();
        }

        // GPS 시작 플래그 확인
        if (getIntent().getBooleanExtra("startGps", false)) {
            // GPS 기능을 활성화하여 현재 위치 가져오기
            if (tMapGpsManager != null) {
                tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
                tMapGpsManager.OpenGps();
//                Toast.makeText(this, "현재 위치를 가져옵니다.", Toast.LENGTH_SHORT).show();
            }
        }



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
        Button btnSetStartPoint = findViewById(R.id.btnSetStart); // 출발지 설정 버튼
        Button btnEndSet = findViewById(R.id.btnEndSet);
        Button btnReset = findViewById(R.id.btnReset);
        // 컴파스 모드 활성화 (기기 방향에 따라 지도 회전)
        //tMapView.setCompassMode(true);
        //임시 운동종료
        Button endTextView = findViewById(R.id.endButton);
        endTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), WorkoutResultActivity.class);
            startActivity(intent);
        });

        // 현재 위치 버튼 클릭 시 실행
        ImageButton btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(v -> {
            if (tMapGpsManager != null) {
                tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
                tMapGpsManager.OpenGps();
                Toast.makeText(this, "현재 위치를 가져옵니다.", Toast.LENGTH_SHORT).show();
                tMapView.setSightVisible(true);
            }
        });

        LinearLayout backGoButtonLayout = findViewById(R.id.backGoButtonLayout);
        // 출발지 설정 버튼 클릭 리스너 추가
        btnSetStartPoint.setOnClickListener(v -> {
            if (markerPoints.isEmpty()) {
                Toast.makeText(this, "마커를 먼저 추가해 주세요.", Toast.LENGTH_SHORT).show();
            }else if (!isStartPointSet) {
                isStartPointSet = true;  // 출발지 설정이 완료되었으므로 여러 개의 마커를 찍을 수 있도록 변경
                btnSetStartPoint.setVisibility(View.GONE);  // "출발지 설정" 버튼 숨기기
                backGoButtonLayout.setVisibility(View.VISIBLE);
            }
        });

        // 경로 지정 완료 버튼 클릭 리스너 추가
        btnEndSet.setOnClickListener(v -> {
            if (markerPoints.size() < 2) {
                Toast.makeText(this, "경로를 계산하기 위해 최소 2개의 마커가 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {

                if (!makerList.isEmpty()) {
                    TMapMarkerItem lastMarker = makerList.get(makerList.size() - 1);
                    tMapView.invalidate();  // 마커 이미지 변경 후 강제 갱신
                }
                // 총 거리 초기화
                totalDistance = 0;
                isStartPointSet = false;  // 출발지 설정이 완료되었으므로 여러 개의 마커를 찍을 수 있도록 변경
                isMarkerAddEnabled = false;
                // 모든 마커 간의 거리 계산
                for (int i = 0; i < markerPoints.size() - 1; i++) {
                    TMapPoint startPoint = markerPoints.get(i);
                    TMapPoint endPoint = markerPoints.get(i + 1);
                    totalDistance += calculateDistance(startPoint, endPoint);
                    calculateRoute(startPoint, endPoint);
                }

                // 총 거리 킬로미터로 변환 및 소수점 한 자리 수로 표현
                totalDistanceInKm = totalDistance / 1000.0;
                totalDistanceInKm = Double.parseDouble(String.format("%.3f", totalDistanceInKm)); // 소수점 3자리로 포맷

                // 예상 시간 계산
                double speed = 4;
                double estimatedTimeInHours = totalDistanceInKm / speed;
                estimatedTimeInMinutes = (int) (estimatedTimeInHours * 60);

                // 예상 칼로리 계산
                estiimatedCalrorie = totalDistance * 0.04;

                // 결과 표시
                runOnUiThread(() -> {
                    estimatedDistanceTextView.setText(String.format("%.3fkm", totalDistanceInKm));
                    estimatedTimeTextView.setText(String.format("%d분", estimatedTimeInMinutes));
                    estimatedCalorieTextView.setText(String.format("%.2fkcal", estiimatedCalrorie));
                    infoLayout.setVisibility(View.VISIBLE);
                    buttonLayout.setVisibility(View.GONE);
                });
            }
        });

        // 달리기 시작시 표현되는 레이아웃
        Button btnStartRoute = findViewById(R.id.btnStartRoute);
        TextView runningTextview = findViewById(R.id.runningTextView);
        LinearLayout runningContainerLayout = findViewById(R.id.runningContainerLayout);
        btnStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerTTS.post(ttsRunnable);                //TTS 첫실행

                infoLayout.setVisibility(View.GONE);
                runningContainerLayout.setVisibility(View.VISIBLE);
                runningTextview.setVisibility(View.VISIBLE);
                startTimer();
                isTracking = true;
                currentCount = 0; // 측정 시작 시 초기화
                startTime = System.currentTimeMillis(); // 경과 시간 시작
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
            backGoButtonLayout.setVisibility(View.GONE);
            runningTextview.setVisibility(View.GONE);
            btnSetStartPoint.setVisibility(View.VISIBLE); // 출발지 설정 버튼 다시 보이기
            buttonLayout.setVisibility(View.VISIBLE);

            Toast.makeText(this, "경로가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 맵 -> 정보
        LinearLayout entireSummaryInfoLayout = findViewById(R.id.entireSummaryInfoLayout);
        ImageButton btnGoSummaryInfo = findViewById(R.id.btnGoSummaryInfo);
        btnGoSummaryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runningContainerLayout.setVisibility(View.GONE);
                entireSummaryInfoLayout.setVisibility(View.VISIBLE);
            }
        });

        // 정보 -> 맵
        ImageButton btnGoMapInfo = findViewById(R.id.btnGoMapInfo);
        btnGoMapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runningContainerLayout.setVisibility(View.VISIBLE);
                entireSummaryInfoLayout.setVisibility(View.GONE);
            }
        });

        //시간
        timeText = findViewById(R.id.timeText);
        summaryTimeText = findViewById(R.id.summaryTimeText);
        btnResume = findViewById(R.id.btnResume);
        btnSummaryResume = findViewById(R.id.btnSummaryResume);

        // Finish 버튼 클릭 리스너 (평균 속도 계산)
        Button btnFinish = findViewById(R.id.btnFinish);
        Button btnSumarryFinish = findViewById(R.id.btnSummaryFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 타이머 멈추기
                pauseTimer();

                // 총 거리, 총 시간 계산
                double totalDistanceKm = currentCount * METER_TO_KM_CONVERSION; // 거리 계산 (km)
                long elapsedTimeInMillis = System.currentTimeMillis() - startTime;

                // 예상 칼로리 계산
                double totalDistanceMeters = currentCount; // 총 거리 (미터 단위)
                double estimatedCalories = totalDistanceMeters * 0.04; // 칼로리 계산
                double averageSpeed =  (totalDistanceKm / (elapsedTimeInMillis / 1000.0)) * 3600.0;

                // TTS stop
                ttsManager.shutdown();

                // 데이터 저장
                ExercisedataManager.getInstance().setCurrentMinTime(elapsedTimeInMillis/60000);
                ExercisedataManager.getInstance().setCurrentSecTime((elapsedTimeInMillis % 60000) / 1000);
                ExercisedataManager.getInstance().setCurrentDistance(totalDistanceKm);

                // 칼로리 계산
                //estimatedCalories = ExercisedataManager.getInstance().getCurrentKcal(); // 칼로리 계산

                // Intent 생성하여 데이터 전달
                Intent intent = new Intent(RouteMain.this, WorkoutResultActivity.class);
                intent.putExtra("totalDistance", totalDistanceKm); // 총 거리
                intent.putExtra("elapsedTime", elapsedTimeInMillis); // 총 시간
                intent.putExtra("averageSpeed", averageSpeed); // 평균 속도
                intent.putExtra("estimatedCalories", estimatedCalories); // 칼로리

                startActivity(intent); // 결과 화면으로 이동
            }
        });

        btnSumarryFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 타이머 멈추기
                pauseTimer();

                // 총 거리, 총 시간 계산
                double totalDistanceKm = currentCount * METER_TO_KM_CONVERSION; // 거리 계산 (km)
                long elapsedTimeInMillis = System.currentTimeMillis() - startTime;

                // 예상 칼로리 계산
                double totalDistanceMeters = currentCount; // 총 거리 (미터 단위)
                double estimatedCalories = totalDistanceMeters * 0.04; // 칼로리 계산
                double averageSpeed =  (totalDistanceKm / (elapsedTimeInMillis / 1000.0)) * 3600.0;

                // TTS stop
                ttsManager.shutdown();

                // 데이터 저장
                ExercisedataManager.getInstance().setCurrentMinTime(elapsedTimeInMillis/60000);
                ExercisedataManager.getInstance().setCurrentSecTime((elapsedTimeInMillis % 60000) / 1000);
                ExercisedataManager.getInstance().setCurrentDistance(totalDistanceKm);

                // 칼로리 계산
                estimatedCalories = ExercisedataManager.getInstance().getCurrentKcal(); // 칼로리 계산

                // Intent 생성하여 데이터 전달
                Intent intent = new Intent(RouteMain.this, WorkoutResultActivity.class);
                intent.putExtra("totalDistance", totalDistanceKm); // 총 거리
                intent.putExtra("elapsedTime", elapsedTimeInMillis); // 총 시간
                intent.putExtra("averageSpeed", averageSpeed); // 평균 속도
                intent.putExtra("estimatedCalories", estimatedCalories); // 칼로리

                startActivity(intent); // 결과 화면으로 이동
            }
        });

        // RESUME 버튼 클릭 리스너 설정
        btnResume.setOnClickListener(v -> {
            if (isRunning) {
                // 타이머 멈춤
                pauseTimer();

                // 거리 및 칼로리 측정 멈춤
                isTracking = false;

            } else {
                // 타이머 다시 시작
                startTimer();

                // 거리 및 칼로리 측정 시작
                isTracking = true;

                // 거리 및 칼로리 측정 초기화
                startTime = System.currentTimeMillis() - pausedTime; // 시간 재설정, pausedTime을 고려하여 시
            }
        });

        // RESUME 버튼 클릭 리스너 설정
        btnSummaryResume.setOnClickListener(v -> {
            if (isRunning) {
                // 타이머 멈춤
                pauseTimer();

                // 거리 및 칼로리 측정 멈춤
                isTracking = false;

            } else {
                // 타이머 다시 시작
                startTimer();

                // 거리 및 칼로리 측정 시작
                isTracking = true;

                // 거리 및 칼로리 측정 초기화
                startTime = System.currentTimeMillis() - pausedTime; // 시간 재설정, pausedTime을 고려하여 시
            }
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

    // 클릭한 위치에 마커를 추가하는 메서드
    private void addMarker(TMapPoint point) {
       if(isMarkerAddEnabled) { // 기존 마커 삭제
           if (isStartPointSet == false) {
               removeCurrentMarker();
           }

           TMapMarkerItem markerItem = new TMapMarkerItem();

           Bitmap icon = BitmapFactory.decodeResource(getResources(), marker);  // 강조된 마커 이미지 로드
           Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, 120, 140, false);  // 크기 조정 (예: 120x120)

           // 마커의 아이콘을 강조된 아이콘으로 설정
           markerItem.setIcon(resizedIcon);

           // 마커의 위치 설정
           markerItem.setTMapPoint(point);
           markerItem.setName("클릭한 위치");

           // 마커의 핀 설정 (중앙에 위치하도록)
           markerItem.setPosition(0.5f, 1.0f);

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
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.markermm);  // 강조된 마커 이미지 로드
            Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, 150, 170, false);  // 크기 조정 (예: 120x120)

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
                Bitmap originalIcon = BitmapFactory.decodeResource(getResources(), R.drawable.markermm);
                Bitmap originalResizedIcon = Bitmap.createScaledBitmap(originalIcon, 120, 140, false);
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
                polyLine.setLineWidth(25); // 경로 라인의 두께 설정
                polyLine.setOutLineWidth(35);

                // 지도에 경로 추가
                tMapView.addTMapPolyLine("line_" + startPoint.toString() + "_" + endPoint.toString(), polyLine);
            }
        });

    }

   ////////////////////////Gps///////////////////////////

    private void initializeGps() {
        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setMinDistance(1);
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

        // 거리 측정 중일 경우 갱신 횟수 기반 거리 업데이트
        if (isTracking) {
            currentCount++; // 갱신 횟수 증가
            updateDistanceAndCalorie(); // 거리 및 칼로리 갱신
        }

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setTMapPoint(currentLocation);
        markerItem.setName("현재 위치");

        // drawable에서 아이콘 설정
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gpscircle);
        // 비트맵 크기 조정
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, false);
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

    ////////////////////러닝/////////////////////

    // 타이머 업데이트 Runnable
    private Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long elapsedTime = System.currentTimeMillis() - startTime;  // 실제 경과 시간 계산
                updateTimeText(elapsedTime);  // UI 업데이트 (시간 출력)

                // 1초마다 타이머 업데이트
                handler.postDelayed(this, 1000);
            }
        }
    };
    // 타이머 시작 함수
    private void startTimer() {
        startTime = System.currentTimeMillis() - pausedTime;  // 재시작할 때, pausedTime을 고려하여 시작
        isRunning = true;  // 타이머 상태 변경
        handler.post(updateTimerRunnable);  // 타이머 업데이트 시작
    }

    // 타이머 멈추는 함수
    private void pauseTimer() {
        long currentTime = System.currentTimeMillis();
        pausedTime += currentTime - startTime;  // 일시정지된 시간 저장
        isRunning = false;  // 타이머 상태 변경
        handler.removeCallbacks(updateTimerRunnable);  // 타이머 업데이트 중지
    }

    // 경과 시간을 화면에 출력하는 함수
    private void updateTimeText(long elapsedTime) {
        long elapsedSeconds = elapsedTime / 1000;  // 초 단위로 변환
        long minutes = elapsedSeconds / 60;  // 분 단위
        long seconds = elapsedSeconds % 60;  // 초 단위

        // 텍스트뷰에 경과 시간 업데이트
        TextView timeTextView = findViewById(R.id.timeText);
        timeTextView.setText(String.format("%02d:%02d", minutes, seconds));  // MM:SS 형식으로 출력
        // 텍스트뷰에 경과 시간 업데이트
        TextView summaryTimeText = findViewById(R.id.summaryTimeText);
        summaryTimeText.setText(String.format("%02d:%02d", minutes, seconds));  // MM:SS 형식으로 출력
        currentCount++;
        updateDistanceAndCalorie();
    }


    // 거리 및 소모 칼로리 갱신
    private void updateDistanceAndCalorie() {
        double totalDistanceKm = currentCount * METER_TO_KM_CONVERSION; // 거리 계산 (km)

        // 거리 텍스트 업데이트
        TextView mapDistanceText = findViewById(R.id.mapdistanceText);
        mapDistanceText.setText(String.format(Locale.getDefault(), "%.3f", totalDistanceKm)); // 소수점 3자리까지
        TextView summaryDistanceText = findViewById(R.id.summaryDistanceText);
        summaryDistanceText.setText(String.format(Locale.getDefault(), "%.3f", totalDistanceKm)); // 소수점 3자리까지

        // 경과 시간 계산
        elapsedTime = System.currentTimeMillis() - pausedTime; // 시간 계산
        // 소모 칼로리 계산 (1m당 0.04 kcal)
        double totalDistanceMeters = currentCount; // 총 거리 (미터 단위)
        double estimatedCalories = totalDistanceMeters * 0.04; // 칼로리 계산

        // 칼로리 텍스트 업데이트
        TextView summaryCalorieText = findViewById(R.id.summaryCalorieText);
        summaryCalorieText.setText(String.format(Locale.getDefault(), "%.2f kcal", estimatedCalories)); // 소수점 2자리까지 표시
        TextView calText = findViewById(R.id.calText);
        calText.setText(String.format(Locale.getDefault(), "%.2f kcal", estimatedCalories)); // 소수점 2자리까지 표시
    }
}