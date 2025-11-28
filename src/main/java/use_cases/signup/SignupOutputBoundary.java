package use_cases.signup;

public interface SignupOutputBoundary {
    void prepareSuccessView(SignupOutputData user);
    void prepareFailView(String error);
}