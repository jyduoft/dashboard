package use_cases;

public class TaskListDeleteTaskRequestModel {

    private final String taskId;

    public TaskListDeleteTaskRequestModel(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
