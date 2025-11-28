package interface_adapter;

import use_cases.SetTimerInputBoundary;

public class SetTimerController {
    final SetTimerInputBoundary interactor;

    public SetTimerController(SetTimerInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String title, long dueMin, long warnMin) {
        interactor.execute(title, dueMin, warnMin);
    }
}