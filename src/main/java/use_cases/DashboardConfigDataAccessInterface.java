package use_cases;

import entity.DashboardConfig;

public interface DashboardConfigDataAccessInterface {
    void save(DashboardConfig config);
    DashboardConfig load();
}
