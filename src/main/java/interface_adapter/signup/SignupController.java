package interface_adapter.signup;

import use_cases.signup.SignupInputBoundary;
import use_cases.signup.SignupInputData;

public class SignupController {
    final SignupInputBoundary userSignupUseCaseInteractor;

    public SignupController(SignupInputBoundary userSignupUseCaseInteractor) {
        this.userSignupUseCaseInteractor = userSignupUseCaseInteractor;
    }

    public void execute(String username, String password, String repeatPassword) {
        SignupInputData signupInputData = new SignupInputData(username, password, repeatPassword);
        userSignupUseCaseInteractor.execute(signupInputData);
    }
}