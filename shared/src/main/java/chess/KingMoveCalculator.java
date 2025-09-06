package chess;
import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator extends PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor){
        int row = position.getRow();
        int col = position.getColumn();

        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (row + i > 8 || row + i < 1 || col + j > 8 || col + i < 1){
                    // if the position is out of bounds, just skip it
                    continue;
                }
                ChessPosition endPosition = new ChessPosition(row+i, col+j);
                addMove(board, position, endPosition, pieceColor);
            }
        }

        return moves;
    }
}
