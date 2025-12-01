package data_access;

import entity.User;
import okhttp3.*;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDataAccessObject {

    private static final MediaType JSON_MEDIA = MediaType.parse("application/json");

    private static final String BASE_URL =
            "https://pokemon-dashboard-csc207-default-rtdb.firebaseio.com/users/";

    private final OkHttpClient client = new OkHttpClient();

    // ============================================================
    // CREATE USER (just write to Firebase)
    // ============================================================
    public void createUser(User user) throws Exception {

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

        client.newCall(request).execute();
    }

    public boolean checkPassword(String username, String inputPassword) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + username + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        String jsonString = response.body().string();

        if (jsonString.equals("null")) {
            return false;
        }

        JSONObject userJson = new JSONObject(jsonString);
        String storedPassword = userJson.getString("password");
        return storedPassword.equals(inputPassword);
    }

    // ============================================================
    // LOAD USER
    // ============================================================
    public JSONObject loadUser(User user) throws Exception {

        Request request = new Request.Builder()
                .url(BASE_URL + user.getName() + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();

        if (json.equals("null")) {
            throw new Exception("User does not exist.");
        }

        return new JSONObject(json);
    }
    public boolean userExists(String username) {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + username + ".json")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();
            return !json.equals("null");
        } catch (Exception e) {
            return false;
        }
    }
}