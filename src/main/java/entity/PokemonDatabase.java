package entity;

import java.util.HashMap;
import java.util.Map;

public class PokemonDatabase {

    public static class PokemonInfo {
        public final String name;
        public final String rarity;  // "common", "uncommon", "rare"

        public PokemonInfo(String name, String rarity) {
            this.name = name;
            this.rarity = rarity;
        }
    }

    private static final Map<Integer, PokemonInfo> DATA = new HashMap<>();

    static {
        // Common
        DATA.put(10,  new PokemonInfo("Caterpie", "common"));
        DATA.put(13,  new PokemonInfo("Weedle", "common"));
        DATA.put(16,  new PokemonInfo("Pidgey", "common"));
        DATA.put(19,  new PokemonInfo("Rattata", "common"));
        DATA.put(21,  new PokemonInfo("Spearow", "common"));
        DATA.put(41,  new PokemonInfo("Zubat", "common"));
        DATA.put(50,  new PokemonInfo("Diglett", "common"));

        // Uncommon
        DATA.put(37,  new PokemonInfo("Vulpix", "uncommon"));
        DATA.put(58,  new PokemonInfo("Growlithe", "uncommon"));
        DATA.put(88,  new PokemonInfo("Grimer", "uncommon"));
        DATA.put(111, new PokemonInfo("Rhyhorn", "uncommon"));
        DATA.put(133, new PokemonInfo("Eevee", "uncommon"));

        // Rare
        DATA.put(147, new PokemonInfo("Dratini", "rare"));
        DATA.put(246, new PokemonInfo("Larvitar", "rare"));
        DATA.put(280, new PokemonInfo("Ralts", "rare"));
    }

    // Get full info block
    public static PokemonInfo get(int id) {
        return DATA.get(id);
    }

    // Used by PokemonManager to build rarity pools
    public static Map<Integer, PokemonInfo> getAll() {
        return DATA;
    }
}

