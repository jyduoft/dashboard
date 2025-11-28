package use_cases;

public interface SetTimerOutputBoundary {
    void prepareSuccessView(SetTimerOutputData outputData);
    void prepareFailView(String error);
}