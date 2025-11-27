package use_cases;

public class TaskListPinTaskRequestModel {

    private final String taskId;
    private final int pinnedIndex;

    public TaskListPinTaskRequestModel(String taskId, int pinnedIndex) {
        this.taskId = taskId;
        this.pinnedIndex = pinnedIndex;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getPinnedIndex() {
        return pinnedIndex;
    }
}
