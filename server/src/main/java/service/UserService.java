package service;

import dataaccess.AuthTokenDao;
import dataaccess.UserDao;
import dataaccess.UserNotFoundException;
import handlers.RegisterResult;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    UserDao userDao;
    AuthTokenDao authDao;

    public UserService(UserDao userDao, AuthTokenDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(UserData request) throws AlreadyTakenException {
        if (request.username() == null || request.username().isEmpty()) {
            throw new BadRequestException("Error: username is a required field");
        } else if (request.password() == null || request.password().isEmpty()) {
            throw new BadRequestException("Error: password is a required field");
        } else if (request.email() == null || request.email().isEmpty()) {
            throw new BadRequestException("Error: email is a required field");
        }

        UserData userData = userDao.getUserData(request.username());
        if (userData != null) {
            throw new AlreadyTakenException("Error: Username " + request.username() + " is already taken");
        }

        userDao.createUser(request);

        AuthData authData = new AuthData(UserService.generateAuthToken(), request.username());
        authDao.createAuth(authData);

        return new RegisterResult(request.username(), authData.authToken());
    }

    public UserData getUser(String username) throws UserNotFoundException, BadRequestException {
        UserData data = userDao.getUserData(username);
        if (data == null) {
            throw new UserNotFoundException(username);
        }

        return data;
    }
}
