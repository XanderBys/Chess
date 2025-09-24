package chess;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PieceMoveCalculator {
    public ArrayList<ChessMove> moves = new ArrayList<>();

    public abstract ArrayList<ChessMove> getMoves(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor pieceColor);

    public void searchOutwards(ChessBoard board, ChessPosition startPosition, int[][] directions, ChessGame.TeamColor pieceColor){
        boolean[] continueSearch = new boolean[directions.length];
        for (int i = 0; i < directions.length; i++){
            continueSearch[i] = true;
        }
        int offset = 1;

        while (anyTrue(continueSearch)){
            continueSearch = addMoves(board, startPosition, directions, pieceColor, offset, continueSearch);
            offset++;
        }
    }

    public boolean[] addMoves(ChessBoard board, ChessPosition startPosition, int[][] directions,
                              ChessGame.TeamColor pieceColor, int offset, boolean[] continueSearch,
                              ChessPiece.PieceType promotionPiece){
        int startRow = startPosition.getRow(), startCol = startPosition.getColumn();
        for (int i = 0; i < directions.length; i++){
            ChessPosition newPosition = new ChessPosition(startRow + directions[i][0] * offset,
                                                          startCol + directions[i][1] * offset);

            if (continueSearch[i] && newPosition.isValid()){
                ChessGame.TeamColor color = getColorAt(board, newPosition);
                if (color == null){
                    moves.add(new ChessMove(startPosition, newPosition, promotionPiece));
                }
                else if (color == pieceColor){
                    // this is a piece of our own color, so we stop short
                    continueSearch[i] = false;
                }
                else{
                    // this is a piece of the opposing team, so we stop here, but we can add this move
                    moves.add(new ChessMove(startPosition, newPosition, promotionPiece));
                    continueSearch[i] = false;
                }
            }
            else{
                continueSearch[i] = false;
            }
        }
        return continueSearch;
    }

    public boolean[] addMoves(ChessBoard board, ChessPosition startPosition, int[][] directions,
                              ChessGame.TeamColor pieceColor, int offset, boolean[] continueSearch){
        return addMoves(board, startPosition, directions, pieceColor, offset, continueSearch, null);
    }

    public void addMoves(ChessBoard board, ChessPosition startPosition, int[][] directions,
                         ChessGame.TeamColor pieceColor, ChessPiece.PieceType promotionPiece){
        boolean[] continueSearch = new boolean[directions.length];
        Arrays.fill(continueSearch, true);

        addMoves(board, startPosition, directions, pieceColor, 1, continueSearch, promotionPiece);
    }

    public void addMoves(ChessBoard board, ChessPosition startPosition, int[][] directions,
                         ChessGame.TeamColor pieceColor){
        addMoves(board, startPosition, directions, pieceColor, null);
    }

    public void resetMoves(){
        moves = new ArrayList<>();
    }

    public ChessGame.TeamColor getColorAt(ChessBoard board, ChessPosition position){
        ChessPiece piece = board.getPiece(position);
        if (piece == null){
            return null;
        }
        else {
            return piece.getTeamColor();
        }
    }
    public boolean anyTrue(boolean[] arr){
        for (boolean x : arr){
            if (x){
                return true;
            }
        }
        return false;
    }
}
