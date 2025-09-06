package chess;
import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMoveCalculator {
    ArrayList<ChessMove> moves = new ArrayList<>();

    /**
     * Calculates the possible moves for a piece on a given square on the given board.
     *
     * @param board The current ChessBoard instance
     * @param position a ChessPosition representing the location of the piece
     * @param pieceColor the color of the piece whose moves we're calculating (this matters because it affects which
     *                   pieces we can capture)
     * @return moves An ArrayList of ChessMove's containing all of the possible moves for the piece
     */
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor);

    /**
     * Checks whether a move goes to a square where there is a piece and, if the move is valid, adds the move to this.moves
     * @param board The current ChessBoard instance
     * @param startPosition the starting position of the potential move
     * @param endPosition the end position of the potential move
     * @param pieceColor the color of the piece whose moves we're calculating
     * @return true if the new position has a piece on it and false otherwise. This information should be used to determine
     *          whether to continue iteration or stop early when calculating possible moves.
     */
    public boolean addMove(ChessBoard board, ChessPosition startPosition,
                                       ChessPosition endPosition, ChessGame.TeamColor pieceColor){
        ChessPiece piece = board.getPiece(endPosition);

        if (piece == null){
            // if piece is null, then the position is empty, so we add the move and continue
            moves.add(new ChessMove(startPosition, endPosition));
            return false;
        }
        else if (piece.getTeamColor() == pieceColor){
            // the piece on endPosition is our same team, so we stop short here
            return true;
        }
        else {
            // there's an enemy piece on endPosition, so we can capture it
            moves.add(new ChessMove(startPosition, endPosition));
            return true;
        }
    }
}
