package data_access;

import entity.User;
import okhttp3.*;
import org.json.JSONObject;
import use_cases.login.LoginUserDataAccessInterface;

import java.io.IOException;

public class LoginDataAccessObject implements LoginUserDataAccessInterface {

    private static final String BASE_URL = "https://pokemon-dashboard-csc207-default-rtdb.firebaseio.com/users/";
    private final OkHttpClient client = new OkHttpClient();
    @Override
    public boolean existsByName(String identifier) {
        Request request = new Request.Builder()
                .url(BASE_URL + identifier + ".json")
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            return !body.equals("null");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public User get(String username) {
        Request request = new Request.Builder()
                .url(BASE_URL + username + ".json")
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            if (body.equals("null")) {
                return null;
            }

            JSONObject json = new JSONObject(body);
            String password = json.getString("password");
            return new User(username, password);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(User user) {
    }
}