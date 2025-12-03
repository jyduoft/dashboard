package use_cases;

import entity.Category;
import entity.Task;
import entity.TaskList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskListInteractor implements TaskListInputBoundary {

    private final TaskListDataAccessInterface taskListDataAccessInterface;
    private final TaskListOutputBoundary presenter;

    public TaskListInteractor(TaskListDataAccessInterface taskListDataAccessInterface,
                              TaskListOutputBoundary presenter) {
        this.taskListDataAccessInterface = taskListDataAccessInterface;
        this.presenter = presenter;
    }

    @Override
    public void viewTaskList() {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();
        TaskListResponseModel responseModel = buildResponse(tasks);
        presenter.prepareSuccessView(responseModel);
    }

    @Override
    public void addTask(TaskListAddTaskRequestModel requestModel) {
        try {
            List<Task> tasks = taskListDataAccessInterface.getAllTasks();

            Category category = Category.UNSORTED;
            Task newTask = Task.TaskFactory.createTask(requestModel.getTaskName(), category);

            tasks.add(newTask);

            taskListDataAccessInterface.saveAllTasks(tasks);
            TaskListResponseModel responseModel = buildResponse(tasks);
            presenter.prepareSuccessView(responseModel);
        } catch (Exception e) {
            presenter.prepareFailView("Could not add task: " + e.getMessage());
        }
    }

    @Override
    public void pinTask(TaskListPinTaskRequestModel requestModel) {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();

        TaskList taskList = new TaskList(tasks, new ArrayList<>());
        Task toPin = taskList.getTaskById(requestModel.getTaskId());
        if (toPin == null) {
            presenter.prepareFailView("Task not found for pinning.");
            return;
        }

        toPin.setPriorityOverride(requestModel.getPinnedIndex());

        taskListDataAccessInterface.saveAllTasks(tasks);
        TaskListResponseModel responseModel = buildResponse(tasks);
        presenter.prepareSuccessView(responseModel);
    }

    @Override
    public void completeTask(TaskListCompleteTaskRequestModel requestModel) {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();

        TaskList taskList = new TaskList(tasks, new ArrayList<>());
        Task target = taskList.getTaskById(requestModel.getTaskId());
        if (target == null) {
            presenter.prepareFailView("Task not found for completion.");
            return;
        }

        target.setComplete(requestModel.isComplete());

        taskListDataAccessInterface.saveAllTasks(tasks);
        TaskListResponseModel responseModel = buildResponse(tasks);
        presenter.prepareSuccessView(responseModel);
    }

    /**
     * Build response with:
     *   - active tasks: not complete, sorted by TaskList rules
     *   - completed tasks: complete, sorted by completedAt
     */
    private TaskListResponseModel buildResponse(List<Task> allTasks) {
        List<Task> active = new ArrayList<>();
        List<Task> completed = new ArrayList<>();

        for (Task t : allTasks) {
            if (t.isComplete()) {
                completed.add(t);
            } else {
                active.add(t);
            }
        }

        TaskList activeList = new TaskList(active, new ArrayList<>());
        List<Task> sortedActive = activeList.getTasksSorted();

        completed.sort(Comparator.comparing(Task::getCompletedAt));

        return new TaskListResponseModel(sortedActive, completed);
    }
}
