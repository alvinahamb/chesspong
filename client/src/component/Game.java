package component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    private int fieldWidth, fieldHeight;
    private int ballX, ballY, ballRadius;
    private int pouvoirBall = 0;
    private int pouvoirBallEnCours = 0;
    private int atteintePouvoir = 0;
    private int progressPouvoir = 0;
    private boolean estAtteint = false;
    private List<int[]> paddles = new ArrayList<>();
    private List<PieceData> pieces = new ArrayList<>();
    private GamePanel panel;
    private Set<Integer> columnsWithPieces = new HashSet<>();
    private Set<Integer> initialColumnsWithPieces = new HashSet<>();

    public Game() {
        panel = new GamePanel();
        panel.setPreferredSize(new Dimension(600, 600));
    }

    public void parseGameState(String state) {
        String[] parts = state.split("\\|");
        String[] gameParts = parts[0].trim().split("\\s+");
        fieldWidth = Integer.parseInt(gameParts[0]);
        fieldHeight = Integer.parseInt(gameParts[1]);
        ballX = Integer.parseInt(gameParts[2]);
        ballY = Integer.parseInt(gameParts[3]);
        ballRadius = Integer.parseInt(gameParts[4]);
        paddles.clear();
        int numPlayers = (gameParts.length - 5) / 4;
        for (int i = 0; i < numPlayers; i++) {
            int[] pad = new int[4];
            pad[0] = Integer.parseInt(gameParts[5 + i*4]);
            pad[1] = Integer.parseInt(gameParts[6 + i*4]);
            pad[2] = Integer.parseInt(gameParts[7 + i*4]);
            pad[3] = Integer.parseInt(gameParts[8 + i*4]);
            paddles.add(pad);
        }
        pieces.clear();
        columnsWithPieces.clear();
        if (parts.length > 1) {
            String[] pieceStrs = parts[1].trim().split(";");
            for (String pStr : pieceStrs) {
                if (pStr.trim().isEmpty()) continue;
                String[] pParts = pStr.trim().split("\\s+");
                PieceData pd = new PieceData();
                pd.x = Integer.parseInt(pParts[0]);
                pd.y = Integer.parseInt(pParts[1]);
                pd.type = pParts[2];
                pd.color = pParts[3];
                pd.lives = Integer.parseInt(pParts[4]);
                pd.actualLives = Integer.parseInt(pParts[5]);
                pieces.add(pd);
                columnsWithPieces.add(pd.x);
            }
        }
        // parse pouvoir info if present (server appends after another '|')
        if (parts.length > 2) {
            try {
                String[] powParts = parts[2].trim().split("\\s+");
                if (powParts.length >= 5) {
                    pouvoirBall = Integer.parseInt(powParts[0]);
                    pouvoirBallEnCours = Integer.parseInt(powParts[1]);
                    atteintePouvoir = Integer.parseInt(powParts[2]);
                    progressPouvoir = Integer.parseInt(powParts[3]);
                    estAtteint = Integer.parseInt(powParts[4]) != 0;
                }
            } catch (Exception ex) {
                // ignore parse errors and keep defaults
            }
        }
        if (initialColumnsWithPieces.isEmpty() && !columnsWithPieces.isEmpty()) {
            initialColumnsWithPieces.addAll(columnsWithPieces);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    private static class PieceData {
        int x, y, lives, actualLives;
        String type, color;
    }

    private class GamePanel extends JPanel {
        private ImageIcon pawnWhite, rookWhite, knightWhite, bishopWhite, queenWhite, kingWhite;
        private ImageIcon pawnBlack, rookBlack, knightBlack, bishopBlack, queenBlack, kingBlack;

        public GamePanel() {
            loadImages();
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    int roundedX = Math.round(x / 5.0f) * 5;
                    int roundedY = Math.round(y / 5.0f) * 5;
                    System.out.println("Clicked at rounded: " + roundedX + ", " + roundedY);
                }

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {}

                @Override
                public void mouseEntered(MouseEvent e) {}

                @Override
                public void mouseExited(MouseEvent e) {}
            });
        }

        private void loadImages() {
            try {
                pawnWhite = new ImageIcon("img/pionwhite.png");
                rookWhite = new ImageIcon("img/tourwhite.png");
                knightWhite = new ImageIcon("img/cavalierwhite.png");
                bishopWhite = new ImageIcon("img/fouwhite.png");
                queenWhite = new ImageIcon("img/reinewhite.png");
                kingWhite = new ImageIcon("img/roiwhite.png");
                pawnBlack = new ImageIcon("img/pionblack.png");
                rookBlack = new ImageIcon("img/tourblack.png");
                knightBlack = new ImageIcon("img/cavalierblack.png");
                bishopBlack = new ImageIcon("img/foublack.png");
                queenBlack = new ImageIcon("img/reineblack.png");
                kingBlack = new ImageIcon("img/roiblack.png");
            } catch (Exception e) {
                // Images not found, will use colored squares
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int squareSize = fieldWidth / 8;
            // Draw chessboard only in columns with pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (initialColumnsWithPieces.contains(col)) {
                        int x = col * squareSize;
                        int y = row * squareSize;
                        Color color = (row + col) % 2 == 0 ? Color.WHITE : Color.BLACK;
                        g.setColor(color);
                        g.fillRect(x, y, squareSize, squareSize);
                    }
                }
            }
            // Draw ball
            g.setColor(Color.RED);
            g.fillOval(ballX - ballRadius, ballY - ballRadius, ballRadius * 2, ballRadius * 2);
            // Draw paddles
            g.setColor(Color.BLUE);
            for (int[] pad : paddles) {
                g.fillRect(pad[0], pad[1], pad[2], pad[3]);
            }
            // Draw pieces
            for (PieceData p : pieces) {
                int x = p.x * squareSize;
                int y = p.y * squareSize;
                ImageIcon icon = getPieceIcon(p.type, p.color);
                if (icon != null && icon.getImage() != null) {
                    g.drawImage(icon.getImage(), x, y, squareSize, squareSize, this);
                } else {
                    // Fallback to colored square
                    g.setColor(p.color.equals("white") ? Color.WHITE : Color.BLACK);
                    g.fillRect(x, y, squareSize, squareSize);
                }
                // Draw lives
                g.setColor(Color.RED);
                g.drawString(p.actualLives + "/" + p.lives, x + squareSize / 2 - 10, y + squareSize / 2 + 5);
            }
            // Draw field boundary
            g.setColor(Color.GREEN);
            g.drawRect(0, 0, fieldWidth - 1, fieldHeight - 1);

            // Draw power progress bar and info (top-right)
            int barWidth = 200;
            int barHeight = 18;
            int margin = 10;
            int barX = Math.max(margin, fieldWidth - barWidth - margin);
            int barY = margin;
            // Background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);
            // Filled portion based on progressPouvoir / atteintePouvoir
            int denom = Math.max(1, atteintePouvoir);
            int fill = (int) (Math.max(0, Math.min(1.0, (double) progressPouvoir / denom)) * barWidth);
            g.setColor(Color.GREEN);
            g.fillRect(barX, barY, fill, barHeight);
            // Border
            g.setColor(Color.BLACK);
            g.drawRect(barX, barY, barWidth, barHeight);
            // Text info below bar
            g.setColor(Color.WHITE);
            String line1 = "Pouvoir: " + pouvoirBall + "  EnCours: " + pouvoirBallEnCours;
            String line2 = "Progress: " + progressPouvoir + "/" + atteintePouvoir + "  Atteint: " + (estAtteint ? "Oui" : "Non");
            g.drawString(line1, barX, barY + barHeight + 15);
            g.drawString(line2, barX, barY + barHeight + 30);
        }

        private ImageIcon getPieceIcon(String type, String color) {
            boolean isWhite = "white".equals(color);
            switch (type) {
                case "pawn": return isWhite ? pawnWhite : pawnBlack;
                case "rook": return isWhite ? rookWhite : rookBlack;
                case "knight": return isWhite ? knightWhite : knightBlack;
                case "bishop": return isWhite ? bishopWhite : bishopBlack;
                case "queen": return isWhite ? queenWhite : queenBlack;
                case "king": return isWhite ? kingWhite : kingBlack;
                default: return null;
            }
        }
    }
}