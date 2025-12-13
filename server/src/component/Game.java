package component;

import java.util.Arrays;
import java.util.List;

public class Game {
	private final int fieldWidth;
	private final int fieldHeight;
	private final ChessBoard board;
	private final Ball ball;
	private final List<Player> players;
	private final int[] scores;

	public Game(int fieldWidth, int fieldHeight, ChessBoard board, Ball ball, List<Player> players) {
		if (fieldWidth <= 0 || fieldHeight <= 0) {
			throw new IllegalArgumentException("Field dimensions must be positive");
		}
		if (board == null || ball == null || players == null || players.size() != 2) {
			throw new IllegalArgumentException("Board, ball and two players are required");
		}
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		this.board = board;
		this.ball = ball;
		this.players = players;
		this.scores = new int[] {0, 0};
	}

	public int getFieldWidth() { return fieldWidth; }
	public int getFieldHeight() { return fieldHeight; }
	public ChessBoard getBoard() { return board; }
	public Ball getBall() { return ball; }
	public List<Player> getPlayers() { return players; }
	public int[] getScores() { return Arrays.copyOf(scores, scores.length); }

	public void tick() {
		ball.move();
		ball.clampWithin(fieldWidth, fieldHeight);
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

		if (ball.getDx() > 0) {
			ball.setX(paddleLeft - ball.getRadius());
		} else {
			ball.setX(paddleRight + ball.getRadius());
		}
		ball.bounceHorizontally();
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

	public void resetBallAtCenter(int direction) {
		ball.reset(fieldWidth / 2, fieldHeight / 2, direction);
	}
}
