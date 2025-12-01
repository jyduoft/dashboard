package use_cases.login;

public interface LoginOutputBoundary {
    void prepareSuccessView(String username);
    void prepareFailView(String error);
}