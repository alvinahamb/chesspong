package ui;

import com.chesspong.config.dto.ConfigDTO;
import com.chesspong.config.ejb.ConfigServiceRemote;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

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
    private ConfigServiceRemote configService;

    public Configuration() {
        setTitle("Configuration");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Lookup EJB
        try {
            Hashtable<String, String> jndiProperties = new Hashtable<>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
            jndiProperties.put("jboss.naming.client.ejb.context", "true");
            
            InitialContext ctx = new InitialContext(jndiProperties);
            // JNDI format: ejb:/<module-name>/<bean-name>!<interface-name>
            configService = (ConfigServiceRemote) ctx.lookup("ejb:/config/ConfigService!com.chesspong.config.ejb.ConfigServiceRemote");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + e.getMessage());
        }

        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // IP input
        panel.add(new JLabel("IP:"));
        ipField = new JTextField();
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
        Choice choice = new Choice();
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

            ConfigDTO config = new ConfigDTO(0, roi, dame, tour, fou, cavalier, pion, ballDegats, pieceNumber);
            configService.create(config);
            JOptionPane.showMessageDialog(this, "Configuration saved!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving configuration.");
        }
    }

    private void loadLastConfiguration() {
        try {
            ConfigDTO last = configService.getLast();
            if (last != null) {
                roiField.setText(String.valueOf(last.getRoi()));
                reineField.setText(String.valueOf(last.getDame()));
                tourField.setText(String.valueOf(last.getTour()));
                fouField.setText(String.valueOf(last.getFou()));
                cavalierField.setText(String.valueOf(last.getCavalier()));
                pionField.setText(String.valueOf(last.getPion()));
                degatField.setText(String.valueOf(last.getBallDegats()));
                nombrePieceField.setText(String.valueOf(last.getPieceNumber()));
            } else {
                JOptionPane.showMessageDialog(this, "No configuration found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading configuration.");
        }
    }
}