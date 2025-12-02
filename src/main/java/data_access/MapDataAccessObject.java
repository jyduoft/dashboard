package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import use_cases.MapDataAccessInterface;

import javax.swing.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * DAO for Geoapify Static Maps API.
 * It returns an ImageIcon that can be shown in a JLabel.
 */
public class MapDataAccessObject implements MapDataAccessInterface {

    private static final OkHttpClient client = new OkHttpClient();

    private static final String BASE_URL = "https://maps.geoapify.com/v1/staticmap";

    private static final String API_KEY = "056244a011f8460bbb08032565d600eb";

    // We set the same cities with weather
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();

    static {
        CITY_COORDS.put("toronto", new double[]{43.65107, -79.347015});
        CITY_COORDS.put("vancouver", new double[]{49.2827, -123.1207});
        CITY_COORDS.put("new york", new double[]{40.7128, -74.0060});
        CITY_COORDS.put("london", new double[]{51.5074, -0.1278});
        CITY_COORDS.put("tokyo", new double[]{35.6895, 139.6917});
        CITY_COORDS.put("beijing", new double[]{39.9042, 116.4074});
        CITY_COORDS.put("sydney", new double[]{-33.8688, 151.2093});
        CITY_COORDS.put("dubai", new double[]{25.2048, 55.2708});
        CITY_COORDS.put("paris", new double[]{48.8566, 2.3522});
    }
    @Override
    public ImageIcon fetchMapForCity(String city) throws Exception {
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City must not be empty");
        }

        double[] coords = CITY_COORDS.get(city.toLowerCase(Locale.ROOT));
        if (coords == null) {
            throw new IllegalArgumentException("Unsupported city: " + city);
        }

        double lat = coords[0];
        double lon = coords[1];

        String url = BASE_URL
                + "?style=osm-bright"
                + "&width=400"
                + "&height=250"
                + "&center=lonlat:" + lon + "," + lat
                + "&zoom=11"
                + "&apiKey=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "";
                throw new RuntimeException("Map HTTP error: " + response.code() + " body: " + body);
            }

            byte[] bytes = response.body().bytes();
            return new ImageIcon(bytes);
        }
    }
}