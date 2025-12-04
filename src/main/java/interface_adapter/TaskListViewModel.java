package interface_adapter;

import entity.Category;
import entity.Task;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;

public class TaskListViewModel {

    public static final String PROPERTY_ACTIVE = "activeTasks";
    public static final String PROPERTY_COMPLETED = "completedTasks";
    public static final String PROPERTY_CATEGORIES = "categories";
    public static final String PROPERTY_ERROR = "errorMessage";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private List<Task> activeTasks = Collections.emptyList();
    private List<Task> completedTasks = Collections.emptyList();
    private List<Category> categories = Collections.emptyList();
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

    public List<Category> getCategories() {
        return categories;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Tasks
    public void setTasks(List<Task> activeTasks, List<Task> completedTasks) {
        List<Task> oldActive = this.activeTasks;
        List<Task> oldCompleted = this.completedTasks;

        this.activeTasks = activeTasks;
        this.completedTasks = completedTasks;
        this.errorMessage = null;

        // Force notifications even if contents are "equal"
        support.firePropertyChange(PROPERTY_ACTIVE, null, activeTasks);
        support.firePropertyChange(PROPERTY_COMPLETED, null, completedTasks);
    }

    // Categories
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        support.firePropertyChange(PROPERTY_CATEGORIES, null, categories);
    }

    // Error - used by both
    public void setErrorMessage(String errorMessage) {
        String oldError = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange(PROPERTY_ERROR, oldError, errorMessage);
    }
}
