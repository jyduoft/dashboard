package interface_adapter.signup;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import use_cases.signup.SignupOutputBoundary;
import use_cases.signup.SignupOutputData;

public class SignupPresenter implements SignupOutputBoundary {

    private final SignupViewModel signupViewModel;
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    public SignupPresenter(ViewManagerModel viewManagerModel,
                           SignupViewModel signupViewModel,
                           LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.signupViewModel = signupViewModel;
        this.loginViewModel = loginViewModel;
    }

    // This constructor matches your current Main.java if you haven't updated it yet,
    // but the one above is better. I will include this one to prevent errors:
    public SignupPresenter(SignupViewModel signupViewModel, ViewManagerModel viewManagerModel) {
        this.signupViewModel = signupViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loginViewModel = null; // Optional if you don't want to autofill
    }

    @Override
    public void prepareSuccessView(SignupOutputData response) {
        // On Success, switch to the Login View
        // (Optional) You could also auto-fill the username in the login field here if you had the LoginViewModel

        System.out.println("Signup Successful: " + response.getUsername());

        // THIS IS THE KEY LINE:
        viewManagerModel.setActiveView("log in");
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        SignupState signupState = signupViewModel.getState();
        signupState.setUsernameError(error);
        signupViewModel.firePropertyChanged();
    }
}