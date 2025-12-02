package use_cases;

import entity.Task;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimerServiceTest {

    @Test
    public void testTimerLogicWithDueTask() {
        // 1. Arrange
        // We need a fake DAO just to create the service
        TaskDataAccessInterface dao = mock(TaskDataAccessInterface.class);
        TimerService service = new TimerService(dao);

        List<Task> tasks = new ArrayList<>();

        // Create a task using your Factory
        Task task = Task.TaskFactory.createTask("Urgent Homework");

        // Add a reminder set for 1 minute AGO (so it is definitely "due")
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(1);
        task.addReminder(pastTime);

        tasks.add(task);

        // 2. Act
        // We call the logic directly so we don't have to wait 5 seconds.
        // NOTE: A JOptionPane window will pop up. You must click OK!
        service.checkTasks(tasks);

        // 3. Assert
        // Verify the reminder was marked as "sent" inside the task object
        boolean markedSent = false;
        for (Task.Reminder r : task.getReminders()) {
            if (r.getTime().equals(pastTime) && r.isNotificationSent()) {
                markedSent = true;
                break;
            }
        }
        assertTrue("The reminder should be marked as sent after the check", markedSent);
    }

    @Test
    public void testTimerLogicIgnoresFutureTasks() {
        // 1. Arrange
        TaskDataAccessInterface dao = mock(TaskDataAccessInterface.class);
        TimerService service = new TimerService(dao);

        List<Task> tasks = new ArrayList<>();

        Task task = Task.TaskFactory.createTask("Future Task");
        // Reminder is in the future (1 hour from now)
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        task.addReminder(futureTime);

        tasks.add(task);

        // 2. Act
        service.checkTasks(tasks);

        // 3. Assert
        // Ensure it was NOT sent
        boolean markedSent = false;
        for (Task.Reminder r : task.getReminders()) {
            if (r.isNotificationSent()) {
                markedSent = true;
                break;
            }
        }
        assertFalse("Future reminders should NOT be marked sent", markedSent);
    }

    @Test
    public void testTimerLogicWithEmptyList() {
        // 1. Arrange
        TaskDataAccessInterface dao = mock(TaskDataAccessInterface.class);
        TimerService service = new TimerService(dao);

        // 2. Act
        // Pass empty list or null to ensure no crash
        service.checkTasks(new ArrayList<>());
        service.checkTasks(null);

        // 3. Assert
        // If we reached here without an exception, the test passes.
        assertTrue(true);
    }

    @Test
    public void testStartTimerCoverage() {
        // This test hits the startTimer method line to ensure coverage.
        // We mock the DAO to return an empty list so the background thread is happy.
        TaskDataAccessInterface dao = mock(TaskDataAccessInterface.class);
        when(dao.getAllTasks()).thenReturn(Collections.emptyList());

        TimerService service = new TimerService(dao);
        service.startTimer();
    }
}