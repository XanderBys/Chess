package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board = new ChessBoard();
    public ChessGame() {
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isValidMove(move)){
            throw new InvalidMoveException(move + " is not a valid move with board:\n" + board + "\nand " + teamTurn + " to play.");
        }

        updateBoard(this.board, move);

        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        }
        else {
            teamTurn = TeamColor.WHITE;
        }
    }

    private void updateBoard(ChessBoard board, ChessMove move){
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() == null){
            board.addPiece(move.getEndPosition(), pieceToMove);
        }
        else{
            board.addPiece(move.getEndPosition(), new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece()));
        }
        board.addPiece(move.getStartPosition(), null);
    }

    private boolean isValidMove(ChessMove move){
        // first check if the move is contained in the possible moves calculated for that piece
        // then,
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null || !piece.pieceMoves(board, startPosition).contains(move)){
            return false;
        }
        else if (piece.getTeamColor() != teamTurn){
            return false;
        }

        try {
            ChessBoard boardCopy = (ChessBoard) board.clone();
            updateBoard(boardCopy, move);
            if (isInCheck(boardCopy, teamTurn)){
                return false;
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(ChessBoard board, TeamColor teamColor) {
        /*
        For each piece on the board
        if the piece is the opposite color
        iterate over validMoves for that piece
        if one of the moves ends where king currently is,
        return true
        
         */
        ChessPosition kingPosition = findKingPosition(teamColor);

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() == teamColor){
                    continue;
                }

                Collection<ChessMove> moves = piece.pieceMoves(board, pos);

                for (ChessMove move : moves){
                    if (move.getEndPosition().equals(kingPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck(TeamColor teamColor){
        return isInCheck(this.board, teamColor);
    }

    /**
     * Determine and return the location of the king for the given board and color
     *
     * @param board the board to search on
     * @param teamColor the color of the king to search for
     * @return a ChessPosition where the king of the given color is located (or null if there is no king)
     */
    private ChessPosition findKingPosition(ChessBoard board, TeamColor teamColor){
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    return pos;
                }
            }
        }
        return null;
    }

    private ChessPosition findKingPosition(TeamColor teamColor){
        return findKingPosition(this.board, teamColor);
    }

    /**
     * Determines if there are any legal moves for the given team's king
     * @param board the board to consider
     * @param teamColor the color whose king we are considering
     * @return true if there is at least one valid move for the king of teamColor, false if not
     */
    private boolean validKingMoves(ChessBoard board, TeamColor teamColor){
        ChessPosition kingPosition = findKingPosition(board, teamColor);
        ChessPiece king = board.getPiece(kingPosition);
        if (king == null){
            return false;
        }

        Collection<ChessMove> moves = king.pieceMoves(board, kingPosition);
        for (ChessMove move : moves){
            try {
                ChessBoard boardCopy = (ChessBoard) board.clone();
                updateBoard(boardCopy, move);
                if (!isInCheck(boardCopy, teamColor)){
                    return true;
                }
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(ChessBoard board, TeamColor teamColor){
        return isInCheck(board, teamColor) && !validKingMoves(board, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheckmate(this.board, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(board, teamColor)){
            return false;
        }

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() != teamColor){
                    continue;
                }

                Collection<ChessMove> moves = new ArrayList<>(piece.pieceMoves(board, pos));
                if (moves.isEmpty()){
                    continue;
                }

                for (ChessMove move : moves){
                    if (isValidMove(move)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getBoard());
    }
}
