package util;

import chess.ChessGame;
import service.BadRequestException;

public class ParameterValidation {
    /**
     * Checks for a non-empty string
     *
     * @param s the String to validate
     * @throws BadRequestException if s is null or empty
     */
    public static void validateString(String s) throws BadRequestException {
        if (s == null || s.isEmpty()) {
            throw new BadRequestException("Error: non-empty string expected");
        }
    }

    public static void validateTeamColor(ChessGame.TeamColor color) {
        if (color == null) {
            throw new BadRequestException("Error: Team Color must be non-null");
        }
    }
}
