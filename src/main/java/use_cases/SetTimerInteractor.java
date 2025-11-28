package use_cases;

import entity.Task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SetTimerInteractor implements SetTimerInputBoundary {
    private final TaskDataAccessInterface dataAccess;
    private final SetTimerOutputBoundary presenter;

    public SetTimerInteractor(TaskDataAccessInterface dataAccess, SetTimerOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SetTimerInputData inputData) {
        Task task = dataAccess.getTask(inputData.getTaskTitle());
        if (task == null) {
            presenter.prepareFailView("Task not found.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = now.plusMinutes(inputData.getDueInMinutes());
        LocalDateTime remind = due.minusMinutes(inputData.getWarnInMinutes());

        task.setDueDate(due);
        List<LocalDateTime> reminders = new ArrayList<>();
        reminders.add(remind);
        task.addReminders(reminders);

        task.setNotificationSent(false);

        dataAccess.saveTask(task);

        SetTimerOutputData output = new SetTimerOutputData("Timer set for: " + task.getTaskName());
        presenter.prepareSuccessView(output);
    }
}