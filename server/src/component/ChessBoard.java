package component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChessBoard {
	public static final int DEFAULT_ROWS = 8;
	public static final int DEFAULT_COLUMNS = 8;

	private final int rows;
	private final int columns;
	private final Piece[][] grid;

	public ChessBoard() {
		this(DEFAULT_ROWS, DEFAULT_COLUMNS);
	}

	public ChessBoard(int rows, int columns) {
		if (rows <= 0 || columns <= 0) {
			throw new IllegalArgumentException("Board dimensions must be positive");
		}
		this.rows = rows;
		this.columns = columns;
		this.grid = new Piece[rows][columns];
	}

	public int getRows() { return rows; }
	public int getColumns() { return columns; }

	public boolean isInside(int x, int y) {
		return x >= 0 && x < columns && y >= 0 && y < rows;
	}

	public Piece get(int x, int y) {
		if (!isInside(x, y)) {
			return null;
		}
		return grid[y][x];
	}

	public boolean place(Piece piece) {
		if (piece == null || !isInside(piece.getX(), piece.getY())) {
			return false;
		}
		if (grid[piece.getY()][piece.getX()] != null) {
			return false;
		}
		grid[piece.getY()][piece.getX()] = piece;
		return true;
	}

	public boolean move(int fromX, int fromY, int toX, int toY) {
		if (!isInside(fromX, fromY) || !isInside(toX, toY)) {
			return false;
		}
		Piece moving = grid[fromY][fromX];
		if (moving == null || grid[toY][toX] != null) {
			return false;
		}
		grid[fromY][fromX] = null;
		moving.setX(toX);
		moving.setY(toY);
		grid[toY][toX] = moving;
		return true;
	}

	public Piece remove(int x, int y) {
		if (!isInside(x, y)) {
			return null;
		}
		Piece removed = grid[y][x];
		grid[y][x] = null;
		return removed;
	}

	public List<Piece> getPieces() {
		List<Piece> pieces = new ArrayList<>();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				Piece piece = grid[row][col];
				if (piece != null) {
					pieces.add(piece);
				}
			}
		}
		return Collections.unmodifiableList(pieces);
	}

	public void clear() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				grid[row][col] = null;
			}
		}
	}
}
