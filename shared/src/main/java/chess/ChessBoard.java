package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;

        return Objects.deepEquals(this.board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.board);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (ChessPiece[] row : this.board){
            builder.append("|");
            for (ChessPiece piece : row){
                if (piece == null){
                    builder.append(" ");
                }
                else{
                    builder.append(piece);
                }

                builder.append("|");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}
