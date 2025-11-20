package entity;

import org.json.JSONObject;

public class Pokemon {
    private String name;
    private String imgFilePath;
    private int id;
    private int level;
    private int exp;
    private int evoReq;
    private int maxExp;

    public Pokemon(String name, String imgFilePath, int id, int level, int exp, int evoReq, int maxExp) {
        this.name = name;
        this.imgFilePath = imgFilePath;
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.evoReq = evoReq;
        this.maxExp = maxExp;
    }

// ---------------------------
// Actions
// ---------------------------

    private void Evolve(){
        //evolves the Pokemon
        //takes the sprite from api and replace the imagefile path with the new evo
        //replaces the evoreq with new evoreq
        //evoreq will be -1 if there is no evo
        //changes the name of the pokemon to the evolved name
    }

    public void addExp(int exp){
        this.exp += exp; //add exp
        if(this.exp >= this.maxExp){
            this.exp -= this.maxExp; //take the exp reuqired to level up
            this.level += 1;
            if((this.evoReq != -1) &&(this.level >= this.evoReq)){
                this.Evolve();
            }
        }
    }
// ---------------------------
// Utilities
// ---------------------------

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("imgFilePath", this.imgFilePath);
        json.put("id", this.id);
        json.put("level", this.level);
        json.put("exp", this.exp);
        json.put("evoReq", this.evoReq);
        json.put("maxExp", this.maxExp);
        return json;
    }

// ---------------------------
// Getters
// ---------------------------
    public String getName() {
        return name;
    }

    public String getImgFilePath() {
        return imgFilePath;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getEvoReq() {
        return evoReq;
    }

    public int getMaxExp() {
        return maxExp;
    }
    public int  getId() {
        return id;
    }
}
