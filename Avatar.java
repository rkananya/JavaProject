package reflect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Avatar extends JPanel {
    private int eyeOffsetX = 0;
    private int eyeOffsetY = 0;
    private int handAngle = 0;
    private boolean waving = false;

    public Avatar() {
        setOpaque(false);
    }

    public void lookAt(Point p) {
        int centerX = getX() + getWidth() / 2;
        int centerY = getY() + getHeight() / 2;
        eyeOffsetX = (p.x - centerX) / 30;
        eyeOffsetY = (p.y - centerY) / 30;
        repaint();
    }

    public void waveHand() {
        if (waving) return;
        waving = true;
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    handAngle = (i % 2 == 0) ? 20 : -20;
                    repaint();
                    Thread.sleep(150);
                }
                handAngle = 0;
                repaint();
            } catch (InterruptedException ignored) {}
            waving = false;
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Face
        g2.setColor(new Color(255, 239, 213));
        g2.fillOval(0, 0, w, h);

        // Eyes
        g2.setColor(Color.BLACK);
        g2.fillOval(w / 3 - 10 + eyeOffsetX, h / 3 + eyeOffsetY, 15, 15);
        g2.fillOval(2 * w / 3 - 20 + eyeOffsetX, h / 3 + eyeOffsetY, 15, 15);

        // Smile
        g2.setStroke(new BasicStroke(3));
        g2.drawArc(w / 3, 2 * h / 3 - 10, w / 3, 20, 0, -180);

        // Hand (wave)
        g2.setColor(new Color(255, 239, 213));
        int armX = w - 30;
        int armY = h / 2;
        g2.translate(armX, armY);
        g2.rotate(Math.toRadians(handAngle));
        g2.fillOval(0, -20, 40, 40);
    }
}
