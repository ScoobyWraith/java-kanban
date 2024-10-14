package exceptions;

public class TaskHasNotParameter extends RuntimeException {
    public TaskHasNotParameter(String message) {
        super(message);
    }
}
