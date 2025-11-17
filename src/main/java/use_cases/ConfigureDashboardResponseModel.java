package use_cases;

import entity.DashboardConfig;

public class ConfigureDashboardResponseModel {
    private final DashboardConfig config;

    public ConfigureDashboardResponseModel(DashboardConfig config) {
        this.config = config;
    }

    public DashboardConfig getConfig() {
        return config;
    }
}
