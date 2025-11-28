package data_access;

import entity.User;
import okhttp3.*;
import org.json.JSONObject;
import use_cases.login.LoginUserDataAccessInterface;
import java.io.IOException;

public class UserDataAccessObject implements LoginUserDataAccessInterface {

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

    // 2. Required to verify password
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

            // IMPORTANT: This assumes your User class has this constructor.
            // If User is an interface, you MUST create a 'CommonUser' class.
            // If User is a class, this works.
            return new entity.User(username, password);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 3. Required by the interface (redirects to teammate's method)
    @Override
    public void save(User user) {
        try {
            createUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }
}
