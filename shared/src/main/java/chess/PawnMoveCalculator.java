package chess;

import java.util.ArrayList;

public class PawnMoveCalculator extends PieceMoveCalculator{
    @Override
    public ArrayList<ChessMove> getMoves(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor pieceColor) {
        int startRow = startPosition.getRow(), startCol = startPosition.getColumn();

        int direction;
        int promotionRow;

        if (pieceColor == ChessGame.TeamColor.WHITE){
            direction = 1;
            promotionRow = 8;
        }
        else{
            direction = -1;
            promotionRow = 1;
        }

        ChessPosition front = new ChessPosition(startRow + direction, startCol);
        ChessPosition leftDiag = new ChessPosition(startRow + direction, startCol - 1);
        ChessPosition rightDiag = new ChessPosition(startRow + direction, startCol + 1);

        boolean onStart = (pieceColor == ChessGame.TeamColor.WHITE && startRow == 2) ||
                          (pieceColor == ChessGame.TeamColor.BLACK && startRow == 7);

        ChessGame.TeamColor oppositeColor;
        if (pieceColor == ChessGame.TeamColor.WHITE){
            oppositeColor = ChessGame.TeamColor.BLACK;
        }
        else{
            oppositeColor = ChessGame.TeamColor.WHITE;
        }

        ArrayList<ChessPiece.PieceType> promotionPieces = new ArrayList<>();
        if (startRow + direction == promotionRow){
            promotionPieces.add(ChessPiece.PieceType.QUEEN);
            promotionPieces.add(ChessPiece.PieceType.ROOK);
            promotionPieces.add(ChessPiece.PieceType.BISHOP);
            promotionPieces.add(ChessPiece.PieceType.KNIGHT);
        }
        else{
            promotionPieces.add(null);
        }

        for (ChessPiece.PieceType piece : promotionPieces){
            if (front.isValid() && board.getPiece(front) == null){
                moves.add(new ChessMove(startPosition, front, piece));
                ChessPosition twoFront = new ChessPosition(startRow + 2 * direction, startCol);

                if (onStart && board.getPiece(twoFront) == null){
                    moves.add(new ChessMove(startPosition, twoFront));
                }
            }

            if (leftDiag.isValid() && getColorAt(board, leftDiag) == oppositeColor){
                moves.add(new ChessMove(startPosition, leftDiag, piece));
            }

            if (rightDiag.isValid() && getColorAt(board, rightDiag) == oppositeColor){
                moves.add(new ChessMove(startPosition, rightDiag, piece));
            }

        }

        // add en passant capture, if relevant
        // this will be validated in the ChessGame class later
        // to make sure the pawn being captured was moved in the past turn
        // here, we just add the option if there is a pawn in the relevant spot
        if ((pieceColor == ChessGame.TeamColor.WHITE && startRow == 5) ||
            (pieceColor == ChessGame.TeamColor.BLACK && startRow == 4)){
            // this is the only row where en passant is allowed
            // (two squares above where the opposing pawns start
            ChessPosition left = new ChessPosition(startRow, startCol - 1);
            ChessPosition right = new ChessPosition(startRow, startCol + 1);
            if (left.isValid() && getColorAt(board, left) == oppositeColor){
                moves.add(new ChessMove(startPosition, leftDiag, true));
            }

            if (right.isValid() && getColorAt(board, right) == oppositeColor){
                moves.add(new ChessMove(startPosition, rightDiag, true));
            }
        }


        return moves;
    }


}
