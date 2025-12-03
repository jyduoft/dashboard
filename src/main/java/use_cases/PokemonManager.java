package use_cases;

import entity.Pokemon;
import entity.PokemonDatabase;
import entity.User;
import data_access.UserDataAccessObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PokemonManager {

    private final ArrayList<Pokemon> inventory;
    private Pokemon currentPokemon;
    private int coins;

    public PokemonManager(User user) throws Exception {
        // Load info block from the DAO
        UserDataAccessObject dao = new UserDataAccessObject();
        JSONObject userInfo = dao.loadUser(user);

        // Get the "info" object if it exists, otherwise use userInfo directly (for backward compatibility)
        JSONObject info = userInfo.optJSONObject("info");
        if (info == null) {
            info = userInfo; // Fallback to direct access if "info" wrapper doesn't exist
        }

        // Load inventory
        JSONArray jsonInv = info.optJSONArray("pokemons");
        if (jsonInv == null) {
            inventory = new ArrayList<>();
        } else {
            inventory = convertJsonInventory(jsonInv);
        }

        // Load CURRENT Pokémon
        int currentId = info.optInt("currentPokemonId", -1);
        loadCurrentPokemonById(currentId);

        // Load coins (default to 0 if not present)
        coins = info.optInt("coins", 0);
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

        // Save coins
        infoJson.put("coins", coins);

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

    // --------------------------
    // Coins management
    // --------------------------

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    // --------------------------
    // Pokemon rolling/gacha
    // --------------------------

    public Pokemon rollPokemon() {
        Random random = new Random();
        Map<Integer, PokemonDatabase.PokemonInfo> allPokemon = PokemonDatabase.getAll();
        
        // Organize Pokemon by rarity
        ArrayList<Integer> commonIds = new ArrayList<>();
        ArrayList<Integer> uncommonIds = new ArrayList<>();
        ArrayList<Integer> rareIds = new ArrayList<>();
        
        for (Map.Entry<Integer, PokemonDatabase.PokemonInfo> entry : allPokemon.entrySet()) {
            String rarity = entry.getValue().rarity;
            if ("common".equals(rarity)) {
                commonIds.add(entry.getKey());
            } else if ("uncommon".equals(rarity)) {
                uncommonIds.add(entry.getKey());
            } else if ("rare".equals(rarity)) {
                rareIds.add(entry.getKey());
            }
        }
        
        // Roll based on probabilities: Rare 15%, Uncommon 30%, Common 55%
        int roll = random.nextInt(100);
        int selectedId;
        
        if (roll < 15) {
            // Rare (0-14, 15%)
            selectedId = rareIds.get(random.nextInt(rareIds.size()));
        } else if (roll < 45) {
            // Uncommon (15-44, 30%)
            selectedId = uncommonIds.get(random.nextInt(uncommonIds.size()));
        } else {
            // Common (45-99, 55%)
            selectedId = commonIds.get(random.nextInt(commonIds.size()));
        }
        
        // Get Pokemon info
        PokemonDatabase.PokemonInfo info = PokemonDatabase.get(selectedId);
        
        // Create new Pokemon instance
        // imgFilePath will be set by the controller after sprite is cached
        Pokemon rolled = new Pokemon(
                info.name,
                "", // imgFilePath will be set later
                selectedId,
                1,  // level: start at 1
                0,  // exp: start at 0
                100, // evoReq: default 100 (can be adjusted if needed)
                100  // maxExp: default 100
        );
        
        // Add to inventory
        inventory.add(rolled);
        
        return rolled;
    }

}
