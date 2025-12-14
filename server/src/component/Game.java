package component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
	private final int fieldWidth;
	private final int fieldHeight;
	private final ChessBoard board;
	private final Ball ball;
	private final List<Player> players;
	private final int[] scores;
	private final int minPaddleX;
	private final int maxPaddleX;
	private boolean gameOver = false;

	public Game(int fieldWidth, int fieldHeight, ChessBoard board, Ball ball, List<Player> players) {
		if (fieldWidth <= 0 || fieldHeight <= 0) {
			throw new IllegalArgumentException("Field dimensions must be positive");
		}
		if (board == null || ball == null || players == null || players.isEmpty()) {
			throw new IllegalArgumentException("Board, ball and players are required");
		}
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		this.board = board;
		this.ball = ball;
		this.players = players;
		this.scores = new int[players.size()];
		// Calculate paddle boundaries based on piece columns
		List<Piece> allPieces = board.getPieces();
		int minCol = Integer.MAX_VALUE;
		int maxCol = Integer.MIN_VALUE;
		for (Piece p : allPieces) {
			minCol = Math.min(minCol, p.getX());
			maxCol = Math.max(maxCol, p.getX());
		}
		int squareSize = fieldWidth / 8;
		this.minPaddleX = minCol * squareSize;
		this.maxPaddleX = (maxCol + 1) * squareSize - 100; // assuming paddle width 100
	}

	public int getFieldWidth() { return fieldWidth; }
	public int getFieldHeight() { return fieldHeight; }
	public ChessBoard getBoard() { return board; }
	public Ball getBall() { return ball; }
	public List<Player> getPlayers() { return players; }
	public int[] getScores() { return Arrays.copyOf(scores, scores.length); }
	public boolean isGameOver() { return gameOver; }

	public void tick() {
		ball.move();
		for (Player p : players) {
			handlePaddleCollision(p.getPaddle());
		}
		ball.clampWithin(minPaddleX, maxPaddleX + 100, 0, fieldHeight);
		// Check piece collisions
		int squareSize = fieldWidth / 8;
		int margin = 10; // reduce piece space
		for (Piece piece : new ArrayList<>(board.getPieces())) {
			int left = piece.getX() * squareSize + margin;
			int right = (piece.getX() + 1) * squareSize - margin;
			int top = piece.getY() * squareSize + margin;
			int bottom = (piece.getY() + 1) * squareSize - margin;
			int ballLeft = ball.getX() - ball.getRadius();
			int ballRight = ball.getX() + ball.getRadius();
			int ballTop = ball.getY() - ball.getRadius();
			int ballBottom = ball.getY() + ball.getRadius();
			if (ballRight >= left && ballLeft <= right && ballBottom >= top && ballTop <= bottom) {
				int damage = ball.getDegat();
				piece.setActualLives(piece.getActualLives() - damage);
				ball.bounceVertically();
				if (piece.getActualLives() <= 0) {
					board.remove(piece.getX(), piece.getY());
					if (piece.getType().equals("king")) {
						gameOver = true;
					}
				}
			}
		}
	}

	public boolean handlePaddleCollision(Paddle paddle) {
		int ballLeft = ball.getX() - ball.getRadius();
		int ballRight = ball.getX() + ball.getRadius();
		int ballTop = ball.getY() - ball.getRadius();
		int ballBottom = ball.getY() + ball.getRadius();

		int paddleLeft = paddle.getX();
		int paddleRight = paddle.getX() + paddle.getWidth();
		int paddleTop = paddle.getY();
		int paddleBottom = paddle.getY() + paddle.getHeight();

		boolean overlapping = ballRight >= paddleLeft && ballLeft <= paddleRight
				&& ballBottom >= paddleTop && ballTop <= paddleBottom;

		if (!overlapping) {
			return false;
		}

		if (ball.getDy() > 0) {
			ball.setY(paddleTop - ball.getRadius());
		} else {
			ball.setY(paddleBottom + ball.getRadius());
		}
		ball.bounceVertically();
		return true;
	}

	public void scoreForPlayer(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= scores.length) {
			throw new IllegalArgumentException("Invalid player index");
		}
		scores[playerIndex] += 1;
	}

	public Player getWinner(int targetScore) {
		if (scores[0] >= targetScore) {
			return players.get(0);
		}
		if (scores[1] >= targetScore) {
			return players.get(1);
		}
		return null;
	}

	public void movePaddleLeft(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= players.size()) return;
		Player player = players.get(playerIndex);
		Paddle paddle = player.getPaddle();
		paddle.moveLeft(minPaddleX, maxPaddleX);
	}

	public void movePaddleRight(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= players.size()) return;
		Player player = players.get(playerIndex);
		Paddle paddle = player.getPaddle();
		paddle.moveRight(minPaddleX, maxPaddleX);
	}

	public String getGameState() {
		StringBuilder sb = new StringBuilder();
		sb.append(fieldWidth).append(" ").append(fieldHeight).append(" ");
		sb.append(ball.getX()).append(" ").append(ball.getY()).append(" ").append(ball.getRadius()).append(" ");
		for (Player p : players) {
			Paddle pad = p.getPaddle();
			sb.append(pad.getX()).append(" ").append(pad.getY()).append(" ").append(pad.getWidth()).append(" ").append(pad.getHeight()).append(" ");
		}
		sb.append("| ");
		for (Piece piece : board.getPieces()) {
			sb.append(piece.getX()).append(" ").append(piece.getY()).append(" ").append(piece.getType()).append(" ").append(piece.getColor()).append(" ").append(piece.getLives()).append(" ").append(piece.getActualLives()).append(" ; ");
		}
		return sb.toString().trim();
	}
}
