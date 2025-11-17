package interface_adapter;

import use_cases.ConfigureDashboardInputBoundary;
import use_cases.ConfigureDashboardRequestModel;

public class ConfigureDashboardController {
    private final ConfigureDashboardInputBoundary interactor;

    public ConfigureDashboardController(ConfigureDashboardInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onConfigConfirm(boolean showTasks,
                                boolean showStocks,
                                boolean showWeather,
                                boolean showMap,
                                boolean showPokemon) {
        ConfigureDashboardRequestModel request = new ConfigureDashboardRequestModel(
                showTasks, showStocks, showWeather, showMap, showPokemon
        );
        interactor.configure(request);
    }
}
