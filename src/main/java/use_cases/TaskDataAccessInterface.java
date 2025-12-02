package use_cases;

import entity.Task;

import java.util.List;

public interface TaskDataAccessInterface {
    Task getTask(String title);
    void saveTask(Task task);
    List<Task> getAllTasks();
}