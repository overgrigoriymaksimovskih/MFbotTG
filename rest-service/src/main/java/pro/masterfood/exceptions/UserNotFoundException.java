package pro.masterfood.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
}
