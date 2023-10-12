package server.Models;

/**
 * Represents a user
 */
public class User {

    /**
     * The user's color
     */
    public enum Color {
        BLACK,
        WHITE
    }

    /**
     * The user's username
     */
    public String username;

    /**
     * The user's password
     */
    public String password;

    /**
     * The user's email
     */
    public String email;

    /**
     * Creates a new user
     * @param username the user's username
     * @param password the user's password
     * @param email the user's email
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
