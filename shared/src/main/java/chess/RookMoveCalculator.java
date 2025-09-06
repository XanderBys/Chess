package chess;
import java.util.Collection;

public class RookMoveCalculator extends PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor){
        // Rooks move up and down and side to side
        // We use a loop to scan in each direction, adding valid moves as we go
        // Once we run into another piece, if it's an enemy piece, we add that move
        // (since we can capture)
        // If it's one of our pieces, we stop short.
        for (int i = position.getRow()-1, j = position.getRow()+1; i >= 1 || j <= 8; i--, j++){
            if (i >= 1) {
                ChessPosition endPosition = new ChessPosition(i, position.getColumn());

                boolean result = addMove(board, position, endPosition, pieceColor);
                if (result){
                    // there is a piece on endPosition, so we stop searching in this line
                    i = -1;
                }
            }
            if (j <= 8){
                ChessPosition endPosition = new ChessPosition(j, position.getColumn());

                boolean result = addMove(board, position, endPosition, pieceColor);
                if (result){
                    // there is a piece on endPosition, so we stop searching in this line
                    j = 9;
                }
            }
        }

        for (int i = position.getColumn()-1, j = position.getColumn()+1; i >= 1 || j <= 8; i--, j++){
            if (i >= 1) {
                ChessPosition endPosition = new ChessPosition(position.getRow(), i);

                boolean result = addMove(board, position, endPosition, pieceColor);
                if (result){
                    // there is a piece on endPosition, so we stop searching in this line
                    i = -1;
                }
            }
            if (j <= 8){
                ChessPosition endPosition = new ChessPosition(position.getRow(), j);

                boolean result = addMove(board, position, endPosition, pieceColor);
                if (result){
                    // there is a piece on endPosition, so we stop searching in this line
                    j = 9;
                }
            }
        }

        return moves;
    }
}
