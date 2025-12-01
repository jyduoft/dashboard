package view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {
    private String activeViewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setActiveView(String activeViewName) {
        this.activeViewName = activeViewName;
    }

    public String getActiveView() {
        return activeViewName;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("view", null, activeViewName);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}