import javax.swing.*;
import java.awt.*;

public class GenrePage extends JFrame {

    public GenrePage() {
        setTitle("ReflectApp 🌸 Music Buddy");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 240, 245));
        setLayout(null);

        JLabel title = new JLabel("Choose Your Music Mood 🎶", SwingConstants.CENTER);
        title.setBounds(50, 50, 400, 50);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(120, 60, 80));
        add(title);

        // Buttons
        JButton meditateBtn = createButton("🧘‍♀️ Meditate", 100);
        meditateBtn.addActionListener(e -> openSuggestionPage("Meditate"));
        add(meditateBtn);

        JButton relaxBtn = createButton("💖 Relax", 220);
        relaxBtn.addActionListener(e -> openSuggestionPage("Relax"));
        add(relaxBtn);

        JButton sleepBtn = createButton("🌙 Sleep", 340);
        sleepBtn.addActionListener(e -> openSuggestionPage("Sleep"));
        add(sleepBtn);
    }

    private JButton createButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(100, y, 300, 80);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(new Color(255, 193, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }

    private void openSuggestionPage(String genre) {
        SuggestionPage sp = new SuggestionPage(genre);
        sp.setVisible(true);
        dispose();
    }
}
