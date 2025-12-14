package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import network.ClientConnection;
import component.Game;
import com.chesspong.config.dto.ConfigDTO;
import com.chesspong.config.ejb.ConfigServiceRemote;
import javax.naming.InitialContext;
import javax.naming.Context;
import java.util.Hashtable;

public class Choice extends JFrame {

    private JTextField ipField;
    private JButton connectButton;
    private JTextArea logArea;
    private JComboBox<String> playerSelect;
    private JButton entrerJeu, configButton;
    private ClientConnection conn;
    private Game game;
    private Timer updateTimer;
    private ConfigServiceRemote configService;

    public Choice() {
        setTitle("ChessPong Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize EJB lookup
        try {
            Hashtable<String, String> jndiProperties = new Hashtable<>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
            jndiProperties.put("jboss.naming.client.ejb.context", "true");
            
            InitialContext ctx = new InitialContext(jndiProperties);
            configService = (ConfigServiceRemote) ctx.lookup("ejb:/config/ConfigService!com.chesspong.config.ejb.ConfigServiceRemote");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to config server: " + e.getMessage());
        }

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Server IP:"));
        ipField = new JTextField("127.0.0.1", 15);
        topPanel.add(ipField);
        connectButton = new JButton("Connect");
        topPanel.add(connectButton);
        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        connectButton.addActionListener(e -> connect());

        pack();
        setVisible(true);
    }

    private void connect() {
        String host = ipField.getText();
        int port = 5000;
        try {
            conn = new ClientConnection(host, port);
            conn.connect();
            log("Connected to server " + host + ":" + port);

            String welcome = conn.readLine();
            log("Server -> " + welcome);

            conn.sendLine("PING");
            String echo = conn.readLine();
            log("Server -> " + echo);

            // Show game options
            remove(getContentPane().getComponent(0)); // remove topPanel
            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new FlowLayout());
            optionsPanel.add(new JLabel("Nombre de pieces:"));
            playerSelect = new JComboBox<>(new String[]{"2", "4", "6", "8"});
            optionsPanel.add(playerSelect);
            entrerJeu = new JButton("Entrer dans le jeu");
            configButton = new JButton("Configuration");
            optionsPanel.add(entrerJeu);
            optionsPanel.add(configButton);
            add(optionsPanel, BorderLayout.NORTH);
            entrerJeu.addActionListener(e -> enterGame((String) playerSelect.getSelectedItem()));
            configButton.addActionListener(e -> config());
            revalidate();
            repaint();
        } catch (Exception ex) {
            log("Error: " + ex.getMessage());
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }

    public void enterGame(String choice) {
        try {
            // Get piece_number from database
            ConfigDTO lastConfig = configService.getLast();
            int pieceNumber = (lastConfig != null) ? lastConfig.getPieceNumber() : 8; // default to 8 if no config
            
            log("Entering game with " + pieceNumber + " pieces from database");
            conn.sendLine("ENTER_GAME " + pieceNumber);
            String response = conn.readLine();
            log("Server -> " + response);
            if (response.startsWith("GAME_INITIALIZED ")) {
                String state = response.substring(16);
                game = new Game();
                game.parseGameState(state);
                // Switch to game view
                JScrollPane scroll = (JScrollPane) logArea.getParent().getParent();
                remove(scroll);
                add(game.getPanel(), BorderLayout.CENTER);
                game.getPanel().setFocusable(true);
                game.getPanel().requestFocus();
                GameKeyListener listener = new GameKeyListener(conn, game, this::log);
                game.getPanel().addKeyListener(listener);
                game.getPanel().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int x = e.getX();
                        int y = e.getY();
                        int roundedX = Math.round(x / 5.0f) * 5;
                        int roundedY = Math.round(y / 5.0f) * 5;
                        try {
                            conn.sendLine("START_BALL " + roundedX + " " + roundedY);
                            String resp = conn.readLine();
                            if (resp.startsWith("GAME_STATE ")) {
                                String st = resp.substring(11);
                                game.parseGameState(st);
                                game.getPanel().repaint();
                            }
                        } catch (Exception ex) {
                            log("Error: " + ex.getMessage());
                        }
                    }
                });
                updateTimer = new Timer(100, e -> {
                    try {
                        conn.sendLine("GET_STATE");
                        String resp = conn.readLine();
                        if (resp.startsWith("GAME_STATE ")) {
                            String st = resp.substring(11);
                            game.parseGameState(st);
                            game.getPanel().repaint();
                        }
                    } catch (Exception ex) {
                        log("Error updating state: " + ex.getMessage());
                    }
                });
                updateTimer.start();
                revalidate();
                repaint();
            }
        } catch (Exception ex) {
            log("Error: " + ex.getMessage());
        }
    }

    private void config() {
        log("Configuration button clicked");
        new Configuration();
    }
}
