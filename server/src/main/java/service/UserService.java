package service;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;
import model.results.RegisterResult;

import java.util.UUID;

import static util.ParameterValidation.validateString;

public class UserService {
    private final UserDao userDao;
    private final AuthTokenDao authDao;

    public UserService(UserDao userDao, AuthTokenDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    private static String generateAuthToken() {
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
        validateString(request.username(), DatabaseManager.maxStringLength);
        validateString(request.password(), DatabaseManager.maxStringLength);
        validateString(request.email(), DatabaseManager.maxStringLength);

        UserData userData = userDao.getUserData(request.username());
        if (userData != null) {
            throw new AlreadyTakenException("Error: Username " + request.username() + " is already taken");
        }

        userDao.createUser(request);

        AuthData authData = createAuth(request.username());

        return new RegisterResult(request.username(), authData.authToken());
    }

    /**
     * Creates an instance of AuthData in data storage
     *
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
     * Validates request and creates a new auth session for user
     *
     * @param request an instance of LoginRequest
     * @return an instance of AuthData
     * @throws BadRequestException   if any required fields are empty
     * @throws UnauthorizedException if username and password do not match
     * @throws DataAccessException   for internal data errors
     */
    public AuthData login(LoginRequest request)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        validateString(request.username());
        validateString(request.password());

        userDao.validateUser(request);

        return createAuth(request.username());
    }

    /**
     * Validates the given authToken and deletes the corresponding auth session
     *
     * @param authToken for the desired user
     * @throws UnauthorizedException if authToken is not recognized
     * @throws DataAccessException   for internal data errors
     */
    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        validateString(authToken);

        authDao.validateAuthData(authToken);

        authDao.deleteAuth(authToken);
    }
}
