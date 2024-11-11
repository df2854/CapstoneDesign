package com.cookandroid.aerobicapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cookandroid.aerobicapplication.Manager.UserdataManager;
import com.cookandroid.aerobicapplication.route.RouteMain;
import com.cookandroid.aerobicapplication.userdata.CommentData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Weather.WeatherApi;
import Weather.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainMenuHomeFragment extends Fragment {
    private TextView recommendText, recordText, weather1, weather2;
    private int userScore = 100;
    private static final String API_KEY = "353054cc32a6f6d8439ead3998765ff9"; // OpenWeatherMap API 키
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_home, container, false);

        recommendText = view.findViewById(R.id.tv_recommendation);
        userScore = UserdataManager.getInstance().getUserScore();
        setRecommendText();

        recordText = view.findViewById(R.id.tv_record);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyApp", MODE_PRIVATE);
        float totalDis = sharedPreferences.getFloat("total", 0);
        recordText.setText(String.format("함께 %.1fkm를 달렸습니다!\n조금 더 힘내볼까요!", totalDis));

        weather1 = view.findViewById(R.id.tv_weather1);
        weather2 = view.findViewById(R.id.tv_weather2);
        getWeather("Busan");

        return view;
    }

    private void setRecommendText(){
        if(userScore > 100)
            recommendText.setText(CommentData.over100);

        else if(userScore == 100)
            recommendText.setText(CommentData.standard);

        else if(userScore > 50)
            recommendText.setText(CommentData.over50);

        else
            recommendText.setText(CommentData.under50);

    }
    private void getWeather(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        Call<WeatherResponse> call = weatherApi.getWeather(city, API_KEY, "metric"); // 온도 단위는 'metric' (섭씨)
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    double temperature = weatherResponse.getMain().getTemp();
                    String weatherMain = TranslateWeather(weatherResponse.getWeather()[0].getMain());
                    String temperatureText = String.format("%.1f", temperature);

                    // UI 업데이트
                    weather1.setText(temperatureText + "°C");
                    weather2.setText(weatherMain);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weather1.setText("Failed");
            }
        });
    }

    private String TranslateWeather(String weather){
        switch(weather){
            case "Clear" : return "맑음";
            case "Clouds" : return "흐림";
            case "Rain" : return "비";
            case "Drizzle" : return "이슬비";
            case "Thunderstorm" : return "천둥번개";
            case "Snow" : return "눈";
            default : return "기타";
        }
    }
}