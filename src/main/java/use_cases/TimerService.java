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
                // Skip completed tasks [cite: 49]
                if (task.isCompleted()) continue;

                if (task.getRemindDates() != null && !task.getRemindDates().isEmpty()) {
                    LocalDateTime remindTime = task.getRemindDates().get(0);

                    // Trigger notification if time is up and not sent yet
                    if (now.isAfter(remindTime) && !task.isNotificationSent()) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null,
                                        "⏰ REMINDER: " + task.getTitle() + " is due soon!")
                        );
                        task.setNotificationSent(true);
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}