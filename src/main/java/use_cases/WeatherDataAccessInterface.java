package use_cases;

import org.json.JSONObject;

public interface WeatherDataAccessInterface {

    JSONObject fetchCurrentWeather(String city) throws Exception;
}
