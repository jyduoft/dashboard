package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task {

    private final String id;
    private String taskName;
    private Category category;
    private LocalDateTime dueDate;
    private final List<Reminder> reminders;
    private int priorityOverride; // -1 = no pin, >=0 = pinned index
    private boolean isComplete;
    private LocalDateTime completedAt;
    private boolean notificationSent;

    public Task(String id, String taskName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Task id cannot be null or blank");
        }
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or blank");
        }
        this.id = id;
        this.taskName = taskName;
        this.category = Category.UNSORTED;
        this.reminders = new ArrayList<>();
        this.priorityOverride = -1;
        this.isComplete = false;
    }
    public Task(String id, String taskName, Category category) {
        this(id, taskName);
        setCategory(category);
    }

    public String getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or blank");
        }
        this.taskName = taskName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = Objects.requireNonNullElse(category, Category.UNSORTED);
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<Reminder> getReminders() {
        return new ArrayList<>(reminders);
    }

    public void addReminder(LocalDateTime time) {
        if (time != null) {
            reminders.add(new Reminder(time));
        }
    }

    public void removeReminder(LocalDateTime time) {
        if (time == null) return;
        reminders.removeIf(r -> r.getTime().equals(time));
    }

    public void addReminders(List<LocalDateTime> times) {
        if (times == null) return;

        for (LocalDateTime time : times) {
            if (time != null) {
                reminders.add(new Reminder(time));
            }
        }
    }


    public void markReminderSent(LocalDateTime time) {
        for (Reminder r : reminders) {
            if (r.getTime().equals(time)) {
                r.markSent();
                break;
            }
        }
    }

    public int getPriorityOverride() {
        return priorityOverride;
    }

    public void setPriorityOverride(int priorityOverride) {
        this.priorityOverride = priorityOverride;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
        if (complete) {
            this.completedAt = LocalDateTime.now();
        } else {
            this.completedAt = null;
        }
    }

    public void toggleComplete() {
        isComplete = !isComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", taskName='" + taskName + '\'' +
                ", category=" + category +
                ", dueDate=" + dueDate +
                ", priorityOverride=" + priorityOverride +
                ", isComplete=" + isComplete +
                '}';
    }

    public void setNotificationSent(boolean b) { // not entirely sure what this is for. leaving it here for now
        notificationSent = b;
    }

    public static class TaskFactory {

        // 1. Create a NEW task (Generates a random unique ID)
        public static Task createTask(String taskName) {
            String uniqueId = java.util.UUID.randomUUID().toString();
            return new Task(uniqueId, taskName);
        }
        public static Task createTask(String taskName, Category category) {
            String uniqueId = java.util.UUID.randomUUID().toString();
            return new Task(uniqueId, taskName, category);
        }
        public static Task reconstituteTask(String id, String taskName, Category category) {
            return new Task(id, taskName, category);
        }
    }

    public static class Reminder {
        private final LocalDateTime time;
        private boolean isNotificationSent;

        public Reminder(LocalDateTime time) {
            if (time == null) {
                throw new IllegalArgumentException("Reminder time cannot be null");
            }
            this.time = time;
            this.isNotificationSent = false;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public boolean isNotificationSent() {
            return isNotificationSent;
        }

        public void markSent() {
            this.isNotificationSent = true;
        }
    }
}
