/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package reflect;

import java.awt.*;
import java.time.*;
import javax.swing.*;
import java.sql.*;
public class Reflect {
   static JFrame frame;
    public static void main(String[] args) {
        frame = new JFrame("Reflect Journal Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700);
        frame.setLayout(new BorderLayout());
        
        JPanel navigate = new JPanel();
        navigate.setBackground(new Color(200, 190, 240, 180));
        navigate.setLayout(new BoxLayout(navigate, BoxLayout.X_AXIS));
        navigate.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
        JButton goHome = new JButton("Home");
        goHome.setBackground(new Color(243,245,236));
        JButton listall= new JButton("List");
        listall.setBackground(new Color(200, 245, 200));
        JButton doSave = new JButton("Save");
        doSave.setBackground(new Color(57,54,54));
        doSave.setForeground(Color.WHITE);
        
        navigate.add(goHome);
        navigate.add(Box.createHorizontalGlue());
        navigate.add(listall);
        navigate.add(Box.createHorizontalGlue());
        navigate.add(doSave);

        frame.add(navigate, BorderLayout.NORTH);
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();

                // Subtle pastel colors for calming diagonal gradient
                Color softLavender = new Color(230, 225, 250);
                Color softMint = new Color(200, 245, 220);

                // Diagonal gradient from top-left to bottom-right
                GradientPaint gp = new GradientPaint(0, 0, softLavender, w, h, softMint);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                g2.dispose();
            }
        };
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        // ----- Mood Panel -----
        JPanel mood = new JPanel();
        mood.setLayout(new BorderLayout(10, 10));
        
        JPanel moodTop = new JPanel();
        moodTop.setLayout(new BoxLayout(moodTop, BoxLayout.Y_AXIS));
        JLabel mlabel = new JLabel("How was your day today?");
        mlabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        mlabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        String stime = time.getHour() + ":" + (time.getMinute() < 10 ? "0" : "") + time.getMinute();
        JLabel datedis = new JLabel("Date: " + date.toString());
        datedis.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel timedis = new JLabel("Time: " + stime);
        timedis.setAlignmentX(Component.CENTER_ALIGNMENT);

        moodTop.add(mlabel);
        moodTop.add(Box.createVerticalStrut(10));
        moodTop.add(datedis);
        moodTop.add(timedis);

        JPanel emoticonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        String[] moodsList = {"Happy", "Good", "Anxious", "Grumpy", "Sad", "Neutral"};
        JButton[] moodButtons = new JButton[moodsList.length];
        String[] selectedMood = {""};
        for (int i = 0; i < moodsList.length; i++) {
            JButton btn = new JButton(moodsList[i]);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(90, 30));
            btn.setBackground(new Color(245,245,220));
            final int index = i;
            btn.addActionListener(e -> {
                selectedMood[0] = moodsList[index];       // store selected mood
                // Highlight selected button
                for (JButton b : moodButtons) {
                    b.setBackground(new Color(245,245,220)); 
                }
                btn.setBackground(new Color(255,200,200));        
            });

            moodButtons[i] = btn;
            emoticonPanel.add(btn);
        }

        mood.add(moodTop, BorderLayout.NORTH);
        mood.add(emoticonPanel, BorderLayout.CENTER);
        content.add(mood);
        content.add(Box.createVerticalStrut(20)); // spacing between sections

        // ----- Journal Panel -----
        JPanel journal = new JPanel(new BorderLayout(10, 10));
        JLabel journLabel = new JLabel("How would you describe your day?");
        journLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        journal.add(journLabel, BorderLayout.NORTH);

        JTextArea entry = new JTextArea(15, 50);
        entry.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 2, true));
        entry.setBorder(BorderFactory.createCompoundBorder(
        entry.getBorder(),  // keep the existing border
        BorderFactory.createEmptyBorder(10, 10, 10, 10) // top, left, bottom, right padding
        ));

        entry.setFont(new Font("SanSerif",Font.PLAIN,14));
        entry.setLineWrap(true);
        entry.setWrapStyleWord(true);
        entry.setToolTipText("Start writing here...");
        JScrollPane scrollPane = new JScrollPane(entry);
        journal.add(scrollPane, BorderLayout.CENTER);

        content.add(journal);

        frame.add(content, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        
        
        doSave.addActionListener(e -> {
        String journalText = entry.getText();
        String moodText = selectedMood[0];
            if(moodText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a mood!");
                return;
            }
            try {
            String url = "jdbc:mysql://localhost:3306/sample";
            String user = "root";
            String pass ="root";
            Connection conn = DriverManager.getConnection(url,user,pass);
            String sql = "insert into moodEntry(entryDate, entryTime, mood, journalText) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            pst.setTime(2, java.sql.Time.valueOf(LocalTime.now()));
            pst.setString(3, moodText); // selected mood
            pst.setString(4, journalText); // journal text
            pst.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Entry saved successfully!");
            entry.setText("");
            for (JButton b : moodButtons) b.setBackground(null);
            selectedMood[0] = "";
            showNextPage(); 
         } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error saving entry!");
        }
        });

        // ------------------ Home Button ------------------
        goHome.addActionListener(e -> {
            // For now, reset fields or go to main page
            entry.setText("");
            for (JButton b : moodButtons) b.setBackground(null);
            selectedMood[0] = "";
            System.out.println("Going Home...");
        });
        
    listall.addActionListener(e -> {
    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/sample", "root", "root")) {

        String q = "SELECT entryDate,entryTime, mood, journalText " +
                   "FROM moodEntry ORDER BY entryDate DESC, entryTime DESC";
        PreparedStatement pst = con.prepareStatement(q);
        ResultSet rs = pst.executeQuery();

        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append("Date: ").append(rs.getDate("entryDate")).append("\n")
              .append("Time: ").append(rs.getTime("entryTime")).append("\n")
              .append("Mood: ").append(rs.getString("mood")).append("\n")
              .append("Journal: ").append(rs.getString("journalText")).append("\n")
              .append("----------------------------------------\n");
        }

        if (sb.length() == 0) {
            sb.append("No journal entries found.");
        }

        JTextArea displayArea = new JTextArea(sb.toString());
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(displayArea);
        scroll.setPreferredSize(new Dimension(450, 600));

        JOptionPane.showMessageDialog(frame, scroll,
                "All Journal Entries", JOptionPane.PLAIN_MESSAGE);

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(frame,
                "Error fetching entries!", "Error", JOptionPane.ERROR_MESSAGE);
    }
});

        mood.setOpaque(false);   
        moodTop.setOpaque(false);   
        emoticonPanel.setOpaque(false); 
        journal.setOpaque(false);    

        frame.setVisible(true);
    }
    
    
    public static void showNextPage() {
    JPanel nextPanel = new JPanel();
    nextPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));

    JButton playBtn = new JButton("Play");
    JButton relaxBtn = new JButton("Relax");

    nextPanel.add(playBtn);
    nextPanel.add(relaxBtn);

    frame.getContentPane().removeAll(); // remove current content
    frame.add(nextPanel, BorderLayout.CENTER);
    frame.revalidate();
    frame.repaint();
}

}
