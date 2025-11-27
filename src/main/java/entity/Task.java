package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private boolean isCompleted;

    // --- TIMER FIELDS ---
    private LocalDateTime dueDate;
    private List<LocalDateTime> remindDates;
    private boolean notificationSent;

    public Task(String title) {
        this.title = title;
        this.isCompleted = false;
        this.notificationSent = false;
        this.remindDates = new ArrayList<>();
    }

    // --- GETTERS & SETTERS ---
    public String getTitle() { return title; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        this.notificationSent = false; // Reset if date changes [cite: 51]
    }

    public List<LocalDateTime> getRemindDates() { return remindDates; }
    public void setRemindDates(List<LocalDateTime> remindDates) { this.remindDates = remindDates; }

    public boolean isNotificationSent() { return notificationSent; }
    public void setNotificationSent(boolean sent) { this.notificationSent = sent; }
}