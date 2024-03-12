package account.exception;

public class PasswordIsSameAsOldException extends RuntimeException {
    public PasswordIsSameAsOldException(String message) {
        super(message);
    }
}
