package chess;

import java.util.ArrayList;

public class BishopMoveCalculator extends PieceMoveCalculator{
    @Override
    public ArrayList<ChessMove> getMoves(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor pieceColor) {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        searchOutwards(board, startPosition, directions, pieceColor);
        return moves;
    }


}
