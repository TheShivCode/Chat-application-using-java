package com.chatapp.ui;

import com.chatapp.client.ClientConnection;
import com.chatapp.common.Message;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatUI {
    private final JFrame frame;
    private final JPanel messagesPanel;
    private final JTextField input;
    private final ClientConnection conn;
    private final String username;
    private final JLabel typingLabel;
    private static final Gson gson = new Gson();
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("hh:mm a");

    public ChatUI(String username, ClientConnection conn) {
        this.username = username;
        this.conn = conn;

        frame = new JFrame("Messenger — " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem mExit = new JMenuItem("Exit");
        mExit.addActionListener(e -> frame.dispose());
        menuFile.add(mExit);
        menuBar.add(menuFile);
        frame.setJMenuBar(menuBar);

        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(260, 0));
        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JPanel searchBar = new JPanel(new BorderLayout(6, 6));
        searchBar.setBorder(new EmptyBorder(8, 8, 8, 8));
        JTextField search = new JTextField();
        JButton newChatBtn = new JButton("New Chat");
        newChatBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "New Chat (demo)"));
        searchBar.add(search, BorderLayout.CENTER);
        searchBar.add(newChatBtn, BorderLayout.EAST);

        DefaultListModel<String> contactsModel = new DefaultListModel<>();
        contactsModel.addElement("Friends (group)");
        contactsModel.addElement("Alice");
        contactsModel.addElement("Bob");
        contactsModel.addElement("Project Team");
        contactsModel.addElement("Support (fake)");
        JList<String> contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setFixedCellHeight(40);
        contactsList.setBorder(new EmptyBorder(6, 6, 6, 6));
        contactsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contactsList.setSelectedIndex(1);

        JPanel leftBottom = new JPanel(new GridLayout(1, 2, 6, 6));
        leftBottom.setBorder(new EmptyBorder(8, 8, 8, 8));
        leftBottom.add(new JButton("Profile"));
        leftBottom.add(new JButton("Settings"));

        left.add(searchBar, BorderLayout.NORTH);
        left.add(new JScrollPane(contactsList), BorderLayout.CENTER);
        left.add(leftBottom, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(8, 8, 8, 8));
        JLabel contactTitle = new JLabel("Chat — " + contactsList.getSelectedValue());
        contactTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        typingLabel = new JLabel(" ");
        typingLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        typingLabel.setForeground(Color.GRAY);
        topBar.add(contactTitle, BorderLayout.WEST);
        topBar.add(typingLabel, BorderLayout.SOUTH);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);
        JScrollPane messagesScroll = new JScrollPane(messagesPanel);
        messagesScroll.setBorder(null);
        messagesScroll.getVerticalScrollBar().setUnitIncrement(16);

        input = new JTextField();
        input.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton attachBtn = new JButton("\uD83D\uDCCE");
        attachBtn.setToolTipText("Attach (demo)");
        attachBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Attach file (demo)"));
        JButton emojiBtn = new JButton("\uD83D\uDE03");
        emojiBtn.setToolTipText("Emoji (demo)");
        emojiBtn.addActionListener(e -> { input.setText(input.getText() + " 🙂"); input.requestFocus(); });
        JButton sendBtn = new JButton("Send");
        sendBtn.setPreferredSize(new Dimension(90, 40));
        sendBtn.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout(8,8));
        inputPanel.setBorder(new EmptyBorder(8,8,8,8));
        inputPanel.add(attachBtn, BorderLayout.WEST);

        JPanel mid = new JPanel(new BorderLayout(6,6));
        mid.add(input, BorderLayout.CENTER);
        JPanel rightBtns = new JPanel(new BorderLayout(6,6));
        rightBtns.add(emojiBtn, BorderLayout.WEST);
        rightBtns.add(sendBtn, BorderLayout.EAST);
        mid.add(rightBtns, BorderLayout.EAST);
        inputPanel.add(mid, BorderLayout.CENTER);

        center.add(topBar, BorderLayout.NORTH);
        center.add(messagesScroll, BorderLayout.CENTER);
        center.add(inputPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, center);
        split.setDividerLocation(260);
        frame.getContentPane().add(split);

        input.addActionListener(e -> sendMessage());

        input.getDocument().addDocumentListener(new SimpleTypingListener(() -> {
            setTypingIndicator("typing...");
            sendTypingSignalToServer();
            new Timer(2000, evt -> { setTypingIndicator(""); ((Timer)evt.getSource()).stop(); }).start();
        }));

        contactsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    contactTitle.setText("Chat — " + contactsList.getSelectedValue());
                    appendSystemBubble("Opened chat with " + contactsList.getSelectedValue());
                    messagesPanel.revalidate();
                }
            }
        });

        frame.setVisible(true);
        ChatUIRegistry.register(this);
        appendSystemBubble("Welcome, " + username + "! This is a polished demo chat UI.");
    }

    private void sendMessage() {
        String txt = input.getText().trim();
        if (txt.isEmpty()) return;
        try {
            Message m = new Message("chat", username, "all", txt, System.currentTimeMillis());
            conn.send(m);
            appendLocalBubble(txt);
            input.setText("");
        } catch (Exception e) {
            appendSystemBubble("Failed to send: " + e.getMessage());
        }
    }

    private void appendLocalBubble(String text) {
        SwingUtilities.invokeLater(() -> {
            JPanel bubble = makeBubble(text, true);
            messagesPanel.add(bubble);
            messagesPanel.add(Box.createVerticalStrut(8));
            messagesPanel.revalidate();
            scrollToBottom();
        });
    }

    private void appendRemoteBubble(String from, String text) {
        SwingUtilities.invokeLater(() -> {
            JPanel bubble = makeBubble(from + ": " + text, false);
            messagesPanel.add(bubble);
            messagesPanel.add(Box.createVerticalStrut(8));
            messagesPanel.revalidate();
            scrollToBottom();
        });
    }

    private void appendSystemBubble(String text) {
        SwingUtilities.invokeLater(() -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel l = new JLabel(text);
            l.setFont(new Font("SansSerif", Font.ITALIC, 12));
            l.setForeground(Color.DARK_GRAY);
            p.add(l);
            p.setOpaque(false);
            messagesPanel.add(p);
            messagesPanel.add(Box.createVerticalStrut(6));
            messagesPanel.revalidate();
            scrollToBottom();
        });
    }

    private JPanel makeBubble(String text, boolean isLocal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JTextArea ta = new JTextArea(text);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        ta.setBackground(isLocal ? new Color(220, 248, 198) : new Color(240, 240, 240));
        ta.setOpaque(true);

        JLabel time = new JLabel(TIME_FMT.format(new Date()));
        time.setFont(new Font("SansSerif", Font.PLAIN, 10));
        time.setForeground(Color.DARK_GRAY);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(ta, BorderLayout.CENTER);
        right.add(time, BorderLayout.SOUTH);

        if (isLocal) panel.add(right, BorderLayout.EAST); else panel.add(right, BorderLayout.WEST);
        return panel;
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, messagesPanel);
            if (sp != null) {
                JViewport v = sp.getViewport();
                v.setViewPosition(new Point(0, Math.max(0, messagesPanel.getPreferredSize().height - v.getExtentSize().height)));
            }
        });
    }

    private void setTypingIndicator(String text) {
        SwingUtilities.invokeLater(() -> typingLabel.setText(text));
    }

    private void sendTypingSignalToServer() {
        try {
            Message t = new Message("typing", username, "all", "typing", System.currentTimeMillis());
            conn.send(t);
        } catch (Exception ignored) {}
    }

    public static void onServerMessage(String json) {
        try {
            Message m = gson.fromJson(json, Message.class);
            if (m == null) return;

            if ("auth".equals(m.type)) {
                if ("LOGIN_OK".equals(m.body) || "REGISTER_OK".equals(m.body)) {
                    ChatUIRegistry.onLoginSuccess(m.to);
                    return;
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, m.body));
                    return;
                }
            }

            if ("typing".equals(m.type)) {
                ChatUI ui = ChatUIRegistry.getActive();
                if (ui != null) ui.setTypingIndicator(m.from + " is typing...");
                new Timer(1500, evt -> {
                    ChatUI ui2 = ChatUIRegistry.getActive();
                    if (ui2 != null) ui2.setTypingIndicator("");
                    ((Timer) evt.getSource()).stop();
                }).start();
                return;
            }

            if ("chat".equals(m.type)) {
                ChatUI ui = ChatUIRegistry.getActive();
                if (ui != null) ui.appendRemoteBubble(m.from, m.body);
                return;
            }

            ChatUI ui = ChatUIRegistry.getActive();
            if (ui != null) ui.appendSystemBubble((m.from == null ? "srv" : m.from) + ": " + m.body);

        } catch (Exception ignored) {}
    }

    // debounce typing listener
    private static class SimpleTypingListener implements javax.swing.event.DocumentListener {
        private final Runnable onTyping;
        private Timer debounce;

        SimpleTypingListener(Runnable onTyping) {
            this.onTyping = onTyping;
            debounce = new Timer(500, e -> {});
            debounce.setRepeats(false);
        }

        private void fire() {
            onTyping.run();
            if (debounce.isRunning()) debounce.restart(); else debounce.start();
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) { fire(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { fire(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { fire(); }
    }
}

