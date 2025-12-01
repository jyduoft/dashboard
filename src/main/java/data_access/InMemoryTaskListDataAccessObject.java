package data_access;

import use_cases.TaskDataAccessInterface;
import use_cases.TaskListDataAccessInterface;
import entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared In-Memory Database for both Task List and Timer.
 */
public class InMemoryTaskListDataAccessObject implements TaskListDataAccessInterface, TaskDataAccessInterface {

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
}