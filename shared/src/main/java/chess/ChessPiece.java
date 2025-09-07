package chess;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    private PieceMoveCalculator moveCalculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;

        switch (type){
            case PieceType.KING:
                moveCalculator = new KingMoveCalculator();
                break;
            case PieceType.QUEEN:
                moveCalculator = new QueenMoveCalculator();
                break;
            case PieceType.BISHOP:
                moveCalculator = new BishopMoveCalculator();
                break;
            case PieceType.KNIGHT:
                moveCalculator = new KnightMoveCalculator();
                break;
            case PieceType.ROOK:
                moveCalculator = new RookMoveCalculator();
                break;
            case PieceType.PAWN:
                moveCalculator = new PawnMoveCalculator();
                break;
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
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return moveCalculator.pieceMoves(board, myPosition, getTeamColor());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    /**
     *
     * @return a string of one character representing the piece.
     * Key:
     * Upper case letters: white pieces
     * Lower case letters: black pieces
     * p: pawn
     * r: rook
     * n: knight
     * b: bishop
     * q: queen
     * k: king
     *
     */
    @Override
    public String toString() {
        String s = "";

        if (this.type == PieceType.PAWN){
            s += 'p';
        }
        else if (this.type == PieceType.ROOK){
            s += 'r';
        }
        else if (this.type == PieceType.KNIGHT){
            s += 'n';
        }
        else if (this.type == PieceType.BISHOP){
            s += 'b';
        }
        else if (this.type == PieceType.QUEEN){
            s += 'q';
        }
        else if (this.type == PieceType.KING){
            s += 'k';
        }

        if (this.color == ChessGame.TeamColor.WHITE){
            s = s.toUpperCase();
        }

        return s;
    }
}
