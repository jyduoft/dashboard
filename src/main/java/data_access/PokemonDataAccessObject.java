package data_access;

import entity.Pokemon;
import entity.User;
import okhttp3.*;
import org.json.JSONObject;
import use_cases.PokemonManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class PokemonDataAccessObject {

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
    // SAVE USER + POKEMON DATA TO FIREBASE
    // ============================================================
    public void saveUserData(User user, PokemonManager manager) throws Exception {

        JSONObject json = new JSONObject();
        // Store the HASHED password, not plain text
        json.put("password", hashPassword(user.getPassword()));
        json.put("info", manager.toJson());         // your full pokemon data

        RequestBody body = RequestBody.create(json.toString(), JSON_MEDIA);

        Request request = new Request.Builder()
                .url(BASE_URL + user.getName() + ".json")
                .put(body)
                .build();

        client.newCall(request).execute(); // no need to parse response
    }

    // ============================================================
    // LOAD USER FROM FIREBASE
    // ============================================================
    public JSONObject loadUserData(User user) throws Exception {

        Request request = new Request.Builder()
                .url(BASE_URL + user.getName() + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String body = response.body().string();

        if (body.equals("null")) {
            throw new Exception("User does not exist.");
        }

        return new JSONObject(body);
    }

    // ============================================================
    // DOWNLOAD AND CACHE GIF SPRITES
    // ============================================================
    public void fetchPokemonSprites(ArrayList<Pokemon> inventory) {

        for (Pokemon pokemon : inventory) {

            int id = pokemon.getId();

            if (!isPokemonCached(id)) {
                downloadSprite(id);
                System.out.println("pokemon doesnt exists so i cache it");
            }
        }
    }

    private void downloadSprite(int id) {

        try {
            Request request = new Request.Builder()
                    .url("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/" + id + ".gif")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) return;

            byte[] bytes = response.body().bytes();
            saveGifToResources(id, bytes);

        } catch (Exception ignored) {}
    }

    private void saveGifToResources(int id, byte[] gifBytes) throws Exception {

        File dir = new File("src/main/resources/cache/pokemon");
        dir.mkdirs();

        File file = new File(dir, id + ".gif");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(gifBytes);
        }
    }

    public boolean isPokemonCached(int id) {
        File file = new File("src/main/resources/cache/pokemon/" + id + ".gif");
        return file.exists();
    }
}
