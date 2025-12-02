package use_cases.login;

import data_access.UserDataAccessObject;
import entity.User;

public class LoginInteractor implements LoginInputBoundary {
    final UserDataAccessObject userDataAccessObject;
    final LoginOutputBoundary loginPresenter;

    public LoginInteractor(UserDataAccessObject userDataAccessObject,
                           LoginOutputBoundary loginPresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void executeLogin(String username, String password) {
        try {
            if (userDataAccessObject.checkPassword(username, password)) {
                loginPresenter.prepareSuccessView(username);
            } else {
                loginPresenter.prepareFailView("Incorrect password or user does not exist.");
            }
        } catch (Exception e) {
            loginPresenter.prepareFailView("Database error: " + e.getMessage());
        }
    }

    @Override
    public void executeSignup(String username, String password) {
        // Input validation checks are safe to keep outside (no DB access here)
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            loginPresenter.prepareFailView("Username and Password cannot be empty.");
            return;
        }

        // FIX: The try block now starts HERE
        try {
            // Now, if the DB is down during this check, the catch block will handle it
            if (userDataAccessObject.userExists(username)) {
                loginPresenter.prepareFailView("User already exists.");
                return;
            }

            entity.User newUser = new entity.User(username, password);
            userDataAccessObject.createUser(newUser);
            loginPresenter.prepareSuccessView(username);

        } catch (Exception e) {
            // This now catches errors from BOTH userExists() and createUser()
            loginPresenter.prepareFailView("Could not sign up: " + e.getMessage());
        }
    }
}