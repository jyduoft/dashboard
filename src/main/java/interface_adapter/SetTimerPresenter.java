package interface_adapter;

import use_cases.SetTimerOutputBoundary;
import javax.swing.JOptionPane;

public class SetTimerPresenter implements SetTimerOutputBoundary {
    @Override
    public void prepareSuccessView(String message) {
        System.out.println("Success: " + message);
    }

    @Override
    public void prepareFailView(String error) {
        JOptionPane.showMessageDialog(null, "Error: " + error);
    }
}