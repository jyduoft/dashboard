package use_cases;

public class SetTimerOutputData {
    private final String message;

    public SetTimerOutputData(String message) {
        this.message = message;
    }
    public String getMessage() { return message; }
}