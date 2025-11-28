package use_cases;

public interface TaskListOutputBoundary {
    void prepareSuccessView(TaskListResponseModel responseModel);
    void prepareFailView(String message);
}
