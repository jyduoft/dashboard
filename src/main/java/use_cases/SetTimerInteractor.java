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
    public void execute(String taskTitle, long dueMinutes, long warnMinutes) {
        Task task = dataAccess.getTask(taskTitle);
        if (task == null) {
            presenter.prepareFailView("Task not found: " + taskTitle);
            return;
        }

        // Calculate Times
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = now.plusMinutes(dueMinutes);
        LocalDateTime remind = due.minusMinutes(warnMinutes);

        // Update Entity
        task.setDueDate(due);
        List<LocalDateTime> reminders = new ArrayList<>();
        reminders.add(remind);
        task.setRemindDates(reminders);

        task.setNotificationSent(false); // Reset notification status

        // Save
        dataAccess.saveTask(task);
        presenter.prepareSuccessView("Timer set for: " + taskTitle);
    }
}