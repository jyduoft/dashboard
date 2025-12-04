package interface_adapter;

import use_cases.*;

public class TaskListController {

    private final TaskListInputBoundary interactor;

    public TaskListController(TaskListInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onDashboardOpened() {
        interactor.viewTaskList();
    }


    // Tasks
    public void onAddTask(String taskName) {
        TaskListAddTaskRequestModel request =
                new TaskListAddTaskRequestModel(taskName, null);
        interactor.addTask(request);
    }

    public void onPinTask(String taskId, int index) {
        TaskListPinTaskRequestModel request =
                new TaskListPinTaskRequestModel(taskId, index);
        interactor.pinTask(request);
    }

    public void onToggleComplete(String taskId, boolean complete) {
        TaskListCompleteTaskRequestModel request =
                new TaskListCompleteTaskRequestModel(taskId, complete);
        interactor.completeTask(request);
    }

    public void onChangeCategory(String taskId, String categoryName) {
        TaskListChangeCategoryRequestModel request =
                new TaskListChangeCategoryRequestModel(taskId, categoryName);
        interactor.changeCategory(request);
    }

    public void onDeleteTask(String taskId) {
        TaskListDeleteTaskRequestModel request =
                new TaskListDeleteTaskRequestModel(taskId);
        interactor.deleteTask(request);
    }


    // Categories
    public void onViewCategories() {
        interactor.viewCategories();
    }

    public void onAddOrUpdateCategory(String categoryName, int priority) {
        TaskListAddOrUpdateCategoryRequestModel request =
                new TaskListAddOrUpdateCategoryRequestModel(categoryName, priority);
        interactor.addOrUpdateCategory(request);
    }

    public void onDeleteCategory(String categoryName) {
        TaskListDeleteCategoryRequestModel request =
                new TaskListDeleteCategoryRequestModel(categoryName);
        interactor.deleteCategory(request);
    }
}
