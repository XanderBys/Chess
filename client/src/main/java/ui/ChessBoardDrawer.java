package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardDrawer {
    private static final int ROW_LENGTH = 8;
    private static final String[] COL_HEADERS = {"a", "b", "c", "d", "e", "f", "g", "h"};

    private static final int COL_SPACING = 3;
    private static final String PIECE_PADDING = "";

    /**
     * only for testing purposes
     *
     * @param args should be left empty
     */
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessBoardDrawer.drawBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
        System.out.println();
        ChessBoardDrawer.drawBoard(game.getBoard(), ChessGame.TeamColor.BLACK);
    }

    /**
     * Draws the board and row and column headers from a given player's point of view
     * @param board an instance of ChessBoard to be displayed on the console
     * @param perspective a TeamColor representing the point of view to be shown
     */
    public static void drawBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        printColHeaders(perspective);
        if (perspective == ChessGame.TeamColor.WHITE) {
            for (int i = ROW_LENGTH; i >= 1; i--) {
                drawRow(board, i, perspective);
            }
        } else if (perspective == ChessGame.TeamColor.BLACK) {
            for (int i = 1; i <= ROW_LENGTH; i++) {
                drawRow(board, i, perspective);
            }
        }
        printColHeaders(perspective);

    }

    private static void printColHeaders(ChessGame.TeamColor perspective) {
        String space = " ".repeat(3);

        resetColors();
        System.out.print(EMPTY + " ");

        if (perspective.equals(ChessGame.TeamColor.WHITE)) {
            for (int i = 0; i < ROW_LENGTH; i++) {
                System.out.print(COL_HEADERS[i] + space);
            }
        } else {
            for (int i = ROW_LENGTH - 1; i >= 0; i--) {
                System.out.print(COL_HEADERS[i] + space);
            }
        }

        System.out.println();
    }

    private static void printRowHeaderPrefix(int rowNumber) {
        printRowHeader(rowNumber);
        System.out.print(" ".repeat(COL_SPACING));
    }

    private static void printRowHeaderSuffix(int rowNumber) {
        System.out.print(" ".repeat(COL_SPACING));
        printRowHeader(rowNumber);
    }

    private static void printRowHeader(int rowNumber) {
        resetColors();

        System.out.print(rowNumber);
    }

    private static void drawRow(ChessBoard board, int rowNumber, ChessGame.TeamColor perspective) {
        printRowHeaderPrefix(rowNumber);
        drawRowOfSquares(board, rowNumber, perspective);
        printRowHeaderSuffix(rowNumber);
        System.out.println();
    }

    private static void drawRowOfSquares(ChessBoard board, int rowNumber, ChessGame.TeamColor perspective) {
        for (int j = 1; j <= ROW_LENGTH; j++) {
            setSquareBGColor(rowNumber, j, perspective);

            ChessPiece piece = board.getPiece(new ChessPosition(rowNumber, j));
            drawPiece(piece);
        }

        System.out.print(RESET_BG_COLOR);
    }

    private static void drawPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print(PIECE_PADDING + EMPTY);
        } else if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            System.out.print(SET_TEXT_COLOR_BLACK + PIECE_PADDING);
            switch (piece.getPieceType()) {
                case PAWN -> System.out.print(WHITE_PAWN);
                case KNIGHT -> System.out.print(WHITE_KNIGHT);
                case BISHOP -> System.out.print(WHITE_BISHOP);
                case ROOK -> System.out.print(WHITE_ROOK);
                case QUEEN -> System.out.print(WHITE_QUEEN);
                case KING -> System.out.print(WHITE_KING);
            }
        } else {
            System.out.print(SET_TEXT_COLOR_BLACK + PIECE_PADDING);
            switch (piece.getPieceType()) {
                case PAWN -> System.out.print(BLACK_PAWN);
                case KNIGHT -> System.out.print(BLACK_KNIGHT);
                case BISHOP -> System.out.print(BLACK_BISHOP);
                case ROOK -> System.out.print(BLACK_ROOK);
                case QUEEN -> System.out.print(BLACK_QUEEN);
                case KING -> System.out.print(BLACK_KING);
            }
        }
        System.out.print(PIECE_PADDING);
    }

    private static void setSquareBGColor(int row, int col, ChessGame.TeamColor perspective) {
        if (isWhiteSquare(row, col, perspective)) {
            setWhite();
        } else {
            setGreen();
        }
    }

    private static boolean isWhiteSquare(int row, int col, ChessGame.TeamColor perspective) {
        return (perspective == ChessGame.TeamColor.BLACK && (row + col) % 2 == 0)
                || (perspective == ChessGame.TeamColor.WHITE && (row + col) % 2 == 1);
    }

    private static void setWhite() {
        System.out.print(SET_BG_COLOR_WHITE);
    }

    private static void setGreen() {
        System.out.print(SET_BG_COLOR_DARK_GREEN);
    }

    private static void resetColors() {
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
    }
}
