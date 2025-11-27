package use_cases;
import entity.Task;

public interface TaskDataAccessInterface {
    Task getTask(String title);
    void saveTask(Task task);
}