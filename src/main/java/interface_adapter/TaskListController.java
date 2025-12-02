package interface_adapter;

import use_cases.TaskListAddTaskRequestModel;
import use_cases.TaskListChangeCategoryRequestModel;
import use_cases.TaskListCompleteTaskRequestModel;
import use_cases.TaskListInputBoundary;
import use_cases.TaskListPinTaskRequestModel;

public class TaskListController {

    private final TaskListInputBoundary interactor;

    public TaskListController(TaskListInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onDashboardOpened() {
        interactor.viewTaskList();
    }

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
}
