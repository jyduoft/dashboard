package use_cases;

import org.json.JSONArray;

public interface NbaGamesDataAccessInterface {

    JSONArray fetchLiveBoxScores() throws Exception;
}
