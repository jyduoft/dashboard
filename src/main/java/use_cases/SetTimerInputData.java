package use_cases;

public class SetTimerInputData {
    private final String taskTitle;
    private final long dueInMinutes;
    private final long warnInMinutes;

    public SetTimerInputData(String taskTitle, long dueInMinutes, long warnInMinutes) {
        this.taskTitle = taskTitle;
        this.dueInMinutes = dueInMinutes;
        this.warnInMinutes = warnInMinutes;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public long getDueInMinutes() {
        return dueInMinutes;
    }

    public long getWarnInMinutes() {
        return warnInMinutes;
    }
}