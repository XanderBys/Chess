package dataaccess;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User with name " + username + " not found.");
    }
}
