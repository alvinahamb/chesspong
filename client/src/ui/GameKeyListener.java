package ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import network.ClientConnection;
import component.Game;

public class GameKeyListener extends KeyAdapter {
    private ClientConnection conn;
    private Game game;
    private Consumer<String> logger;

    public GameKeyListener(ClientConnection conn, Game game, Consumer<String> logger) {
        this.conn = conn;
        this.game = game;
        this.logger = logger;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        try {
            if (key == 'q' || key == 'Q') {
                conn.sendLine("MOVE_PADDLE 0 LEFT");
                String response = conn.readLine();
                if (response.startsWith("GAME_STATE ")) {
                    String state = response.substring(11);
                    game.parseGameState(state);
                    game.getPanel().repaint();
                }
            } else if (key == 'd' || key == 'D') {
                conn.sendLine("MOVE_PADDLE 0 RIGHT");
                String response = conn.readLine();
                if (response.startsWith("GAME_STATE ")) {
                    String state = response.substring(11);
                    game.parseGameState(state);
                    game.getPanel().repaint();
                }
            } else if (key == 'k' || key == 'K') {
                conn.sendLine("MOVE_PADDLE 1 LEFT");
                String response = conn.readLine();
                if (response.startsWith("GAME_STATE ")) {
                    String state = response.substring(11);
                    game.parseGameState(state);
                    game.getPanel().repaint();
                }
            } else if (key == 'm' || key == 'M') {
                conn.sendLine("MOVE_PADDLE 1 RIGHT");
                String response = conn.readLine();
                if (response.startsWith("GAME_STATE ")) {
                    String state = response.substring(11);
                    game.parseGameState(state);
                    game.getPanel().repaint();
                }
            }
        } catch (Exception ex) {
            logger.accept("Error: " + ex.getMessage());
        }
    }
}