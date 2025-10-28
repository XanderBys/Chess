package util;

import chess.ChessGame;
import service.BadRequestException;

public class ParameterValidation {
    /**
     * Checks for a non-empty string
     *
     * @param s         the String to validate
     * @param maxLength the maximum length string that should be accepted. If maxLength < 0, strings of all lengths
     *                  will be accepted.
     * @throws BadRequestException if s is null or empty
     */
    public static void validateString(String s, int maxLength) throws BadRequestException {
        if (s == null || s.isEmpty()) {
            throw new BadRequestException("Error: non-empty string expected");
        } else if (maxLength >= 0 && s.length() > maxLength) {
            throw new BadRequestException("Error: string exceeds maximum length");
        }
    }

    public static void validateString(String s) {
        validateString(s, -1);
    }

    public static void validateTeamColor(ChessGame.TeamColor color) {
        if (color == null) {
            throw new BadRequestException("Error: Team Color must be non-null");
        }
    }
}
