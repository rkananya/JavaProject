import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class PlayPage extends JPanel {
    
    // Database connection
    private Connection conn;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/reflect_app";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root"; 
    
    // UI Navigation
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private AppLauncher launcher;
    
    // User data
    private int userId;
    private String username;
    private int totalScore;
    private int gamesPlayed;
    private int bestScore;
    
    // Game state
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int gameScore = 0;
    private int correctCount = 0;
    private int timeLeft = 15;
    private javax.swing.Timer gameTimer;
    private long startTime;
    
    // Game UI components
    private JLabel questionLabel;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel progressLabel;
    private JButton[] optionButtons = new JButton[4];
    
    // ==================== CONSTRUCTOR ====================
    
    public PlayPage(AppLauncher launcher) {
        this.launcher = launcher;
        connectDatabase();
        
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(15, 23, 42));
        
        cardPanel.add(createWelcomeScreen(), "WELCOME");
        cardPanel.add(createGameScreen(), "GAME");
        cardPanel.add(createResultScreen(), "RESULT");
        cardPanel.add(createLeaderboardScreen(), "LEADERBOARD");
        
        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "WELCOME");
    }
    
    // ==================== DATABASE ====================
    
    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✅ Quiz Database connected!");
        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }
    
    private void getOrCreateUser(String name) {
        try {
            // Check if user exists
            String sql = "SELECT * FROM quiz_users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                userId = rs.getInt("id");
                username = rs.getString("username");
                totalScore = rs.getInt("total_score");
                gamesPlayed = rs.getInt("games_played");
                bestScore = rs.getInt("best_score");
            } else {
                // Create new user
                sql = "INSERT INTO quiz_users (username) VALUES (?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, name);
                stmt.executeUpdate();
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                    username = name;
                    totalScore = 0;
                    gamesPlayed = 0;
                    bestScore = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private List<QuizQuestion> loadQuestions() {
        List<QuizQuestion> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM quiz_questions ORDER BY RAND() LIMIT 10";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new QuizQuestion(
                    rs.getInt("id"),
                    rs.getString("question"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct").charAt(0),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private void updateUserStats() {
        try {
            boolean isNewBest = gameScore > bestScore;
            String sql = "UPDATE quiz_users SET total_score = total_score + ?, games_played = games_played + 1";
            if (isNewBest) {
                sql += ", best_score = ?";
            }
            sql += " WHERE id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameScore);
            if (isNewBest) {
                stmt.setInt(2, gameScore);
                stmt.setInt(3, userId);
            } else {
                stmt.setInt(2, userId);
            }
            stmt.executeUpdate();
            
            // Update local variables
            totalScore += gameScore;
            gamesPlayed++;
            if (isNewBest) bestScore = gameScore;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private List<String[]> getLeaderboard() {
        List<String[]> board = new ArrayList<>();
        try {
            String sql = "SELECT username, best_score, games_played FROM quiz_users ORDER BY best_score DESC LIMIT 10";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int rank = 1;
            while (rs.next()) {
                board.add(new String[]{
                    String.valueOf(rank++),
                    rs.getString("username"),
                    String.valueOf(rs.getInt("best_score")),
                    String.valueOf(rs.getInt("games_played"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return board;
    }
    
    // ==================== WELCOME SCREEN ====================
    
    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 23, 42));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel title = new JLabel("🎯 QUIZ BATTLE");
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(new Color(251, 191, 36));
        
        JLabel subtitle = new JLabel("Test your knowledge!");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setPreferredSize(new Dimension(250, 40));
        
        JButton startBtn = createButton("START QUIZ", new Color(34, 197, 94));
        startBtn.setPreferredSize(new Dimension(200, 50));
        
        JButton leaderBtn = createButton("LEADERBOARD", new Color(59, 130, 246));
        leaderBtn.setPreferredSize(new Dimension(200, 50));
        
        JButton backBtn = createButton("← HOME", new Color(107, 114, 128));
        backBtn.setPreferredSize(new Dimension(150, 40));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridy = 1;
        panel.add(subtitle, gbc);
        gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(startBtn, gbc);
        gbc.gridy = 4;
        panel.add(leaderBtn, gbc);
        gbc.gridy = 5;
        panel.add(backBtn, gbc);
        
        startBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name!");
                return;
            }
            getOrCreateUser(name);
            startNewGame();
        });
        
        leaderBtn.addActionListener(e -> {
            updateLeaderboard();
            cardLayout.show(cardPanel, "LEADERBOARD");
        });
        
        backBtn.addActionListener(e -> {
            if (launcher != null) {
                launcher.showPage("MainPage"); // Go back to main menu
            }
        });
        
        return panel;
    }
    
    // ==================== GAME SCREEN ====================
    
    private JPanel createGameScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42));
        
        // Top panel with score and timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 41, 59));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftInfo.setBackground(new Color(30, 41, 59));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(new Color(34, 197, 94));
        
        progressLabel = new JLabel("Question: 1/10");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        progressLabel.setForeground(Color.WHITE);
        
        leftInfo.add(scoreLabel);
        leftInfo.add(progressLabel);
        
        timerLabel = new JLabel("⏱️ 15");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        timerLabel.setForeground(new Color(251, 191, 36));
        
        topPanel.add(leftInfo, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);
        
        // Question panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(new Color(15, 23, 42));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        questionPanel.add(questionLabel);
        questionPanel.add(Box.createVerticalStrut(40));
        
        for (int i = 0; i < 4; i++) {
            final char opt = (char)('A' + i);
            optionButtons[i] = createOptionButton();
            optionButtons[i].addActionListener(e -> checkAnswer(opt));
            questionPanel.add(optionButtons[i]);
            questionPanel.add(Box.createVerticalStrut(15));
        }
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(questionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createOptionButton() {
        JButton btn = new JButton();
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(30, 41, 59));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setMaximumSize(new Dimension(700, 60));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(30, 41, 59));
            }
        });
        
        return btn;
    }
    
    // ==================== RESULT SCREEN ====================
    
    private JPanel createResultScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 23, 42));
        return panel;
    }
    
    // ==================== LEADERBOARD SCREEN ====================
    
    private JPanel createLeaderboardScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42));
        return panel;
    }
    
    // ==================== GAME LOGIC ====================
    
    private void startNewGame() {
        questions = loadQuestions();
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available!");
            return;
        }
        
        currentQuestionIndex = 0;
        gameScore = 0;
        correctCount = 0;
        startTime = System.currentTimeMillis();
        
        cardLayout.show(cardPanel, "GAME");
        displayQuestion();
        startTimer();
    }
    
    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            endGame();
            return;
        }
        
        QuizQuestion q = questions.get(currentQuestionIndex);
        questionLabel.setText("<html><div style='text-align: center; width: 600px;'>" + q.question + "</div></html>");
        
        optionButtons[0].setText("A) " + q.optionA);
        optionButtons[1].setText("B) " + q.optionB);
        optionButtons[2].setText("C) " + q.optionC);
        optionButtons[3].setText("D) " + q.optionD);
        
        for (JButton btn : optionButtons) {
            btn.setEnabled(true);
            btn.setBackground(new Color(30, 41, 59));
        }
        
        progressLabel.setText("Question: " + (currentQuestionIndex + 1) + "/" + questions.size());
        timeLeft = 15;
        timerLabel.setText("⏱️ " + timeLeft);
        timerLabel.setForeground(new Color(251, 191, 36));
    }
    
    private void checkAnswer(char answer) {
        if (gameTimer != null) gameTimer.stop();
        
        QuizQuestion q = questions.get(currentQuestionIndex);
        boolean correct = (answer == q.correct);
        
        if (correct) {
            correctCount++;
            int points = 100 + (timeLeft * 10);
            gameScore += points;
            scoreLabel.setText("Score: " + gameScore);
        }
        
        int correctIdx = q.correct - 'A';
        int selectedIdx = answer - 'A';
        
        optionButtons[correctIdx].setBackground(new Color(34, 197, 94));
        if (!correct) {
            optionButtons[selectedIdx].setBackground(new Color(239, 68, 68));
        }
        
        for (JButton btn : optionButtons) {
            btn.setEnabled(false);
        }
        
javax.swing.Timer delay = new javax.swing.Timer(1500, e -> {
    currentQuestionIndex++;
    
    
    if (currentQuestionIndex < questions.size()) {
        displayQuestion();
        startTimer();
    } else {
       
        endGame();
    }
});
        delay.setRepeats(false);
        delay.start();
    }
    
    private void startTimer() {
        if (gameTimer != null) gameTimer.stop();
        
        gameTimer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("⏱️ " + timeLeft);
            
            if (timeLeft <= 5) {
                timerLabel.setForeground(new Color(239, 68, 68));
            }
            
            if (timeLeft <= 0) {
                gameTimer.stop();
                checkAnswer('X');
            }
        });
        gameTimer.start();
    }
    
    private void endGame() {
        if (gameTimer != null) gameTimer.stop();
        
        updateUserStats();
        showResultScreen();
    }
    
    private void showResultScreen() {
        JPanel resultPanel = (JPanel) cardPanel.getComponent(2);
        resultPanel.removeAll();
        resultPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel title = new JLabel("🎉 GAME OVER 🎉");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(new Color(251, 191, 36));
        
        JLabel scoreResult = new JLabel("Final Score: " + gameScore);
        scoreResult.setFont(new Font("Arial", Font.BOLD, 32));
        scoreResult.setForeground(Color.WHITE);
        
        JLabel accuracy = new JLabel(String.format("Correct: %d/%d (%.0f%%)", 
            correctCount, questions.size(), (correctCount * 100.0 / questions.size())));
        accuracy.setFont(new Font("Arial", Font.PLAIN, 20));
        accuracy.setForeground(Color.WHITE);
        
        JLabel best = new JLabel("Your Best: " + bestScore);
        best.setFont(new Font("Arial", Font.PLAIN, 18));
        best.setForeground(new Color(251, 191, 36));
        
        JButton playAgain = createButton("PLAY AGAIN", new Color(34, 197, 94));
        playAgain.setPreferredSize(new Dimension(200, 50));
        playAgain.addActionListener(e -> startNewGame());
        
        JButton backBtn = createButton("MAIN MENU", new Color(59, 130, 246));
        backBtn.setPreferredSize(new Dimension(200, 50));
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "WELCOME"));
        
        gbc.gridx = 0; gbc.gridy = 0;
        resultPanel.add(title, gbc);
        gbc.gridy = 1;
        resultPanel.add(scoreResult, gbc);
        gbc.gridy = 2;
        resultPanel.add(accuracy, gbc);
        gbc.gridy = 3;
        resultPanel.add(best, gbc);
        gbc.gridy = 4;
        resultPanel.add(playAgain, gbc);
        gbc.gridy = 5;
        resultPanel.add(backBtn, gbc);
        
        resultPanel.revalidate();
        resultPanel.repaint();
        cardLayout.show(cardPanel, "RESULT");
    }
    
    private void updateLeaderboard() {
        JPanel leaderPanel = (JPanel) cardPanel.getComponent(3);
        leaderPanel.removeAll();
        leaderPanel.setLayout(new BorderLayout());
        leaderPanel.setBackground(new Color(15, 23, 42));
        
        JLabel title = new JLabel("🏆 LEADERBOARD", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(251, 191, 36));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        String[] columns = {"Rank", "Player", "Best Score", "Games"};
        List<String[]> data = getLeaderboard();
        String[][] tableData = data.toArray(new String[0][]);
        
        JTable table = new JTable(tableData, columns);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(40);
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(51, 65, 85));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        
        JButton backBtn = createButton("← HOME", new Color(107, 114, 128));
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "WELCOME"));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(15, 23, 42));
        bottomPanel.add(backBtn);
        
        leaderPanel.add(title, BorderLayout.NORTH);
        leaderPanel.add(scroll, BorderLayout.CENTER);
        leaderPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        leaderPanel.revalidate();
        leaderPanel.repaint();
    }
    
    // ==================== UTILITY ====================
    
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    // ==================== INNER CLASS ====================
    
    class QuizQuestion {
        int id;
        String question;
        String optionA, optionB, optionC, optionD;
        char correct;
        String category;
        
        QuizQuestion(int id, String q, String a, String b, String c, String d, char cor, String cat) {
            this.id = id;
            this.question = q;
            this.optionA = a;
            this.optionB = b;
            this.optionC = c;
            this.optionD = d;
            this.correct = cor;
            this.category = cat;
        }
    }
}