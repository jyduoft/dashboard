package use_cases;

public class TaskListDeleteCategoryRequestModel {
    private final String categoryName;

    public TaskListDeleteCategoryRequestModel(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
