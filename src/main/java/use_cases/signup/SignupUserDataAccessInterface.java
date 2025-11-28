package use_cases.signup;

import entity.User;

public interface SignupUserDataAccessInterface {
    boolean existsByName(String identifier);
    void save(User user);
}