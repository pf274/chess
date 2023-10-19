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
     * @throws DataAccessException if the user does not exist
     */
    public User getUser(String username) throws DataAccessException {
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
//        throw new DataAccessException("User with username '" + username + "' not found");
    }

    /**
     * Gets a user from the list of users, given the email.
     * @param email the email
     * @return the user
     * @throws DataAccessException if the user does not exist
     */
    public User getUserByEmail(String email) throws DataAccessException {
        for (User user : users) {
            if (user.email.equals(email)) {
                return user;
            }
        }
        throw new DataAccessException("User with email '" + email + "' not found");
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
                throw new DataAccessException("User with username '" + username + "' already exists");
            }
            if (u.email.equals(email)) {
                throw new DataAccessException("User with email '" + email + "' already exists");
            }
        }
        User user = new User(username, password, email);
        users.add(user);
    }

    /**
     * Updates a user in the list of users, given the username and the new user.
     * @param username the username
     * @param user the new user
     * @throws DataAccessException if the user does not exist
     */
    public void updateUser(String username, User user) throws DataAccessException {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).username.equals(username)) {
                users.set(i, user);
                return;
            }
        }
        throw new DataAccessException("User with username '" + username + "' not found");
    }

    /**
     * Deletes a user from the list of users, given the username.
     * @param username the username
     * @throws DataAccessException if the user does not exist
     */
    public void deleteUser(String username) throws DataAccessException {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).username.equals(username)) {
                users.remove(i);
                return;
            }
        }
        throw new DataAccessException("User with username " + username + " not found");
    }

    /**
     * Clears the list of users.
     * @throws DataAccessException if clearing the list of users fails
     */
    public void clear() throws DataAccessException {
        users.clear();
    }
}
