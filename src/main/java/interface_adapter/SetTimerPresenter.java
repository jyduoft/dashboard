package interface_adapter;

import use_cases.SetTimerOutputBoundary;
import use_cases.SetTimerOutputData;
import javax.swing.JOptionPane;

public class SetTimerPresenter implements SetTimerOutputBoundary {
    @Override
    public void prepareSuccessView(SetTimerOutputData data) {
        JOptionPane.showMessageDialog(null, data.getMessage());
    }

    @Override
    public void prepareFailView(String error) {
        JOptionPane.showMessageDialog(null, "Error: " + error);
    }
}