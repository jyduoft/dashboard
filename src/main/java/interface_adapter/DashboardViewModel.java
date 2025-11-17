package interface_adapter;

import entity.DashboardConfig;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DashboardViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private DashboardConfig config;

    // get current dashboard settings
    public DashboardConfig getConfig() {
        return config;
    }

    // set new dashboard settings
    public void setConfig(DashboardConfig config) {
        // update old settings to new settings
        DashboardConfig old = this.config;
        this.config = config;
        support.firePropertyChange("config", old, config);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
