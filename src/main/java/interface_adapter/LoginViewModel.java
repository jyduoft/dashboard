package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoginViewModel {
    public static final String VIEW_NAME = "log in";

    private String username = "";
    private String password = "";
    private String error = null;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setState(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setError(String error) {
        String oldError = this.error;
        this.error = error;
        support.firePropertyChange("error", oldError, error);
    }

    public String getError() { return error; }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}