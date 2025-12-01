package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeatherDataAccessObject {

    private static final OkHttpClient client = new OkHttpClient();

    // Use OpenWeatherMap API, with version 2.5
    // NOTE: Although when you look at website it says 3.0 is free, but this is different track
    //       The free version is 2.5. And you can find details: https://openweathermap.org/current#geo
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "383bd777ae3e664af695517e6b9fffa6";

    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();

    static {
        CITY_COORDS.put("toronto", new double[]{43.65107, -79.347015});
        CITY_COORDS.put("vancouver", new double[]{49.2827, -123.1207});
        CITY_COORDS.put("new york", new double[]{40.7128, -74.0060});
        CITY_COORDS.put("london", new double[]{51.5074, -0.1278});
        CITY_COORDS.put("tokyo", new double[]{35.6895, 139.6917});
        CITY_COORDS.put("beijing", new double[]{39.9042, 116.4074});
        CITY_COORDS.put("paris", new double[]{48.8566, 2.3522});
    }

    public JSONObject fetchCurrentWeather(String city) throws Exception {
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City must not be empty");
        }

        double[] coords = CITY_COORDS.get(city.toLowerCase(Locale.ROOT));
        if (coords == null) {
            throw new IllegalArgumentException("Unsupported city: " + city);
        }

        double lat = coords[0];
        double lon = coords[1];

        String url = BASE_URL +
                "?lat=" + lat +
                "&lon=" + lon +
                "&units=metric" +
                "&appid=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP error: " + response.code());
            }

            String body = response.body().string();
            return new JSONObject(body);
            // It will return a JSON Body like below:
            //            {
            //                "coord": {
            //                "lon": 7.367,
            //                        "lat": 45.133
            //            },
            //                "weather": [
            //                {
            //                    "id": 501,
            //                        "main": "Rain",
            //                        "description": "moderate rain",
            //                        "icon": "10d"
            //                }
            //                            ],
            //                "base": "stations",
            //                    "main": {
            //                "temp": 284.2,
            //                        "feels_like": 282.93,
            //                        "temp_min": 283.06,
            //                        "temp_max": 286.82,
            //                        "pressure": 1021,
            //                        "humidity": 60,
            //                        "sea_level": 1021,
            //                        "grnd_level": 910
            //            },
            //                "visibility": 10000,
            //                    "wind": {
            //                "speed": 4.09,
            //                        "deg": 121,
            //                        "gust": 3.47
            //            },
            //                "rain": {
            //                "1h": 2.73
            //            },
            //                "clouds": {
            //                "all": 83
            //            },
            //                "dt": 1726660758,
            //                    "sys": {
            //                "type": 1,
            //                        "id": 6736,
            //                        "country": "IT",
            //                        "sunrise": 1726636384,
            //                        "sunset": 1726680975
            //            },
            //                "timezone": 7200,
            //                    "id": 3165523,
            //                    "name": "Province of Turin",
            //                    "cod": 200
            //            }

        }
    }
}
