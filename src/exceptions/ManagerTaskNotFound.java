package exceptions;

public class ManagerTaskNotFound extends RuntimeException {
    public ManagerTaskNotFound(String message) {
        super(message);
    }
}
