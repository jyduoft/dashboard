package interface_adapter;

import entity.Task;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;

public class TaskListViewModel {

    public static final String PROPERTY_ACTIVE = "activeTasks";
    public static final String PROPERTY_COMPLETED = "completedTasks";
    public static final String PROPERTY_ERROR = "errorMessage";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private List<Task> activeTasks = Collections.emptyList();
    private List<Task> completedTasks = Collections.emptyList();
    private String errorMessage = null;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public List<Task> getActiveTasks() {
        return activeTasks;
    }

    public List<Task> getCompletedTasks() {
        return completedTasks;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setTasks(List<Task> activeTasks, List<Task> completedTasks) {
        List<Task> oldActive = this.activeTasks;
        List<Task> oldCompleted = this.completedTasks;

        this.activeTasks = activeTasks;
        this.completedTasks = completedTasks;
        this.errorMessage = null;

        support.firePropertyChange(PROPERTY_ACTIVE, oldActive, activeTasks);
        support.firePropertyChange(PROPERTY_COMPLETED, oldCompleted, completedTasks);
    }

    public void setErrorMessage(String errorMessage) {
        String oldError = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange(PROPERTY_ERROR, oldError, errorMessage);
    }
}
