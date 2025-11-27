package use_cases;

public class TaskListAddTaskRequestModel {

    private final String taskName;
    private final String categoryName;

    public TaskListAddTaskRequestModel(String taskName, String categoryName) {
        this.taskName = taskName;
        this.categoryName = categoryName;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
