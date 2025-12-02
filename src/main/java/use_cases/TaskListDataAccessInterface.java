package use_cases;

import entity.Category;
import entity.Task;
import java.util.List;

/**
 * Here so that it can be linked up to Firebase later
 */
public interface TaskListDataAccessInterface {

    // Tasks
    List<Task> getAllTasks();
    void saveAllTasks(List<Task> tasks);

    // Categories
    List<Category> getAllCategories();
    void saveAllCategories(List<Category> categories);
}
