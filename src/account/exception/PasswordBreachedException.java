package account.exception;

public class PasswordBreachedException extends RuntimeException {
    public PasswordBreachedException(String message) {
        super(message);
    }
}
