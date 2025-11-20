package use_cases;

import entity.Pokemon;
import entity.User;
import data_access.UserDataAccessObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokemonManager {

    private final ArrayList<Pokemon> inventory;
    private Pokemon currentPokemon;

    public PokemonManager(User user) throws Exception {
        // Load info block from the DAO
        UserDataAccessObject dao = new UserDataAccessObject();
        JSONObject userInfo = dao.loadUser(user);

        // Load inventory
        JSONArray jsonInv = userInfo.optJSONArray("pokemons");
        if (jsonInv == null) {
            inventory = new ArrayList<>();
        } else {
            inventory = convertJsonInventory(jsonInv);
        }

        // Load CURRENT Pokémon
        int currentId = userInfo.optInt("currentPokemonId", -1);
        loadCurrentPokemonById(currentId);
    }

    // --------------------------
    // JSON → Entities conversion
    // --------------------------

    private ArrayList<Pokemon> convertJsonInventory(JSONArray inv) {
        ArrayList<Pokemon> pokemons = new ArrayList<>();
        for (int i = 0; i < inv.length(); i++) {
            pokemons.add(convertJsonPokemon(inv.getJSONObject(i)));
        }
        return pokemons;
    }

    private Pokemon convertJsonPokemon(JSONObject json) {
        return new Pokemon(
                json.optString("name", "unknown"),
                json.optString("imgFilePath", ""),
                json.optInt("id", -1),
                json.optInt("level", 1),
                json.optInt("exp", 0),
                json.optInt("evoReq", 100),
                json.optInt("maxExp", 100)
        );
    }

    public JSONObject toJson() {
        JSONObject infoJson = new JSONObject();

        // Save Pokémon list
        JSONArray invArray = new JSONArray();
        for (Pokemon p : inventory) {
            invArray.put(p.toJson());
        }
        infoJson.put("pokemons", invArray);

        // Save current pokemon ID
        if (currentPokemon != null) {
            infoJson.put("currentPokemonId", currentPokemon.getId());
        }

        return infoJson;
    }


    // --------------------------
    // Current Pokémon management
    // --------------------------

    private void loadCurrentPokemonById(int id) {
        if (id == -1) {
            // If none saved, pick first in inventory if available
            if (!inventory.isEmpty()) {
                currentPokemon = inventory.get(0);
            }
            return;
        }

        for (Pokemon p : inventory) {
            if (p.getId() == id) {
                currentPokemon = p;
                return;
            }
        }

        // If saved ID not found in inventory, fallback to first Pokémon
        if (!inventory.isEmpty()) {
            currentPokemon = inventory.get(0);
        }
    }

    public void setCurrentPokemon(Pokemon p) {
        if (inventory.contains(p)) {
            this.currentPokemon = p;
        }
    }

    public Pokemon getCurrentPokemon() {
        return currentPokemon;
    }

    // --------------------------
    // Inventory access
    // --------------------------

    public ArrayList<Pokemon> getUserInv() {
        return inventory;
    }

    // --------------------------
    // Updating Pokémon
    // --------------------------

    public void giveExpToCurrentPokemon(int exp) {
        if (currentPokemon != null) {
            currentPokemon.addExp(exp);
        }
    }

}
