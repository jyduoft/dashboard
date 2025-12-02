package use_cases;

import entity.Task;
import entity.Category;
import java.util.List;

/**
 * Here so that it can be linked up to Firebase later
 */
public interface TaskListDataAccessInterface {
    List<Task> getAllTasks();
    void saveAllTasks(List<Task> tasks);

    List<Category> getAllCategories();
    void saveAllCategories(List<Category> categories);
}
