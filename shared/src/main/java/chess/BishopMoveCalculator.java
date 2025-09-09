package chess;
import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator extends PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor){
        boolean[] continueSearch = {true, true, true, true}; // one element for each direction of search

        for (int i = 1; i < 8; i++){
            // create a new position for each direction of search (the four diagonals)
            ArrayList<ChessPosition> endPositions = new ArrayList<>();
            endPositions.add(new ChessPosition(position.getRow()+i, position.getColumn()+i));
            endPositions.add(new ChessPosition(position.getRow()+i, position.getColumn()-i));
            endPositions.add(new ChessPosition(position.getRow()-i, position.getColumn()+i));
            endPositions.add(new ChessPosition(position.getRow()-i, position.getColumn()-i));

            for (int j = 0; j < endPositions.toArray().length; j++){
                ChessPosition pos = endPositions.get(j);
                if (pos.getRow() >= 1 && pos.getRow() <= 8 && pos.getColumn() >= 1 && pos.getColumn() <= 8 && continueSearch[j]){
                    boolean result = addMove(board, position, pos, pieceColor);
                    if (result){
                        // there is a piece on endPosition, so we stop searching in this line
                        continueSearch[j] = false;
                    }
                }
            }
        }

        return moves;
    }
}
