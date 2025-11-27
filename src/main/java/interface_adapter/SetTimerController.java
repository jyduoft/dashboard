package interface_adapter;

import use_cases.SetTimerInputBoundary;
import use_cases.SetTimerInputData;

public class SetTimerController {
    private final SetTimerInputBoundary interactor;

    public SetTimerController(SetTimerInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String title, long dueMin, long warnMin) {
        SetTimerInputData data = new SetTimerInputData(title, dueMin, warnMin);
        interactor.execute(data);
    }
}
