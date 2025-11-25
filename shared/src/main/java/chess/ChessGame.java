package chess;

import java.util.*;

import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board = new ChessBoard();

    boolean[][] enPassant = new boolean[8][8];

    private boolean gameOver = false;

    HashMap<ChessPiece.PieceType, Boolean> whiteCastle = new HashMap<>();
    HashMap<ChessPiece.PieceType, Boolean> blackCastle = new HashMap<>();
    HashMap<TeamColor, HashMap<ChessPiece.PieceType, Boolean>> canCastle = new HashMap<>();

    private static final ChessPosition WHITE_KING_POSITION = new ChessPosition(1, 5);
    private static final ChessMove WHITE_QUEENSIDE_CASTLE = new ChessMove(WHITE_KING_POSITION, new ChessPosition(1, 3), null);
    private static final ChessMove WHITE_KINGSIDE_CASTLE = new ChessMove(WHITE_KING_POSITION, new ChessPosition(1, 7), null);

    private static final ChessPosition BLACK_KING_POSITION = new ChessPosition(8, 5);
    private static final ChessMove BLACK_QUEENSIDE_CASTLE = new ChessMove(BLACK_KING_POSITION, new ChessPosition(8, 3), null);
    private static final ChessMove BLACK_KINGSIDE_CASTLE = new ChessMove(BLACK_KING_POSITION, new ChessPosition(8, 7), null);

    private static final ArrayList<ChessMove> CASTLING_MOVES = new ArrayList<>(Arrays.asList(WHITE_KINGSIDE_CASTLE,
                                                                                            WHITE_QUEENSIDE_CASTLE,
                                                                                            BLACK_KINGSIDE_CASTLE,
                                                                                            BLACK_QUEENSIDE_CASTLE));

    public ChessGame() {
        board.resetBoard();
        teamTurn = TeamColor.WHITE;

        whiteCastle.put(ChessPiece.PieceType.KING, true);
        whiteCastle.put(ChessPiece.PieceType.QUEEN, true);
        blackCastle.put(ChessPiece.PieceType.KING, true);
        blackCastle.put(ChessPiece.PieceType.QUEEN, true);
        canCastle.put(TeamColor.WHITE, whiteCastle);
        canCastle.put(TeamColor.BLACK, blackCastle);
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
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return moves;
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING){
            if (piece.getTeamColor() == TeamColor.WHITE && startPosition.equals(WHITE_KING_POSITION)){
                if (isValidMove(board, WHITE_KINGSIDE_CASTLE)){
                    moves.add(WHITE_KINGSIDE_CASTLE);
                }
                if (isValidMove(board, WHITE_QUEENSIDE_CASTLE)){
                    moves.add(WHITE_QUEENSIDE_CASTLE);
                }
            }
            else if (piece.getTeamColor() == TeamColor.BLACK && startPosition.equals(BLACK_KING_POSITION)){
                if (isValidMove(board, BLACK_KINGSIDE_CASTLE)){
                    moves.add(BLACK_KINGSIDE_CASTLE);
                }
                if (isValidMove(board, BLACK_QUEENSIDE_CASTLE)){
                    moves.add(BLACK_QUEENSIDE_CASTLE);
                }
            }
        }
        Collection<ChessMove> pieceMoves = new ArrayList<>(piece.pieceMoves(board, startPosition));
        for (ChessMove move : pieceMoves) {
            if (isValidMove(board, move)) {
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        // the test cases don't have an enPassant flag in 'move',
        // so we have to check and set the flag manually if it is an en passant move
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN
                && move.getEndPosition().getColumn() != move.getStartPosition().getColumn()
                && board.getPiece(move.getEndPosition()) == null){
            // if the piece is a pawn AND it moves diagonally AND the end square is empty
            // it has to be enPassant, since we already confirmed the move was valid
            move = new ChessMove(move.getStartPosition(), move.getEndPosition(), true);
        }

        if (!isValidMove(move) || (piece != null && piece.getTeamColor() != teamTurn)) {
            throw new InvalidMoveException("That move is invalid.");
        } else if (isGameOver()) {
            throw new InvalidMoveException("You cannot make moves once the game is over.");
        }

        updateBoard(this.board, move);

        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        }
        else {
            teamTurn = TeamColor.WHITE;
        }

        updateEnPassant(move);
        updateCanCastle(move);
    }

    public void updateCanCastle(ChessMove move){
        ChessPiece piece = board.getPiece(move.getEndPosition());
        ChessPiece.PieceType pieceType = piece.getPieceType();
        if (pieceType == ChessPiece.PieceType.KING){
            HashMap<ChessPiece.PieceType, Boolean> cantCastle = new HashMap<>();
            cantCastle.put(ChessPiece.PieceType.KING, false);
            cantCastle.put(ChessPiece.PieceType.QUEEN, false);
            canCastle.put(piece.getTeamColor(), cantCastle);
        }
        else if (pieceType == ChessPiece.PieceType.ROOK){
            if (move.getStartPosition().getColumn() == 1){
                // queenside
                canCastle.get(piece.getTeamColor()).put(ChessPiece.PieceType.QUEEN, false);
            }
            else if (move.getStartPosition().getColumn() == 8){
                // kingside
                canCastle.get(piece.getTeamColor()).put(ChessPiece.PieceType.KING, false);
            }
        }
    }
    public void updateEnPassant(ChessMove move){
        // update the en passant tracker.
        // first, reset it to be all 'false' so that en passant only works for one move after the pawn moves
        enPassant = new boolean[8][8];
        ChessPiece piece = board.getPiece(move.getEndPosition());

        // then, if a pawn moved into en passant range, set the flag to be true
        int rowDiff = abs(move.getEndPosition().getRow() - move.getStartPosition().getRow());
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && rowDiff == 2){
            // in this case, a pawn has moved up two squares, so set the en passant flag for that square to be true
            allowEnPassant(move.getEndPosition());
        }
    }

    public void updateBoard(ChessBoard board, ChessMove move){
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());

        if (move.equals(WHITE_KINGSIDE_CASTLE) || move.equals(BLACK_KINGSIDE_CASTLE)){
            ChessPosition rookPosition = new ChessPosition(move.getStartPosition().getRow(), 8);
            ChessPosition newRookPosition = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn()-1);
            board.addPiece(newRookPosition, board.getPiece(rookPosition));
            board.addPiece(rookPosition, null);
        }
        else if (move.equals(WHITE_QUEENSIDE_CASTLE) || move.equals(BLACK_QUEENSIDE_CASTLE)){
            ChessPosition rookPosition = new ChessPosition(move.getStartPosition().getRow(), 1);
            ChessPosition newRookPosition = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn()+1);
            board.addPiece(newRookPosition, board.getPiece(rookPosition));
            board.addPiece(rookPosition, null);
        }

        if (move.isEnPassant()){
            ChessPosition capturePosition = new ChessPosition(move.getStartPosition().getRow(), 
                                                              move.getEndPosition().getColumn());
            board.addPiece(move.getEndPosition(), pieceToMove);
            board.addPiece(capturePosition, null);
        }
        else if (move.getPromotionPiece() == null){
            board.addPiece(move.getEndPosition(), pieceToMove);
        }
        else{
            board.addPiece(move.getEndPosition(), new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece()));
        }
        board.addPiece(move.getStartPosition(), null);
    }

    public boolean isValidMove(ChessBoard board, ChessMove move) {
        // first check if the move is contained in the possible moves calculated for that piece
        // then,
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null){
            return false;
        }
        else if (move.isEnPassant()
                && !enPassantAllowed(new ChessPosition(move.getStartPosition().getRow(),
                                                       move.getEndPosition().getColumn())) ){
            return false;
        }
        else if (CASTLING_MOVES.contains(move)){
            if (isInCheck(board, piece.getTeamColor())){
                return false;
            }

            if (move.equals(BLACK_KINGSIDE_CASTLE) || move.equals(WHITE_KINGSIDE_CASTLE)){
                if (move.equals(BLACK_KINGSIDE_CASTLE) && !canCastle.get(TeamColor.BLACK).get(ChessPiece.PieceType.KING)
                || move.equals(WHITE_KINGSIDE_CASTLE) && !canCastle.get(TeamColor.WHITE).get(ChessPiece.PieceType.KING)
                ){
                    return false;
                }
                for (int i = move.getStartPosition().getColumn()+1; i < move.getEndPosition().getColumn(); i++){
                    ChessMove newMove = generateNewMove(move, i);
                    if (moveCausesCheck(board, newMove) || board.getPiece(move.getEndPosition()) != null) {
                        return false;
                    }
                }
            }
            else if (move.equals(WHITE_QUEENSIDE_CASTLE) || move.equals(BLACK_QUEENSIDE_CASTLE)){
                if
                (move.equals(BLACK_QUEENSIDE_CASTLE) && !canCastle.get(TeamColor.BLACK).get(ChessPiece.PieceType.QUEEN)
                || move.equals(WHITE_QUEENSIDE_CASTLE) && !canCastle.get(TeamColor.WHITE).get(ChessPiece.PieceType.QUEEN
                )){
                    return false;
                }
                for (int i = move.getStartPosition().getColumn()-1; i > move.getEndPosition().getColumn(); i--){
                    ChessMove newMove = generateNewMove(move, i);
                    if (moveCausesCheck(board, newMove) || board.getPiece(move.getEndPosition()) != null) {
                        return false;
                    }
                }
            }

        }
        else if (!piece.pieceMoves(board, startPosition).contains(move)){
            return false;
        }
        return !moveCausesCheck(board, move);
    }

    ChessMove generateNewMove(ChessMove move, int i) {
        return new ChessMove(move.getStartPosition(), new ChessPosition(move.getStartPosition().getRow(), i));
    }

    private boolean moveCausesCheck(ChessBoard board, ChessMove move){
        ChessPiece piece = board.getPiece(move.getStartPosition());
        try {
            ChessBoard boardCopy = (ChessBoard) board.clone();
            updateBoard(boardCopy, move);
            return isInCheck(boardCopy, piece.getTeamColor());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isValidMove(ChessMove move){
        return isValidMove(this.board, move);
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

        ChessPosition kingPosition = findKingPosition(board, teamColor);

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() == teamColor){
                    continue;
                }

                Collection<ChessMove> moves = new ArrayList<>(piece.pieceMoves(board, pos));

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


    private boolean enPassantAllowed(ChessPosition pos){
        return enPassant[pos.getRow() - 1][pos.getColumn() - 1];
    }

    private void allowEnPassant(ChessPosition pos){
        enPassant[pos.getRow() - 1][pos.getColumn() - 1] = true;
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

    /**
     * Gets all valid moves for a given team at a given board state.
     * @param board the board to consider when calculating all valid moves
     * @param teamColor the team color whose moves we're calculating
     * @return a Collection containing all the valid moves in the given game state
     */
    private Collection<ChessMove> getAllValidMoves(ChessBoard board, TeamColor teamColor){
        Collection<ChessMove> moves = new ArrayList<>();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() != teamColor){
                    continue;
                }
                Collection<ChessMove> pieceMoves = validMoves(pos);
                moves.addAll(pieceMoves);
            }
        }
        return moves;
    }

    private Collection<ChessMove> getAllValidMoves(TeamColor teamColor){
        return getAllValidMoves(this.board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(ChessBoard board, TeamColor teamColor){
        return isInCheck(board, teamColor) && getAllValidMoves(board, teamColor).isEmpty();
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheckmate(this.board, teamColor);
    }

    public boolean isGameOver() {
        return isInCheckmate(TeamColor.WHITE) || isInCheckmate(TeamColor.BLACK)
                || isInStalemate(TeamColor.WHITE) || isInStalemate(TeamColor.BLACK)
                || gameOver;
    }

    public void setGameOver(boolean status) {
        gameOver = status;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && getAllValidMoves(teamColor).isEmpty();
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
