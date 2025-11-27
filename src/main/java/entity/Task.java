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
    private final ArrayList<LocalDateTime> remindDates;
    private int priorityOverride;
    private boolean isComplete;

    public Task(String id, String taskName) {
        this.id = id;
        this.taskName = taskName;
        this.category = Category.UNSORTED;
        this.dueDate = null;
        this.remindDates = new ArrayList<LocalDateTime>();
        this.priorityOverride = -1;
        this.isComplete = false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name=" + taskName +
                '}';
    }

    public String getId() {return id;}

    public String getTaskName() {return taskName;}
    public void setTaskName(String taskName) {this.taskName = taskName;}

    public Category getCategory() {return this.category;}
    public void setCategory(Category category) {
        this.category = Objects.requireNonNullElse(category, Category.UNSORTED);
    }

    public LocalDateTime getDueDate() {return this.dueDate;}
    public void setDueDate(LocalDateTime dueDate) {this.dueDate = dueDate;}

    public ArrayList<LocalDateTime> getRemindDates() {return this.remindDates;}
    public void addRemindDate(LocalDateTime remindDate) {this.remindDates.add(remindDate);}
    public void removeRemindDate(LocalDateTime remindDate) {this.remindDates.remove(remindDate);}

    public int getPriorityOverride() {return this.priorityOverride;}
    public void setPriorityOverride(int priorityOverride) {this.priorityOverride = priorityOverride;}

    public boolean isComplete() {return this.isComplete;}
    public void setComplete(boolean isComplete) {this.isComplete = isComplete;}

//    public void setRemindDates(List<LocalDateTime> reminders) {
//    }
//
//    public void setNotificationSent(boolean b) {
//    }
//
//    public Object getTitle() {
//    }

    public static class TaskFactory {
        private static long nextId = 0;

        public static Task createTask(String taskName) {
            String id = "task-" + (++nextId);
            return new Task(id, taskName);
        }
    }

}
