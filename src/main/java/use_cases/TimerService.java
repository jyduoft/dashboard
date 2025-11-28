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
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            for (Task task : tasks) {
                // Alt Flow: Task complete -> No notification [cite: 49]
                if (task.isComplete()) continue;

                List<Task.Reminder> reminders = task.getReminders();
                if (reminders == null || reminders.isEmpty()) {
                    continue;
                }

                for (Task.Reminder reminder : reminders) {
                    // Only care about unsent reminders whose time has passed
                    if (!reminder.isNotificationSent()
                            && (now.isAfter(reminder.getTime()) || now.isEqual(reminder.getTime()))) {

                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(
                                        null,
                                        "⏰ Due soon: " + task.getTaskName()
                                )
                        );

                        // mark this reminder as sent so we don't spam
                        reminder.markSent();

                        // break if you only want one popup per task per tick
                        break;
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
