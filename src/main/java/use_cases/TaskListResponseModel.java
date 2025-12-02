package use_cases;

import entity.Task;
import entity.Category;

import java.util.ArrayList;
import java.util.List;

public class TaskListResponseModel {

    private final List<Task> activeTasks;
    private final List<Task> completedTasks;
    private final List<Category> categories;

    public TaskListResponseModel(List<Task> activeTasks,
                                 List<Task> completedTasks,
                                 List<Category> categories) {
        this.activeTasks = activeTasks;
        this.completedTasks = completedTasks;
        this.categories = categories;
    }

    public List<Task> getActiveTasks() {
        return activeTasks;
    }
    public List<Task> getCompletedTasks() {
        return completedTasks;
    }
    public List<Category> getCategories() { return categories; }
}

