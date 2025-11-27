package data_access;

import use_cases.TaskListDataAccessInterface;
import entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * in-memory implementation of TaskListDataAccessInterface.
 * will implement firebase later
 */
public class InMemoryTaskListDataAccessObject implements TaskListDataAccessInterface {

    private final List<Task> tasks = new ArrayList<>();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void saveAllTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
    }
}
