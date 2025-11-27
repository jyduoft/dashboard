package use_cases;

public interface SetTimerOutputBoundary {
    void prepareSuccessView(String message);
    void prepareFailView(String error);
}