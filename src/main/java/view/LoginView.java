package view;

import interface_adapter.LoginController;
import interface_adapter.LoginViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "log in";
    private final LoginViewModel loginViewModel;
    private final LoginController loginController;

    private final JTextField usernameInputField = new JTextField(15);
    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JButton logIn = new JButton("Log In");
    private final JButton signUp = new JButton("Sign Up");

    public LoginView(LoginViewModel loginViewModel, LoginController controller) {
        this.loginViewModel = loginViewModel;
        this.loginController = controller;
        this.loginViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel("Login Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelTextPanel usernameInfo = new LabelTextPanel(
                new JLabel("Username"), usernameInputField);
        LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        JPanel buttons = new JPanel();
        buttons.add(logIn);
        buttons.add(signUp);

        logIn.addActionListener(evt -> {
            if (evt.getSource().equals(logIn)) {
                loginController.login(usernameInputField.getText(),
                        String.valueOf(passwordInputField.getPassword()));
            }
        });

        signUp.addActionListener(evt -> {
            loginController.signup(usernameInputField.getText(),
                    String.valueOf(passwordInputField.getPassword()));
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(usernameInfo);
        this.add(passwordInfo);
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("error".equals(evt.getPropertyName())) {
            String error = (String) evt.getNewValue();
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
            }
        }
    }

    // Helper class for labeling text fields
    static class LabelTextPanel extends JPanel {
        LabelTextPanel(JLabel label, JTextField textField) {
            this.add(label);
            this.add(textField);
        }
    }
}