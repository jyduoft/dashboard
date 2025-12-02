package use_cases;

import org.json.JSONObject;

public interface StockDataAccessInterface {

    double fetchPrice(String symbol) throws Exception;

    JSONObject fetchCompanyProfile(String symbol) throws Exception;

}
