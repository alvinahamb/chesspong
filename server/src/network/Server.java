package network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import component.*;


import com.chesspong.config.dto.ConfigDTO;
import com.chesspong.config.ejb.ConfigServiceRemote;
import javax.naming.InitialContext;
import javax.naming.Context;
import java.util.Hashtable;

public class Server implements Closeable {
    private final int port;
    private final ExecutorService executor;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private Game game;
    private Thread gameThread;
    private ConfigServiceRemote configService;
    private ConfigDTO config;

    public Server(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        if (running) {
            return;
        }
        serverSocket = new ServerSocket(port);
        running = true;

        // Lookup EJB
        try {
            Hashtable<String, String> jndiProperties = new Hashtable<>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
            jndiProperties.put("jboss.naming.client.ejb.context", "true");
            
            InitialContext ctx = new InitialContext(jndiProperties);
            configService = (ConfigServiceRemote) ctx.lookup("ejb:/config/ConfigService!com.chesspong.config.ejb.ConfigServiceRemote");
            config = configService.getLast();
            if (config == null) {
                System.err.println("No config found, using defaults");
                // Set default config (roi, dame, tour, fou, cavalier, pion, ballDegats, pouvoirBall, atteintePouvoir, pieceNumber)
                config = new ConfigDTO(0, 6, 5, 2, 4, 3, 1, 1, 3, 5, 8);
            }
        } catch (Exception e) {
            System.err.println("Failed to lookup EJB: " + e.getMessage());
            e.printStackTrace();
            // Use default config (roi,dame,tour,fou,cavalier,pion,ballDegats,pouvoirBall,atteintePouvoir,pieceNumber)
            config = new ConfigDTO(0, 6, 5, 2, 4, 3, 1, 1, 3, 5, 8);
        }

        executor.execute(this::acceptLoop);
        System.out.println("Listening on port " + port);
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                executor.execute(() -> handleClient(socket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        System.out.println("Client connected: " + socket.getRemoteSocketAddress());
        try (Socket closeable = socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(closeable.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(closeable.getOutputStream()), true)) {

            writer.println("WELCOME");
            String line;
            while (running && (line = reader.readLine()) != null) {
                System.out.println("Client says: " + line);
                if (line.startsWith("ENTER_GAME ")) {

                    //   String numStr = line.substring(11);
                    // try {
                    //     int numPieces = Integer.parseInt(numStr);
                    //     game = initializeGame(numPieces);
                    //     startGameLoop();
                    //     writer.println("GAME_INITIALIZED " + game.getGameState());
                    // } catch (NumberFormatException e) {
                    //     writer.println("ERROR Invalid number of pieces");
                    // }

                    game = initializeGame();
                    startGameLoop();
                    writer.println("GAME_INITIALIZED " + game.getGameState());
                } else if (line.startsWith("MOVE_PADDLE ")) {
                    String[] parts = line.split(" ");
                    try {
                        int playerIndex = Integer.parseInt(parts[1]);
                        String direction = parts[2];
                        if (direction.equals("LEFT")) {
                            game.movePaddleLeft(playerIndex);
                        } else if (direction.equals("RIGHT")) {
                            game.movePaddleRight(playerIndex);
                        }
                        writer.println("GAME_STATE " + game.getGameState());
                    } catch (Exception e) {
                        writer.println("ERROR Invalid move command");
                    }
                } else if (line.startsWith("START_BALL ")) {
                    String[] parts = line.split(" ");
                    int targetX = Integer.parseInt(parts[1]);
                    int targetY = Integer.parseInt(parts[2]);
                    int ballX = game.getBall().getX();
                    int ballY = game.getBall().getY();
                    int dx = targetX - ballX;
                    int dy = targetY - ballY;
                    double length = Math.sqrt(dx * dx + dy * dy);
                    if (length > 0) {
                        int speed = game.getBall().getSpeed();
                        game.getBall().setDx((int) (dx / length * speed));
                        game.getBall().setDy((int) (dy / length * speed));
                    }
                    writer.println("GAME_STATE " + game.getGameState());
                } else if (line.equals("GET_STATE")) {
                    writer.println("GAME_STATE " + game.getGameState());
                } else if (line.startsWith("SAVE_CONFIG ")) {
                    // Expected format: SAVE_CONFIG roi dame tour fou cavalier pion ballDegats pouvoirBall atteintePouvoir pieceNumber
                    try {
                        String[] parts = line.split(" ");
                            if (parts.length < 11) {
                                writer.println("ERROR Invalid SAVE_CONFIG format");
                            } else {
                                int roi = Integer.parseInt(parts[1]);
                                int dame = Integer.parseInt(parts[2]);
                                int tour = Integer.parseInt(parts[3]);
                                int fou = Integer.parseInt(parts[4]);
                                int cavalier = Integer.parseInt(parts[5]);
                                int pion = Integer.parseInt(parts[6]);
                                int ballDegats = Integer.parseInt(parts[7]);
                                int pouvoirBall = Integer.parseInt(parts[8]);
                                int atteintePouvoir = Integer.parseInt(parts[9]);
                                int pieceNumber = Integer.parseInt(parts[10]);
                                ConfigDTO dto = new ConfigDTO(0, roi, dame, tour, fou, cavalier, pion, ballDegats, pouvoirBall, atteintePouvoir, pieceNumber);
                            try {
                                configService.create(dto);
                                // refresh local config
                                config = configService.getLast();
                                writer.println("OK Config saved");
                            } catch (Exception ex) {
                                writer.println("ERROR Saving config: " + ex.getMessage());
                            }
                        }
                    } catch (NumberFormatException ex) {
                        writer.println("ERROR Invalid numbers in SAVE_CONFIG");
                    }
                } else if (line.equals("LOAD_CONFIG")) {
                    try {
                        ConfigDTO last = configService.getLast();
                        if (last != null) {
                            writer.println("CONFIG " + last.getRoi() + " " + last.getDame() + " " + last.getTour() + " " + last.getFou() + " " + last.getCavalier() + " " + last.getPion() + " " + last.getBallDegats() + " " + last.getPouvoirBall() + " " + last.getAtteintePouvoir() + " " + last.getPieceNumber());
                        } else {
                            writer.println("ERROR No config");
                        }
                    } catch (Exception ex) {
                        writer.println("ERROR Loading config: " + ex.getMessage());
                    }
                } else {
                    writer.println("ECHO " + line);
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Client handler error: " + e.getMessage());
            }
        }
        System.out.println("Client disconnected.");
    }

    private void startGameLoop() {
        if (gameThread != null && gameThread.isAlive()) return;
        gameThread = new Thread(() -> {
            while (running && game != null && !game.isGameOver()) {
                game.tick();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        gameThread.start();
    }

    private int getLives(String type) {
        return switch (type) {
            case "pawn" -> config.getPion();
            case "rook" -> config.getTour();
            case "knight" -> config.getCavalier();
            case "bishop" -> config.getFou();
            case "queen" -> config.getDame();
            case "king" -> config.getRoi();
            default -> 1;
        };
    }

    private Game initializeGame() {
        int numPieces = config.getPieceNumber();
        int pouvoirBall = config.getPouvoirBall();
        int atteintePouvoir = config.getAtteintePouvoir();
        int fieldWidth = 600;
        int fieldHeight = 600;
        int rows = 8;
        int columns = 8;
        ChessBoard board = new ChessBoard(0, 0, fieldWidth, fieldHeight, rows, columns);
        // Define piece types based on numPieces
        String[] pieceTypes;
        switch (numPieces) {
            case 2:
                pieceTypes = new String[]{"king", "queen"};
                break;
            case 4:
                pieceTypes = new String[]{"bishop", "queen", "king", "bishop"};
                break;
            case 6:
                pieceTypes = new String[]{"knight", "bishop", "queen", "king", "bishop", "knight"};
                break;
            case 8:
                pieceTypes = new String[]{"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};
                break;
            default:
                pieceTypes = new String[]{"king", "queen"}; // default to 2
                numPieces = 2;
                break;
        }
        int startCol = (columns - numPieces) / 2;
        // Initialize pieces
        List<Piece> whitePieces = new ArrayList<>();
        // White back rank (y=0)
        for (int i = 0; i < pieceTypes.length; i++) {
            whitePieces.add(new Piece(startCol + i, 0, pieceTypes[i], "white", getLives(pieceTypes[i])));
        }
        // White pawns (y=1)
        for (int i = 0; i < pieceTypes.length; i++) {
            whitePieces.add(new Piece(startCol + i, 1, "pawn", "white", getLives("pawn")));
        }
        List<Piece> blackPieces = new ArrayList<>();
        // Black back rank (y=rows-1)
        for (int i = 0; i < pieceTypes.length; i++) {
            blackPieces.add(new Piece(startCol + i, rows - 1, pieceTypes[i], "black", getLives(pieceTypes[i])));
        }
        // Black pawns (y=rows-2)
        for (int i = 0; i < pieceTypes.length; i++) {
            blackPieces.add(new Piece(startCol + i, rows - 2, "pawn", "black", getLives("pawn")));
        }
        // Place all pieces on board
        for (Piece p : whitePieces) {
            board.place(p);
        }
        for (Piece p : blackPieces) {
            board.place(p);
        }
        Ball ball = new Ball(fieldWidth / 2, fieldHeight / 2, 10, 7, config.getBallDegats());
        List<Player> players = new ArrayList<>();
        int numPlayers = 2; // Fixed to 2 players
        for (int i = 0; i < numPlayers; i++) {
            List<Piece> playerPieces = (i == 0) ? whitePieces : blackPieces;
            Paddle paddle;
            if (i == 0) {
                int paddleY = board.getY() + 2 * (board.getHeight() / rows); // at the end of the second row
                paddle = new Paddle(fieldWidth / 2 - 50, paddleY, 100, 10, 10);
            } else {
                int paddleY = board.getY() + (rows - 2) * (board.getHeight() / rows) - 10; // above the second last row
                paddle = new Paddle(fieldWidth / 2 - 50, paddleY, 100, 10, 10);
            }
            Player player = new Player(i, "Player" + (i+1), playerPieces, paddle);
            players.add(player);
        }
        return new Game(fieldWidth, fieldHeight, board, ball, players, pouvoirBall, atteintePouvoir);
    }

    @Override
    public void close() throws IOException {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
        executor.shutdownNow();
    }
}
