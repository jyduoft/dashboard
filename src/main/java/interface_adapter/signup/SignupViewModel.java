package interface_adapter.signup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SignupViewModel {
    public final String viewName = "sign up";
    private SignupState state = new SignupState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setState(SignupState state) {
        this.state = state;
    }

    public SignupState getState() {
        return state;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}