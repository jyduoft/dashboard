package entity;

// Monitor the status of Dashboard

public class DashboardConfig {
    private final boolean showTasks;
    private final boolean showStocks;
    private final boolean showWeather;
    private final boolean showSports;
    private final boolean showPokemon;

    public DashboardConfig(boolean showTasks, boolean showStocks,
                           boolean showWeather, boolean showSports,
                           boolean showPokemon) {
        this.showTasks = showTasks;
        // this.showTasks = true;

        this.showStocks = showStocks;
        this.showWeather = showWeather;
        this.showSports = showSports;
        this.showPokemon = showPokemon;
    }
    // Keep Task always show in the dashboard?
    public boolean isShowTasks() { return showTasks; }
    public boolean isShowStocks() { return showStocks; }
    public boolean isShowWeather() { return showWeather; }
    public boolean isShowSports() { return showSports; }
    public boolean isShowPokemon() { return showPokemon; }
}
