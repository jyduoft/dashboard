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
    private final List<LocalDateTime> remindDates;
    private int priorityOverride; // -1 = no pin, >=0 = pinned index
    private boolean isComplete;
    private LocalDateTime completedAt;

    private Task(String id, String taskName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Task id cannot be null or blank");
        }
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or blank");
        }
        this.id = id;
        this.taskName = taskName;
        this.category = Category.UNSORTED;
        this.remindDates = new ArrayList<>();
        this.priorityOverride = -1;
        this.isComplete = false;
    }
    private Task(String id, String taskName, Category category) {
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

    public List<LocalDateTime> getRemindDates() {
        return new ArrayList<>(remindDates);
    }

    public void addRemindDate(LocalDateTime dateTime) {
        if (dateTime != null) {
            remindDates.add(dateTime);
        }
    }

    public void removeRemindDate(LocalDateTime dateTime) {
        remindDates.remove(dateTime);
    }

    public void addRemindDates(List<LocalDateTime> dateTimes) {
        if (dateTimes == null) return;

        for (LocalDateTime dt : dateTimes) {
            if (dt != null) {
                if (dt.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Reminder cannot be in the past.");
                }
                remindDates.add(dt);
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

    public static class TaskFactory {
        private static long nextId = 1;

        public static Task createTask(String taskName) {
            String id = "task-" + nextId;
            nextId++;
            return new Task(id, taskName);
        }

        public static Task createTask(String taskName, Category category) {
            String id = "task-" + nextId;
            nextId++;
            return new Task(id, taskName, category);
        }
    }
}
