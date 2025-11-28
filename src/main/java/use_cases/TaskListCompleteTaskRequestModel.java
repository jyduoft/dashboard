package use_cases;

public class TaskListCompleteTaskRequestModel {

    private final String taskId;
    private final boolean complete;

    public TaskListCompleteTaskRequestModel(String taskId, boolean complete) {
        this.taskId = taskId;
        this.complete = complete;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isComplete() {
        return complete;
    }
}
