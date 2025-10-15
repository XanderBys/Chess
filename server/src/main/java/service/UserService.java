package service;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import dataaccess.UserNotFoundException;
import handlers.LoginRequest;
import handlers.RegisterResult;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDao userDao;
    private final AuthTokenDao authDao;

    public UserService(UserDao userDao, AuthTokenDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Validates request parameters and creates a new user in the DAO.
     *
     * @param request instance of UserData
     * @return an instance of AuthData
     * @throws AlreadyTakenException if username is already taken
     * @throws BadRequestException   if any of the strings in request are invalid
     * @throws DataAccessException   for internal data errors
     */
    public RegisterResult register(UserData request)
            throws AlreadyTakenException, BadRequestException, DataAccessException {
        validateString(request.username());
        validateString(request.password());
        validateString(request.email());

        UserData userData = userDao.getUserData(request.username());
        if (userData != null) {
            throw new AlreadyTakenException("Error: Username " + request.username() + " is already taken");
        }

        userDao.createUser(request);

        AuthData authData = createAuth(request.username());

        return new RegisterResult(request.username(), authData.authToken());
    }

    private void validateString(Object o) throws BadRequestException {
        if (o == null || !o.getClass().equals(String.class) || ((String) o).isEmpty()) {
            throw new BadRequestException("Error: non-empty string expected");
        }
    }

    /**
     * Creates an instance of AuthData in data storage
     * @param username the username of the user whose auth session is starting
     * @return the created instance of AuthData
     * @throws DataAccessException for internal data errors
     */
    private AuthData createAuth(String username) throws DataAccessException {
        AuthData authData = new AuthData(username, UserService.generateAuthToken());
        authDao.createAuth(authData);

        return authData;
    }

    /**
     * Get the stored data about a user
     * @param username of the user whose data to retrieve
     * @return an instance of UserData
     * @throws UserNotFoundException if username is not in the database
     * @throws DataAccessException for internal data errors
     */
    public UserData getUser(String username) throws UserNotFoundException, DataAccessException {
        UserData data = userDao.getUserData(username);
        if (data == null) {
            throw new UserNotFoundException(username);
        }

        return data;
    }

    /**
     * Validates request and creates a new auth session for user
     * @param request an instance of LoginRequest
     * @return an instance of AuthData
     * @throws BadRequestException if any required fields are empty
     * @throws UnauthorizedException if username and password do not match
     * @throws DataAccessException for internal data errors
     */
    public AuthData login(LoginRequest request)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        validateString(request.username());
        validateString(request.password());

        validateLoginData(request);

        return createAuth(request.username());
    }

    /**
     * Checks that user is registered and that username and password match
     * @param userToValidate an instance of LoginRequest containing username and password
     * @throws UnauthorizedException if the password doesn't match username or if username is null
     * @throws DataAccessException for internal data errors
     */
    private void validateLoginData(LoginRequest userToValidate) throws UnauthorizedException, DataAccessException {
        UserData storedUserData = userDao.getUserData(userToValidate.username());

        if (storedUserData == null || !storedUserData.password().equals(userToValidate.password())) {
            throw new UnauthorizedException("");
        }
    }

    /**
     * Validates the given authToken and deletes the corresponding auth session
     * @param authToken for the desired user
     * @throws UnauthorizedException if authToken is not recognized
     * @throws DataAccessException for internal data errors
     */
    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        validateString(authToken);

        validateAuthData(authToken);

        authDao.deleteAuth(authToken);
    }

    /**
     * Checks whether authToken is in the database
     * @param authToken authToken to validate
     * @throws UnauthorizedException if authToken not recognized
     * @throws DataAccessException for internal data errors
     */
    public void validateAuthData(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("");
        }
    }
}
