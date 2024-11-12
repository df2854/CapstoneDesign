package Weather;

public class WeatherResponse {
    private Main main;
    private Weather[] weather;
    private String name; // 도시 이름

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public String getName() {
        return name;
    }

    public class Main {
        private double temp; // 온도

        public double getTemp() {
            return temp;
        }
    }

    public class Weather {
        private String main; // 대략적인 날씨 상태 (예: Clear, Clouds 등)
        private String description; // 자세한 설명 (예: scattered clouds)

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }
    }
}