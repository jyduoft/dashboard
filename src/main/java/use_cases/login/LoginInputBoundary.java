package use_cases.login;

public interface LoginInputBoundary {
    void executeLogin(String username, String password);
    void executeSignup(String username, String password);
}