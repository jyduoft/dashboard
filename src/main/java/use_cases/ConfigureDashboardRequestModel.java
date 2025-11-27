package use_cases;

public class    ConfigureDashboardRequestModel {
    private final boolean showTasks;
    private final boolean showStocks;
    private final boolean showWeather;
    private final boolean showMap;
    private final boolean showPokemon;

    public ConfigureDashboardRequestModel(boolean showTasks, boolean showStocks,
                                          boolean showWeather, boolean showMap,
                                          boolean showPokemon) {
        this.showTasks = showTasks;
        this.showStocks = showStocks;
        this.showWeather = showWeather;
        this.showMap = showMap;
        this.showPokemon = showPokemon;
    }

    public boolean isShowTasks() { return showTasks; }
    public boolean isShowStocks() { return showStocks; }
    public boolean isShowWeather() { return showWeather; }
    public boolean isShowMap() { return showMap; }
    public boolean isShowPokemon() { return showPokemon; }
}
