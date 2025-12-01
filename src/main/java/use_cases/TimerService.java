package use_cases;

import entity.Task;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerService {
    public void startTimer(List<Task> tasks) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Check every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();

            for (Task task : tasks) {
                // FIX 1: Method name is 'isComplete()', not 'isCompleted()'
                if (task.isComplete()) continue;

                List<Task.Reminder> reminders = task.getReminders();
                if (reminders == null || reminders.isEmpty()) {
                    continue;
                }

                for (Task.Reminder reminder : reminders) {
                    if (!reminder.isNotificationSent()
                            && (now.isAfter(reminder.getTime()) || now.isEqual(reminder.getTime()))) {

                        SwingUtilities.invokeLater(() ->
                                // FIX 4: Method name is 'getTaskName()', not 'getTitle()'
                                JOptionPane.showMessageDialog(null,
                                        "⏰ REMINDER: " + task.getTaskName() + " is due soon!")
                        );

                        // Mark this specific reminder as sent
                        reminder.markSent();

                        // Break if you only want one popup per task per tick (optional)
                        break;
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}