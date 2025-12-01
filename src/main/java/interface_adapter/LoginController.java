package interface_adapter;

import use_cases.login.LoginInputBoundary;

public class LoginController {
    final LoginInputBoundary loginUseCaseInteractor;

    public LoginController(LoginInputBoundary loginUseCaseInteractor) {
        this.loginUseCaseInteractor = loginUseCaseInteractor;
    }

    public void login(String username, String password) {
        loginUseCaseInteractor.executeLogin(username, password);
    }

    public void signup(String username, String password) {
        loginUseCaseInteractor.executeSignup(username, password);
    }
}