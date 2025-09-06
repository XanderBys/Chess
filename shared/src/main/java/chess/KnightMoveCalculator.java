package chess;
import java.util.Collection;
import static java.lang.Math.abs;

public class KnightMoveCalculator extends PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor){
        int row = position.getRow();
        int col = position.getColumn();
        int[][] offsets = {{1, 2}, {2, 1}, {-1, 2}, {2, -1}, {1, -2}, {-2, 1}, {-1, -2}, {-2, -1}};

        for (int i = 0; i < offsets.length; i++){
            int new_row = row + offsets[i][0];
            int new_col = col + offsets[i][1];
            if (new_row > 8 || new_row < 1 || new_col > 8 || new_col < 1){
                // if the position is out of bounds, just skip it
                continue;
            }

            ChessPosition endPosition = new ChessPosition(new_row, new_col);
            addMove(board, position, endPosition, pieceColor);
        }
        return moves;
    }
}
