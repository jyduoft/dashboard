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
    // PASSWORD HASHING (SHA-256)
    // ============================================================
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convert bytes to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    // ============================================================
    // CREATE USER (with hashed password)
    // ============================================================
    public void createUser(User user) throws Exception {

        JSONObject base = new JSONObject();
        // Store the HASHED password, not the plain text
        base.put("password", hashPassword(user.getPassword()));

        JSONObject info = new JSONObject();
        info.put("pokemons", new org.json.JSONArray());
        info.put("currentPokemonId", -1);
        info.put("coins", 100); // Give new users 100 starting coins

        base.put("info", info);

        RequestBody body = RequestBody.create(base.toString(), JSON_MEDIA);

        Request request = new Request.Builder()
                .url(BASE_URL + user.getName() + ".json")
                .put(body)
                .build();

        client.newCall(request).execute();
    }

    // ============================================================
    // CHECK PASSWORD (compares hashed values)
    // ============================================================
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
        String storedHashedPassword = userJson.getString("password");
        
        // Hash the input password and compare with stored hash
        String inputHashedPassword = hashPassword(inputPassword);
        return storedHashedPassword.equals(inputHashedPassword);
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