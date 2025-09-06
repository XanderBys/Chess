package chess;
import java.util.Collection;

public abstract class PieceMoveCalculator {
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
