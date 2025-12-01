package interface_adapter;

import use_cases.login.LoginOutputBoundary;
import view.ViewManagerModel;

public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(String username) {
        // Switch to the Dashboard View
        viewManagerModel.setActiveView("Dashboard");
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        loginViewModel.setError(error);
    }
}
