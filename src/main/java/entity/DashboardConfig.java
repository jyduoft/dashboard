package entity;

// Monitor the status of Dashboard

public class DashboardConfig {
    private final boolean showTasks;
    private final boolean showStocks;
    private final boolean showWeather;
    private final boolean showMap;
    private final boolean showPokemon;

    public DashboardConfig(boolean showTasks, boolean showStocks,
                           boolean showWeather, boolean showMap,
                           boolean showPokemon) {
        this.showTasks = showTasks;
        // this.showTasks = true;

        this.showStocks = showStocks;
        this.showWeather = showWeather;
        this.showMap = showMap;
        this.showPokemon = showPokemon;
    }
    // Keep Task always show in the dashboard?
    public boolean isShowTasks() { return showTasks; }
    public boolean isShowStocks() { return showStocks; }
    public boolean isShowWeather() { return showWeather; }
    public boolean isShowMap() { return showMap; }
    public boolean isShowPokemon() { return showPokemon; }
}
