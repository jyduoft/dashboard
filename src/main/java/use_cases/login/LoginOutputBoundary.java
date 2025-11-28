package use_cases.login;

public interface LoginOutputBoundary {
    void prepareSuccessView(LoginOutputData user);
    void prepareFailView(String error);
}