package use_cases;

import entity.Task;
import java.util.List;

public class TaskListResponseModel {

    private final List<Task> activeTasks;
    private final List<Task> completedTasks;

    public TaskListResponseModel(List<Task> activeTasks, List<Task> completedTasks) {
        this.activeTasks = activeTasks;
        this.completedTasks = completedTasks;
    }

    public List<Task> getActiveTasks() {
        return activeTasks;
    }
    public List<Task> getCompletedTasks() {
        return completedTasks;
    }
}

