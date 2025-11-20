package view;

import javax.swing.*;
import java.awt.*;

public class PokemonPanel extends JPanel {

    private final ImageIcon icon;
    private int x = 0;
    private int dx = 3;

    public PokemonPanel(ImageIcon icon) {
        this.icon = icon;

        Timer timer = new Timer(30, e -> {
            move();
            repaint();
        });
        timer.start();

        setPreferredSize(new Dimension(200, 60));
    }

    private void move() {
        int width = getWidth();
        int imgWidth = icon.getIconWidth();

        x += dx;
        if (x < 0) {
            x = 0;
            dx = -dx;
        } else if (x + imgWidth > width) {
            x = width - imgWidth;
            dx = -dx;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int imgHeight = icon.getIconHeight();
        int y = (getHeight() - imgHeight) / 2;

        // IMPORTANT: this animates GIFs properly
        g.drawImage(icon.getImage(), x, y, this);
    }
}
