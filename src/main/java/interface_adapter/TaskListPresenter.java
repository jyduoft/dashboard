package interface_adapter;

import use_cases.TaskListOutputBoundary;
import use_cases.TaskListResponseModel;

public class TaskListPresenter implements TaskListOutputBoundary {

    private final TaskListViewModel viewModel;

    public TaskListPresenter(TaskListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(TaskListResponseModel responseModel) {
        viewModel.setTasks(
                responseModel.getActiveTasks(),
                responseModel.getCompletedTasks()
        );

        viewModel.setCategories(responseModel.getCategories());
    }

    @Override
    public void prepareFailView(String message) {
        viewModel.setErrorMessage(message);
    }
}
