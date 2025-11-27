package use_cases;

public interface SetTimerInputBoundary {
    void execute(String taskTitle, long dueMinutes, long warnMinutes);
}