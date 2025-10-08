import javax.swing.*;
import java.awt.*;

public class SuggestionPage extends JFrame {

    public SuggestionPage(String genre) {
        setTitle("ReflectApp 🎧 " + genre);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 240, 245));
        setLayout(null);

        JLabel title = new JLabel("Your " + genre + " Playlist 🎵", SwingConstants.CENTER);
        title.setBounds(50, 30, 400, 50);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title);

        String[] sounds;
        switch(genre) {
            case "Meditate": sounds = new String[]{"🧘‍♀️ Calm Meditation"}; break;
            case "Relax": sounds = new String[]{"💖 Rain", "💖 Ocean", "💖 Forest"}; break;
            default: sounds = new String[]{"🌙 Sleep Sounds"}; break;
        }

        int y = 120;
        for(String sound : sounds) {
            JButton btn = new JButton(sound);
            btn.setBounds(100, y, 300, 70);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setBackground(new Color(255, 200, 220));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);

            btn.addActionListener(e -> {
                MusicPlayerPage mp = new MusicPlayerPage(genre, sound);
                mp.setVisible(true);
                dispose();
            });
            add(btn);
            y += 100;
        }

        JButton backBtn = new JButton("🔙 Back");
        backBtn.setBounds(150, 600, 200, 50);
        backBtn.setBackground(new Color(255, 193, 204));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 18));
        backBtn.addActionListener(e -> {
            GenrePage gp = new GenrePage();
            gp.setVisible(true);
            dispose();
        });
        add(backBtn);
    }
}
