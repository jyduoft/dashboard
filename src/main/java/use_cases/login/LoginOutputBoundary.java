package use_cases.login;

public interface LoginOutputBoundary {
    void prepareSuccessView(String username, String password);
    void prepareFailView(String error);
}