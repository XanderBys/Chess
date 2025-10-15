package util;

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
}
