package chess;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator extends PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor){
        int row = position.getRow(), col = position.getColumn();
        int startRow;
        int promotionRow;
        int direction;
        if (pieceColor == ChessGame.TeamColor.WHITE){
            startRow = 2;
            promotionRow = 8;
            direction = 1;
        }
        else{
            startRow = 7;
            promotionRow = 1;
            direction = -1;
        }

        ChessPosition frontPosition = new ChessPosition(row+direction, col);
        ChessPosition leftDiagonalPosition = new ChessPosition(row+direction, col - 1);
        ChessPosition rightDiagonalPosition = new ChessPosition(row+direction, col + 1);
        ChessPiece frontPiece, leftDiagonalPiece, rightDiagonalPiece;
        boolean promotion;
        if (row+direction == promotionRow){
            promotion = true;
            frontPiece = board.getPiece(frontPosition);
        }
        else if ((direction == 1 && row+direction < promotionRow) || (direction == -1 && row+direction > promotionRow)){
            promotion = false;
            frontPiece = board.getPiece(frontPosition);
        }
        else{
            return moves;
        }

        if (col - 1 >= 1){
            leftDiagonalPiece = board.getPiece(leftDiagonalPosition);
        }
        else{
            leftDiagonalPiece = null;
        }
        if (col + 1 <= 8){
            rightDiagonalPiece = board.getPiece(rightDiagonalPosition);
        }
        else{
            rightDiagonalPiece = null;
        }

        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        ArrayList<ChessPiece.PieceType> promotionPieces = new ArrayList<>();
        if (frontPiece == null){
            endPositions.add(frontPosition);
        }
        if (leftDiagonalPiece != null && leftDiagonalPiece.getTeamColor() != pieceColor) {
            endPositions.add(leftDiagonalPosition);
        }
        if (rightDiagonalPiece != null && rightDiagonalPiece.getTeamColor() != pieceColor) {
            endPositions.add(rightDiagonalPosition);
        }

        if (row == startRow){
            ChessPosition oneAhead = new ChessPosition(row + direction, col);
            ChessPosition twoAhead = new ChessPosition(row + 2*direction, col);
            if (board.getPiece(oneAhead) == null && board.getPiece(twoAhead) == null){
                moves.add(new ChessMove(position, twoAhead));
            }
        }

        if (promotion){
            promotionPieces.add(ChessPiece.PieceType.QUEEN);
            promotionPieces.add(ChessPiece.PieceType.ROOK);
            promotionPieces.add(ChessPiece.PieceType.BISHOP);
            promotionPieces.add(ChessPiece.PieceType.KNIGHT);
            for (ChessPiece.PieceType promotionPiece : promotionPieces){
                for (ChessPosition endPosition : endPositions){
                    moves.add(new ChessMove(position, endPosition, promotionPiece));
                }
            }
        }
        else{
            for (ChessPosition endPosition : endPositions){
                moves.add(new ChessMove(position, endPosition));
            }
        }

        return moves;
    }
}
