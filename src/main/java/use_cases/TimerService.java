package use_cases;

import entity.Task;
import javax.swing.*;
import java.awt.*;
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
                if (task.isCompleted()) continue;

                if (task.getRemindDates() != null && !task.getRemindDates().isEmpty()) {
                    LocalDateTime remindTime = task.getRemindDates().get(0);

                    if (now.isAfter(remindTime) && !task.isNotificationSent()) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null, "⏰ Due soon: " + task.getTitle())
                        );
                        task.setNotificationSent(true);
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
