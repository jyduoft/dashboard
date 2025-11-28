package data_access;

import entity.User;
import okhttp3.*;
import org.json.JSONObject;
import use_cases.signup.SignupUserDataAccessInterface;

import java.io.IOException;

public class SignupDataAccessObject implements SignupUserDataAccessInterface {

    private static final MediaType JSON_MEDIA = MediaType.parse("application/json");
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
    public void save(User user) {
        JSONObject base = new JSONObject();
        base.put("password", user.getPassword());

        JSONObject info = new JSONObject();
        info.put("pokemons", new org.json.JSONArray());
        info.put("currentPokemonId", -1);
        base.put("info", info);

        RequestBody body = RequestBody.create(base.toString(), JSON_MEDIA);

        Request request = new Request.Builder()
                .url(BASE_URL + user.getName() + ".json")
                .put(body)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}