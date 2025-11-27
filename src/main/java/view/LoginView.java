package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "log in";
    private final LoginViewModel loginViewModel;
    private final LoginController loginController;
    // We need this to switch screens when "Sign Up" is clicked
    private final ViewManagerModel viewManagerModel;

    final JTextField usernameInputField = new JTextField(15);
    final JPasswordField passwordInputField = new JPasswordField(15);
    final JButton logIn;
    final JButton signUp; // Changed from 'cancel' to 'signUp'

    // Updated Constructor to accept ViewManagerModel
    public LoginView(LoginViewModel loginViewModel, LoginController controller, ViewManagerModel viewManagerModel) {
        this.loginController = controller;
        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel; // Save it
        this.loginViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel("Login Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelTextPanel usernameInfo = new LabelTextPanel(
                new JLabel("Username"), usernameInputField);
        LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        JPanel buttons = new JPanel();
        logIn = new JButton("Log In");
        buttons.add(logIn);

        // Change Cancel to Sign Up
        signUp = new JButton("Sign Up");
        buttons.add(signUp);

        // 1. Log In Button Listener
        logIn.addActionListener(evt -> {
            if (evt.getSource().equals(logIn)) {
                LoginState currentState = loginViewModel.getState();
                loginController.execute(
                        currentState.getUsername(),
                        currentState.getPassword()
                );
            }
        });

        // 2. Sign Up Button Listener (Switches to Sign Up View)
        signUp.addActionListener(evt -> {
            // "sign up" must match the viewName in your SignupView class
            viewManagerModel.setActiveView("sign up");
            viewManagerModel.firePropertyChanged();
        });

        // 3. Username Key Listener (Fixed to use keyReleased for accuracy)
        usernameInputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                loginViewModel.setState(currentState);
            }
        });

        // 4. Password Key Listener
        passwordInputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                LoginState currentState = loginViewModel.getState();
                currentState.setPassword(String.valueOf(passwordInputField.getPassword()));
                loginViewModel.setState(currentState);
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(usernameInfo);
        this.add(passwordInfo);
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // No debug printing
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LoginState state = (LoginState) evt.getNewValue();
        if (state.getUsernameError() != null) {
            JOptionPane.showMessageDialog(this, state.getUsernameError());
        }
    }
}