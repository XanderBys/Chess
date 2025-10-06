package handlers;

import model.AuthData;

public record RegisterResult(String username, AuthData authData) {
}
