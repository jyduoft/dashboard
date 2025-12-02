package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import use_cases.NbaGamesDataAccessInterface;

/**
 * Data access object for NBA live games.
 *
 * Currently returns MOCK data (simulated scores).
 * In the future, you can uncomment the real HTTP call and
 * connect to the balldontlie /box_scores/live endpoint.
 */
public class NbaGamesDataAccessObject implements NbaGamesDataAccessInterface {

    // ---------------For future real API use---------------------
    // NOTE: the code below is my free account's API. It can only visit the past data.
    //
    // private static final OkHttpClient client = new OkHttpClient();
    // private static final String LIVE_URL = "https://api.balldontlie.io/v1/box_scores/live";
    // private static final String API_KEY = "67ad7653-7853-4643-b98c-4cedbf20ade7";
    @Override
    public JSONArray fetchLiveBoxScores() throws Exception {
        // -------------MOCK IMPLEMENTATION---------------
        JSONArray data = new JSONArray();

        // Game 1
        JSONObject g1 = new JSONObject();
        g1.put("status", "In Progress");
        g1.put("period", 3);
        g1.put("time", "Q3 05:23");
        g1.put("home_team_score", 207);
        g1.put("visitor_team_score", 236);

        JSONObject home1 = new JSONObject();
        home1.put("abbreviation", "UTG");
        home1.put("name", "UTSG");
        g1.put("home_team", home1);

        JSONObject visitor1 = new JSONObject();
        visitor1.put("abbreviation", "UTM");
        visitor1.put("name", "Missisauga");
        g1.put("visitor_team", visitor1);

        data.put(g1);

        // Game 2
        JSONObject g2 = new JSONObject();
        g2.put("status", "Final");
        g2.put("period", 4);
        g2.put("time", "Final");
        g2.put("home_team_score", 132);
        g2.put("visitor_team_score", 104);

        JSONObject home2 = new JSONObject();
        home2.put("abbreviation", "RAP");
        home2.put("name", "Raptors");
        g2.put("home_team", home2);

        JSONObject visitor2 = new JSONObject();
        visitor2.put("abbreviation", "MIA");
        visitor2.put("name", "Heat");
        g2.put("visitor_team", visitor2);

        data.put(g2);

        // Game 3
        JSONObject g3 = new JSONObject();
        g3.put("status", "Scheduled");
        g3.put("period", 0);
        g3.put("time", "Today 7:30 PM");
        g3.put("home_team_score", 0);
        g3.put("visitor_team_score", 0);

        JSONObject home3 = new JSONObject();
        home3.put("abbreviation", "STU");
        home3.put("name", "Student");
        g3.put("home_team", home3);

        JSONObject visitor3 = new JSONObject();
        visitor3.put("abbreviation", "PRO");
        visitor3.put("name", "Professors");
        g3.put("visitor_team", visitor3);

        data.put(g3);

        return data;

        // -----------------Future Implementation------------------
        /*
        Request request = new Request.Builder()
                .url(LIVE_URL)
                .addHeader("Authorization", API_KEY)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();

            if (!response.isSuccessful()) {
                throw new RuntimeException("NBA HTTP error: " + response.code()
                        + " body: " + body);
            }

            JSONObject root = new JSONObject(body);
            return root.getJSONArray("data");
        }
        */

        // The JSON file returned from live box would looks like:
//        {
//            "data": [
//            {
//                "date": "2024-02-07",
//                    "season": 2023,
//                    "status": "Final",
//                    "period": 4,
//                    "time": "Final",
//                    "postseason": false,
//                    "home_team_score": 117,
//                    "visitor_team_score": 123,
//                    "home_team": {
//                "id": 4,
//                        "conference": "East",
//                        "division": "Southeast",
//                        "city": "Charlotte",
//                        "name": "Hornets",
//                        "full_name": "Charlotte Hornets",
//                        "abbreviation": "CHA",
//                        "players": [
//                {
//                    "min": "23",
//                        "fgm": 1,
//                        "fga": 2,
//                        "fg_pct": 0.5,
//                        "fg3m": 0,
//                        "fg3a": 1,
//                        "fg3_pct": 0,
//                        "ftm": 1,
//                        "fta": 2,
//                        "ft_pct": 0.5,
//                        "oreb": 0,
//                        "dreb": 3,
//                        "reb": 3,
//                        "ast": 3,
//                        "stl": 0,
//                        "blk": 1,
//                        "turnover": 1,
//                        "pf": 1,
//                        "pts": 3,
//                        "player": {
//                    "id": 56677866,
//                            "first_name": "Leaky",
//                            "last_name": "Black",
//                            "position": "F",
//                            "height": "6-6",
//                            "weight": "209",
//                            "jersey_number": "12",
//                            "college": "North Carolina",
//                            "country": "USA",
//                            "draft_year": null,
//                            "draft_round": null,
//                            "draft_number": null
//                }
//                },
//          ...
//        ]
//            },
//                "visitor_team": {
//                "id": 28,
//                        "conference": "East",
//                        "division": "Atlantic",
//                        "city": "Toronto",
//                        "name": "Raptors",
//                        "full_name": "Toronto Raptors",
//                        "abbreviation": "TOR",
//                        "players": [
//                {
//                    "min": "15",
//                        "fgm": 2,
//                        "fga": 3,
//                        "fg_pct": 0.6666667,
//                        "fg3m": 0,
//                        "fg3a": 0,
//                        "fg3_pct": 0,
//                        "ftm": 0,
//                        "fta": 0,
//                        "ft_pct": 0,
//                        "oreb": 3,
//                        "dreb": 5,
//                        "reb": 8,
//                        "ast": 2,
//                        "stl": 1,
//                        "blk": 0,
//                        "turnover": 1,
//                        "pf": 2,
//                        "pts": 4,
//                        "player": {
//                    "id": 489,
//                            "first_name": "Thaddeus",
//                            "last_name": "Young",
//                            "position": "F",
//                            "height": "6-8",
//                            "weight": "225",
//                            "jersey_number": "21",
//                            "college": "Georgia Tech",
//                            "country": "USA",
//                            "draft_year": 2007,
//                            "draft_round": 1,
//                            "draft_number": 12
//                }
//                },
//          ...
//        ]
//            }
//            },
//  ...
//  ]
//        }
    }
}
