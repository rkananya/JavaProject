/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package reflect;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class home{

    public static void main(String[] args) {
        SwingUtilities.invokeLater(home::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Reflect - Home");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(255, 241, 229), getWidth(), getHeight(),
                        new Color(229, 255, 247)); // peach to mint
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        Avatar avatar = new Avatar();
        avatar.setBounds(230, 100, 150, 150);
        mainPanel.add(avatar);
        JButton journalBtn = new JButton("Journal");
        JButton playBtn = new JButton("Play");
        JButton relaxBtn = new JButton("Relax");
        JButton exitBtn = new JButton("Exit");

        Font btnFont = new Font("SansSerif", Font.BOLD, 18);
        for (JButton btn : new JButton[]{journalBtn, relaxBtn, exitBtn}) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(240, 248, 255));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }

        journalBtn.setBounds(220, 350, 160, 45);
        relaxBtn.setBounds(220, 420, 160, 45);
        exitBtn.setBounds(220, 490, 160, 45);

        mainPanel.add(journalBtn);
        mainPanel.add(relaxBtn);
        mainPanel.add(exitBtn);

        // ===== Mouse Interaction (avatar follows cursor) =====
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                avatar.lookAt(e.getPoint());
            }
        });
        avatar.waveHand();

        journalBtn.addActionListener(e -> {
            frame.dispose();
            Reflect.main(null); 
        });

        playBtn.addActionListener(e->{
            frame.dispose();
            GenrePage.main(null);
        });
       

        exitBtn.addActionListener(e -> System.exit(0));

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
