package entity;

// Monitor the status of Dashboard

public class DashboardConfig {
    private final boolean showTasks;
    private final boolean showStocks;
    private final boolean showWeather;
    private final boolean showSports;
    private final boolean showMaps;
    private final boolean showPokemon;

    public DashboardConfig(boolean showTasks, boolean showStocks,
                           boolean showWeather, boolean showSports,
                           boolean showMaps, boolean showPokemon) {
        this.showTasks = showTasks;
        this.showStocks = showStocks;
        this.showWeather = showWeather;
        this.showSports = showSports;
        this.showMaps = showMaps;
        this.showPokemon = showPokemon;
    }
    public boolean isShowTasks() { return showTasks; }
    public boolean isShowStocks() { return showStocks; }
    public boolean isShowWeather() { return showWeather; }
    public boolean isShowSports() { return showSports; }
    public boolean isShowMaps() { return showMaps; }
    public boolean isShowPokemon() { return showPokemon; }
}
