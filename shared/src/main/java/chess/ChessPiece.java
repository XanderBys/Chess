package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {
    ChessGame.TeamColor pieceColor;
    PieceType type;
    PieceMoveCalculator moveCalculator;
    int startRow;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

        if (this.type == PieceType.KING){
            moveCalculator = new KingMoveCalculator();
        }
        else if (this.type == PieceType.QUEEN){
            moveCalculator = new QueenMoveCalculator();
        }
        else if (this.type == PieceType.ROOK){
            moveCalculator = new RookMoveCalculator();
        }
        else if (this.type == PieceType.BISHOP){
            moveCalculator = new BishopMoveCalculator();
        }
        else if (this.type == PieceType.KNIGHT){
            moveCalculator = new KnightMoveCalculator();
        }
        else if (this.type == PieceType.PAWN){
            moveCalculator = new PawnMoveCalculator();
        }

        if (this.type == PieceType.PAWN){
            if (this.pieceColor == ChessGame.TeamColor.WHITE) {
                startRow = 2;
            }
            else{
                startRow = 7;
            }
        }
        else {
            if (this.pieceColor == ChessGame.TeamColor.WHITE) {
                startRow = 1;
            } else {
                startRow = 8;
            }
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    public int getStartRow(){
        return startRow;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        moveCalculator.resetMoves();
        return moveCalculator.getMoves(board, myPosition, pieceColor);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        String s;
        if (type == PieceType.KING){
            s = "k";
        }
        else if (type == PieceType.QUEEN){
            s = "q";
        }
        else if (type == PieceType.BISHOP){
            s = "b";
        }
        else if (type == PieceType.KNIGHT){
            s = "n";
        }
        else if (type == PieceType.ROOK){
            s = "r";
        }
        else {
            s = "p";
        }

        if (pieceColor == ChessGame.TeamColor.WHITE){
            s = s.toUpperCase();
        }

        return s;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
