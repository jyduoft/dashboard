package use_cases.login;

import data_access.UserDataAccessObject;
import entity.User;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginInteractorTest {

    // ============================================================
    // TEST LOGIN FEATURES
    // ============================================================

    @Test
    public void testLoginSuccess() throws Exception {
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        when(userDAO.checkPassword("tony", "123")).thenReturn(true);
        interactor.executeLogin("tony", "123");
        verify(presenter).prepareSuccessView("tony");
    }

    @Test
    public void testLoginFailure() throws Exception {
        // 1. Arrange
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        when(userDAO.checkPassword("tony", "wrongpass")).thenReturn(false);
        interactor.executeLogin("tony", "wrongpass");
        verify(presenter).prepareFailView(contains("Incorrect password"));
    }

    @Test
    public void testLoginDatabaseCrash() throws Exception {
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        when(userDAO.checkPassword(anyString(), anyString()))
                .thenThrow(new RuntimeException("Firebase is down"));
        interactor.executeLogin("tony", "123");
        verify(presenter).prepareFailView(contains("Database error"));
    }

    // ============================================================
    // TEST SIGNUP FEATURES
    // ============================================================

    @Test
    public void testSignupSuccess() throws Exception {
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        when(userDAO.userExists("newuser")).thenReturn(false);
        interactor.executeSignup("newuser", "password123");
        verify(userDAO).createUser(any(User.class));
        verify(presenter).prepareSuccessView("newuser");
    }

    @Test
    public void testSignupUserAlreadyExists() throws Exception {
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        when(userDAO.userExists("tony")).thenReturn(true);
        interactor.executeSignup("tony", "password123");
        verify(presenter).prepareFailView("User already exists.");
        verify(userDAO, never()).createUser(any(User.class));
    }

    @Test
    public void testSignupEmptyInputs() {
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        interactor.executeSignup("tony", "");
        verify(presenter).prepareFailView(contains("cannot be empty"));
    }

    @Test
    public void testSignupCrash() throws Exception {
        UserDataAccessObject userDAO = Mockito.mock(UserDataAccessObject.class);
        LoginOutputBoundary presenter = Mockito.mock(LoginOutputBoundary.class);
        LoginInteractor interactor = new LoginInteractor(userDAO, presenter);
        when(userDAO.userExists(anyString())).thenThrow(new RuntimeException("Network Error"));
        interactor.executeSignup("crashUser", "123");
        verify(presenter).prepareFailView(contains("Could not sign up"));
    }
}