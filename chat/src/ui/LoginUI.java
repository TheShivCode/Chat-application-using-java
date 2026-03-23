package com.chatapp.ui;

import com.chatapp.client.ClientConnection;
import com.chatapp.common.Message;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI {

    private final JFrame frame = new JFrame("Messenger — Login");
    private final JTextField userField = new JTextField();
    private final JPasswordField passField = new JPasswordField();
    private final JLabel status = new JLabel(" ");
    private ClientConnection conn;
    private final Gson gson = new Gson();

    public LoginUI() {

        frame.setSize(420, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Welcome to Messenger");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login or create a new account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(title);
        main.add(Box.createVerticalStrut(4));
        main.add(subtitle);
        main.add(Box.createVerticalStrut(20));

        JLabel uL = new JLabel("Username");
        uL.setFont(new Font("SansSerif", Font.PLAIN, 14));
        main.add(uL);

        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        userField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        main.add(userField);
        main.add(Box.createVerticalStrut(15));

        JLabel pL = new JLabel("Password");
        pL.setFont(new Font("SansSerif", Font.PLAIN, 14));
        main.add(pL);

        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        main.add(passField);
        main.add(Box.createVerticalStrut(15));

        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setForeground(Color.WHITE);

        registerBtn.setBackground(new Color(76, 175, 80));
        registerBtn.setForeground(Color.WHITE);

        btns.add(loginBtn);
        btns.add(registerBtn);

        main.add(btns);
        main.add(Box.createVerticalStrut(10));
        main.add(status);

        frame.add(main);
        frame.setVisible(true);

        // prepare registry callback: when auth OK arrives, open chat with same connection
        ChatUIRegistry.setLoginCallback((username, connection) -> {
            SwingUtilities.invokeLater(() -> {
                new ChatUI(username, connection);
                frame.dispose();
            });
        });

        loginBtn.addActionListener(e -> authenticate("login"));
        registerBtn.addActionListener(e -> authenticate("register"));
        passField.addActionListener(e -> authenticate("login"));
    }

    private void authenticate(String mode) {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (user.isEmpty()) { status.setText("Enter username"); return; }

        try {
            conn = new ClientConnection();
            conn.connect("127.0.0.1", 8080);
            // register connection in registry
            ChatUIRegistry.setConnection(conn);

            Message msg = new Message("auth", user, mode, pass, System.currentTimeMillis());
            conn.send(msg);
            status.setText(mode.equals("login") ? "Logging in..." : "Registering...");
        } catch (Exception ex) {
            status.setText("Connection failed: " + ex.getMessage());
        }
    }
}
