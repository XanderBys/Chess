package chess;

import java.util.ArrayList;

public class QueenMoveCalculator extends PieceMoveCalculator{
    @Override
    public ArrayList<ChessMove> getMoves(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor pieceColor) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        searchOutwards(board, startPosition, directions, pieceColor);
        return moves;
    }


}
