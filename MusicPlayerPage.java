import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;

public class MusicPlayerPage extends JFrame {

    private Clip clip;

    public MusicPlayerPage(String genre, String soundName) {
        setTitle("ReflectApp 🎶 " + genre);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 240, 245));
        setLayout(null);

        JLabel label = new JLabel("Playing: " + soundName, SwingConstants.CENTER);
        label.setBounds(50, 30, 400, 50);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        add(label);

        JButton playBtn = new JButton("▶ Play 🎵");
        playBtn.setBounds(150, 150, 200, 70);
        playBtn.setFont(new Font("Arial", Font.BOLD, 18));
        playBtn.setBackground(new Color(255, 193, 204));
        playBtn.setForeground(Color.WHITE);
        playBtn.setFocusPainted(false);
        playBtn.setBorderPainted(false);
        playBtn.setOpaque(true);
        add(playBtn);

        JButton stopBtn = new JButton("⏹ Stop");
        stopBtn.setBounds(150, 250, 200, 70);
        stopBtn.setFont(new Font("Arial", Font.BOLD, 18));
        stopBtn.setBackground(new Color(255, 193, 204));
        stopBtn.setForeground(Color.WHITE);
        stopBtn.setFocusPainted(false);
        stopBtn.setBorderPainted(false);
        stopBtn.setOpaque(true);
        add(stopBtn);

        // Map button to file name inside resources folder
        String fileName = "";
        switch (soundName) {
            case "🧘‍♀️ Calm Meditation": fileName = "meditate1.wav"; break;
            case "💖 Rain": fileName = "relax_rain.wav"; break;
            case "💖 Ocean": fileName = "relax_ocean.wav"; break;
            case "💖 Forest": fileName = "relax_forest.wav"; break;
            case "🌙 Sleep Sounds": fileName = "sleep1.wav"; break;
        }

        playBtn.addActionListener(e -> playWav(fileName));
        stopBtn.addActionListener(e -> stopWav());

        JButton backBtn = new JButton("🔙 Back");
        backBtn.setBounds(150, 600, 200, 50);
        backBtn.setBackground(new Color(255, 193, 204));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 18));
        backBtn.addActionListener(e -> {
            SuggestionPage sp = new SuggestionPage(genre);
            sp.setVisible(true);
            stopWav();
            dispose();
        });
        add(backBtn);
    }

    private void playWav(String fileName) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }

            // Load from resources folder using ClassLoader
            URL url = getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                JOptionPane.showMessageDialog(this, "File not found in resources: " + fileName);
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error playing sound!");
        }
    }

    private void stopWav() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
