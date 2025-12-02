package use_cases;

public class TaskListChangeCategoryRequestModel {

    private final String taskId;
    private final String categoryName;

    public TaskListChangeCategoryRequestModel(String taskId, String categoryName) {
        this.taskId = taskId;
        this.categoryName = categoryName;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
