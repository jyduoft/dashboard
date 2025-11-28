package interface_adapter.login;

import use_cases.login.LoginOutputBoundary;
import use_cases.login.LoginOutputData;
import javax.swing.JOptionPane;
import interface_adapter.ViewManagerModel;

public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel; // <--- Add this

    public LoginPresenter(LoginViewModel loginViewModel, ViewManagerModel viewManagerModel) {
        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData response) {
        // On success, switch to the Main Dashboard!
        // Make sure your MainDashboardView has: public final String viewName = "dashboard";
        viewManagerModel.setActiveView("dashboard");
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        LoginState loginState = loginViewModel.getState();
        loginState.setUsernameError(error);
        loginViewModel.firePropertyChanged();
    }
}
