package ui;

import network.ClientConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Configuration extends JFrame {

    private JTextField ipField;
    private JTextField roiField;
    private JTextField reineField;
    private JTextField fouField;
    private JTextField cavalierField;
    private JTextField tourField;
    private JTextField pionField;
    private JTextField degatField;
    private JTextField nombrePieceField;
    private JButton okButton;
    private JButton loadButton;

    public Configuration() {
        setTitle("Configuration");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // IP input
        panel.add(new JLabel("IP:"));
        ipField = new JTextField("127.0.0.1");
        panel.add(ipField);

        // Roi
        panel.add(new JLabel("Roi:"));
        roiField = new JTextField();
        panel.add(roiField);

        // Reine
        panel.add(new JLabel("Reine:"));
        reineField = new JTextField();
        panel.add(reineField);

        // Fou
        panel.add(new JLabel("Fou:"));
        fouField = new JTextField();
        panel.add(fouField);

        // Cavalier
        panel.add(new JLabel("Cavalier:"));
        cavalierField = new JTextField();
        panel.add(cavalierField);

        // Tour
        panel.add(new JLabel("Tour:"));
        tourField = new JTextField();
        panel.add(tourField);

        // Pion
        panel.add(new JLabel("Pion:"));
        pionField = new JTextField();
        panel.add(pionField);

        // Degat de balle
        panel.add(new JLabel("Degat de balle:"));
        degatField = new JTextField();
        panel.add(degatField);

        // Nombre de piece
        panel.add(new JLabel("Nombre de piece:"));
        nombrePieceField = new JTextField();
        panel.add(nombrePieceField);

        // Buttons
        panel.add(new JLabel("")); // empty for alignment
        JPanel buttonPanel = new JPanel(new FlowLayout());
        okButton = new JButton("Confirm");
        loadButton = new JButton("Load Last");
        buttonPanel.add(okButton);
        buttonPanel.add(loadButton);
        panel.add(buttonPanel);

        // Action listeners
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveConfiguration();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadLastConfiguration();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void saveConfiguration() {
        try {
            int roi = Integer.parseInt(roiField.getText());
            int dame = Integer.parseInt(reineField.getText());
            int tour = Integer.parseInt(tourField.getText());
            int fou = Integer.parseInt(fouField.getText());
            int cavalier = Integer.parseInt(cavalierField.getText());
            int pion = Integer.parseInt(pionField.getText());
            int ballDegats = Integer.parseInt(degatField.getText());
            int pieceNumber = Integer.parseInt(nombrePieceField.getText());

            String host = ipField.getText().isEmpty() ? "127.0.0.1" : ipField.getText();
            int port = 5000;
            try (ClientConnection conn = new ClientConnection(host, port)) {
                conn.connect();
                // read welcome
                try { conn.readLine(); } catch (Exception ignored) {}
                String cmd = String.format("SAVE_CONFIG %d %d %d %d %d %d %d %d", roi, dame, tour, fou, cavalier, pion, ballDegats, pieceNumber);
                conn.sendLine(cmd);
                String resp = conn.readLine();
                if (resp != null && resp.startsWith("OK")) {
                    JOptionPane.showMessageDialog(this, "Configuration saved on server.");
                } else if (resp != null) {
                    JOptionPane.showMessageDialog(this, "Server response: " + resp);
                } else {
                    JOptionPane.showMessageDialog(this, "No response from server.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving configuration: " + ex.getMessage());
        }
    }

    private void loadLastConfiguration() {
        String host = ipField.getText().isEmpty() ? "127.0.0.1" : ipField.getText();
        int port = 5000;
        try (ClientConnection conn = new ClientConnection(host, port)) {
            conn.connect();
            // read welcome
            try { conn.readLine(); } catch (Exception ignored) {}
            conn.sendLine("LOAD_CONFIG");
            String resp = conn.readLine();
            if (resp == null) {
                JOptionPane.showMessageDialog(this, "No response from server.");
                return;
            }
            if (resp.startsWith("CONFIG ")) {
                String[] parts = resp.split(" ");
                if (parts.length >= 9) {
                    roiField.setText(parts[1]);
                    reineField.setText(parts[2]);
                    tourField.setText(parts[3]);
                    fouField.setText(parts[4]);
                    cavalierField.setText(parts[5]);
                    pionField.setText(parts[6]);
                    degatField.setText(parts[7]);
                    nombrePieceField.setText(parts[8]);
                    JOptionPane.showMessageDialog(this, "Configuration loaded from server.");
                } else {
                    JOptionPane.showMessageDialog(this, "Malformed CONFIG response.");
                }
            } else if (resp.startsWith("ERROR")) {
                JOptionPane.showMessageDialog(this, resp);
            } else {
                JOptionPane.showMessageDialog(this, "Server response: " + resp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading configuration: " + ex.getMessage());
        }
    }
}