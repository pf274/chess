package server.DAO;

import server.Models.User;
import java.util.ArrayList;

/**
 * The UserDAO class is responsible for storing and retrieving User objects from the database
 */
public class UserDAO implements DAO {

    /**
     * The list of users.
     */
    private final ArrayList<User> users = new ArrayList<>();

    /**
     * Gets a user from the list of users, given the username.
     * @param username the username
     * @return the user
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Adds a user to the list of users.
     * @param username the username
     * @param password the password
     * @param email the email
     * @throws DataAccessException if the user already exists
     */
    public void addUser(String username, String password, String email) throws DataAccessException {
        for (User u : users) {
            if (u.username.equals(username)) {
                throw new DataAccessException(500, "User with username '" + username + "' already exists");
            }
            if (u.email.equals(email)) {
                throw new DataAccessException(500, "User with email '" + email + "' already exists");
            }
        }
        User user = new User(username, password, email);
        users.add(user);
    }

    /**
     * Clears the list of users.
     * @throws DataAccessException if clearing the list of users fails
     */
    public void clear() throws DataAccessException {
        users.clear();
    }
}
