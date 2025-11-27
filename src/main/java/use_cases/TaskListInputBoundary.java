package use_cases;

public interface TaskListInputBoundary {
    void viewTaskList();
    void addTask(TaskListAddTaskRequestModel requestModel);
    void pinTask(TaskListPinTaskRequestModel requestModel);
    void completeTask(TaskListCompleteTaskRequestModel requestModel);
}
