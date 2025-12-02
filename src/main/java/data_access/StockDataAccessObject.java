package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import use_cases.StockDataAccessInterface;

public class StockDataAccessObject implements StockDataAccessInterface {

    private static final OkHttpClient client = new OkHttpClient();

    private static final String API_KEY = "d4kc2cpr01qvpdoj9gk0d4kc2cpr01qvpdoj9gkg";
    private static final String BASE_URL = "https://finnhub.io/api/v1";

    /**
     *Use Finnhub API to get current price of a stock
     */
    public double fetchPrice(String symbol) throws Exception {

        String url = BASE_URL + "/quote?symbol=" + symbol + "&token=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                int code = response.code();
                if (code == 429) {
                    throw new RuntimeException("Rate limit exceeded (HTTP 429)");
                }
                throw new RuntimeException("HTTP error: " + code);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            double currentPrice = json.getDouble("c");

            return currentPrice;
        }
    }


    public JSONObject fetchCompanyProfile(String symbol) throws Exception {

        String url = BASE_URL + "/stock/profile2?symbol=" + symbol + "&token=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                int code = response.code();
                if (code == 429) {
                    throw new RuntimeException("Rate limit exceeded (HTTP 429)");
                }
                throw new RuntimeException("HTTP error: " + code);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            return json;
        }
    }
}
