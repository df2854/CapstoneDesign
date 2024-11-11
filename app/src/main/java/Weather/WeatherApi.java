package Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String city,  // 도시 이름
            @Query("appid") String apiKey,  // API 키
            @Query("units") String units  // 온도 단위
    );
}
