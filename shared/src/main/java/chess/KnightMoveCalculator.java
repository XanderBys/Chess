package chess;

import java.util.ArrayList;

public class KnightMoveCalculator extends PieceMoveCalculator{
    @Override
    public ArrayList<ChessMove> getMoves(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor pieceColor) {
        int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                              {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        addMoves(board, startPosition, directions, pieceColor);
        return moves;
    }


}
