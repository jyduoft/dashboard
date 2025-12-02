package use_cases;

public interface TaskListInputBoundary {
    void viewTaskList();
    void addTask(TaskListAddTaskRequestModel requestModel);
    void pinTask(TaskListPinTaskRequestModel requestModel);
    void completeTask(TaskListCompleteTaskRequestModel requestModel);
    void deleteTask(TaskListDeleteTaskRequestModel requestModel);
    void changeCategory(TaskListChangeCategoryRequestModel requestModel);
    void viewCategories();
    void addOrUpdateCategory(TaskListAddOrUpdateCategoryRequestModel requestModel);
    void deleteCategory(TaskListDeleteCategoryRequestModel requestModel);
}
