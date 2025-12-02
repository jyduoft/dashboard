package use_cases;

import entity.Task;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerService {

    // CHANGE 1: We hold the DAO, not a List
    private final TaskDataAccessInterface dataAccess;

    public TimerService(TaskDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void startTimer() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            // CHANGE 2: Ask for the FRESH list every time the loop runs
            List<Task> tasks = dataAccess.getAllTasks();

            checkTasks(tasks);
        }, 0, 5, TimeUnit.SECONDS);
    }

    // This logic stays the same, just extracted for cleanliness
    void checkTasks(List<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        if (tasks == null) return;

        for (Task task : tasks) {
            if (task.isComplete()) continue;

            List<Task.Reminder> reminders = task.getReminders();
            if (reminders == null || reminders.isEmpty()) continue;

            for (Task.Reminder reminder : reminders) {
                if (!reminder.isNotificationSent()
                        && (now.isAfter(reminder.getTime()) || now.isEqual(reminder.getTime()))) {

                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(null,
                                    "⏰ REMINDER: " + task.getTaskName() + " is due soon!")
                    );
                    reminder.markSent();
                    break;
                }
            }
        }
    }
}