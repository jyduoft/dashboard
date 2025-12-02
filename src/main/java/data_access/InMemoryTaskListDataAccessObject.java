package data_access;

import use_cases.TaskDataAccessInterface;
import use_cases.TaskListDataAccessInterface;
import entity.Task;
import entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared In-Memory Database for both Task List and Timer.
 * Also stores categories
 */
public class InMemoryTaskListDataAccessObject implements TaskListDataAccessInterface, TaskDataAccessInterface {

    private final List<Task> tasks = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();

    public InMemoryTaskListDataAccessObject() {
        // Ensure UNSORTED category always exists
        categories.add(Category.UNSORTED);
    }

    // ====================
    // Task stuff
    // ====================
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void saveAllTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
    }

    @Override
    public Task getTask(String id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public void saveTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                return;
            }
        }
        tasks.add(task);
    }

    // ====================
    // Category stuff
    // ====================
    @Override
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    @Override
    public void saveAllCategories(List<Category> newCategories) {
        categories.clear();

        // Always ensure UNSORTED exists
        categories.add(Category.UNSORTED);

        if (newCategories == null) {
            return;
        }

        // Avoid duplicating UNSORTED and preserve other categories
        for (Category c : newCategories) {
            if (c == null) continue;
            // skip if same as UNSORTED by name (or by reference)
            if (Category.UNSORTED.getName().equalsIgnoreCase(c.getName())) {
                continue;
            }
            categories.add(c);
        }
    }
}