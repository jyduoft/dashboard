package use_cases;

public class TaskListAddOrUpdateCategoryRequestModel {

    private final String categoryName;
    private final int priority;

    public TaskListAddOrUpdateCategoryRequestModel(String categoryName, int priority) {
        this.categoryName = categoryName;
        this.priority = priority;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getPriority() {
        return priority;
    }
}
