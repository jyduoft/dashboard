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
        List<Category> categories = taskListDataAccessInterface.getAllCategories();

        TaskListResponseModel responseModel = buildResponse(tasks, categories);
        presenter.prepareSuccessView(responseModel);
    }

    @Override
    public void addTask(TaskListAddTaskRequestModel requestModel) {
        try {
            List<Task> tasks = taskListDataAccessInterface.getAllTasks();
            List<Category> categories = taskListDataAccessInterface.getAllCategories();

            Category category = Category.UNSORTED;
            Task newTask = Task.TaskFactory.createTask(requestModel.getTaskName(), category);
            tasks.add(newTask);

            taskListDataAccessInterface.saveAllTasks(tasks);

            TaskListResponseModel responseModel = buildResponse(tasks, categories);
            presenter.prepareSuccessView(responseModel);
        } catch (Exception e) {
            presenter.prepareFailView("Could not add task: " + e.getMessage());
        }
    }

    @Override
    public void pinTask(TaskListPinTaskRequestModel requestModel) {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();
        List<Category> categories = taskListDataAccessInterface.getAllCategories();

        TaskList taskList = new TaskList(tasks, new ArrayList<Task>());
        Task toPin = taskList.getTaskById(requestModel.getTaskId());
        if (toPin == null) {
            presenter.prepareFailView("Task not found for pinning.");
            return;
        }

        toPin.setPriorityOverride(requestModel.getPinnedIndex());

        taskListDataAccessInterface.saveAllTasks(tasks);
        TaskListResponseModel responseModel = buildResponse(tasks, categories);
        presenter.prepareSuccessView(responseModel);
    }

    @Override
    public void completeTask(TaskListCompleteTaskRequestModel requestModel) {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();
        List<Category> categories = taskListDataAccessInterface.getAllCategories();

        TaskList taskList = new TaskList(tasks, new ArrayList<Task>());
        Task target = taskList.getTaskById(requestModel.getTaskId());
        if (target == null) {
            presenter.prepareFailView("Task not found for completion.");
            return;
        }

        target.setComplete(requestModel.isComplete());

        taskListDataAccessInterface.saveAllTasks(tasks);
        TaskListResponseModel responseModel = buildResponse(tasks, categories);
        presenter.prepareSuccessView(responseModel);
    }

    @Override
    public void changeCategory(TaskListChangeCategoryRequestModel requestModel) {

    }

    @Override
    public void viewCategories() {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();
        List<Category> categories = taskListDataAccessInterface.getAllCategories();
        TaskListResponseModel response = buildResponse(tasks, categories);
        presenter.prepareSuccessView(response);
    }

    @Override
    public void addOrUpdateCategory(TaskListAddOrUpdateCategoryRequestModel requestModel) {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();
        List<Category> categories = taskListDataAccessInterface.getAllCategories();

        // find existing category by name, or add new
        String name = requestModel.getCategoryName().trim();
        int priority = requestModel.getPriority();

        Category existing = null;
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(name)) {
                existing = c;
                break;
            }
        }

        if (existing != null) {
            // replace with new Category instance with updated priority
            categories.remove(existing);
        }
        categories.add(new Category(name, priority));

        taskListDataAccessInterface.saveAllCategories(categories);

        TaskListResponseModel response = buildResponse(tasks, categories);
        presenter.prepareSuccessView(response);
    }

    @Override
    public void deleteCategory(TaskListDeleteCategoryRequestModel requestModel) {
        List<Task> tasks = taskListDataAccessInterface.getAllTasks();
        List<Category> categories = taskListDataAccessInterface.getAllCategories();

        String name = requestModel.getCategoryName().trim();

        // never delete UNSORTED
        if (Category.UNSORTED.getName().equalsIgnoreCase(name)) {
            presenter.prepareFailView("Cannot delete the UNSORTED category.");
            return;
        }

        // remove category
        categories.removeIf(c -> c.getName().equalsIgnoreCase(name));

        // reassign tasks using that category → UNSORTED
        for (Task t : tasks) {
            if (t.getCategory() != null &&
                    t.getCategory().getName().equalsIgnoreCase(name)) {
                t.setCategory(Category.UNSORTED);
            }
        }

        taskListDataAccessInterface.saveAllCategories(categories);
        taskListDataAccessInterface.saveAllTasks(tasks);

        TaskListResponseModel response = buildResponse(tasks, categories);
        presenter.prepareSuccessView(response);
    }


    /**
     * Build response with:
     *   - active tasks: not complete, sorted by TaskList rules
     *   - completed tasks: complete, sorted by completedAt
     *   - categories: unchanged from data access
     */
    private TaskListResponseModel buildResponse(List<Task> allTasks, List<Category> categories) {
        List<Task> active = new ArrayList<Task>();
        List<Task> completed = new ArrayList<Task>();

        for (Task t : allTasks) {
            if (t.isComplete()) {
                completed.add(t);
            } else {
                active.add(t);
            }
        }

        TaskList activeList = new TaskList(active, new ArrayList<String>());
        List<Task> sortedActive = activeList.getTasksSorted();

        // Sort completed tasks by completedAt
        completed.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                if (t1.getCompletedAt() == null && t2.getCompletedAt() == null) return 0;
                if (t1.getCompletedAt() == null) return 1;
                if (t2.getCompletedAt() == null) return -1;
                return t1.getCompletedAt().compareTo(t2.getCompletedAt());
            }
        });

        List<Category> categoriesCopy = new ArrayList<Category>(categories);

        return new TaskListResponseModel(sortedActive, completed, categoriesCopy);
    }
}
