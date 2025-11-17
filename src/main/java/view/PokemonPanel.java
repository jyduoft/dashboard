package view;

import javax.swing.*;
import java.awt.*;

public class PokemonPanel extends JPanel {

    private final Image pokemon;
    private int x = 0;
    private int dx = 3;

    public PokemonPanel(Image pokemon) {
        this.pokemon = pokemon;

        Timer timer = new Timer(30, e -> {
            move();
            repaint();
        });
        timer.start();

        setPreferredSize(new Dimension(200, 60));
    }

    private void move() {
        int width = getWidth();
        int imgWidth = pokemon.getWidth(this);
        if (imgWidth <= 0) imgWidth = 40;

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
        int imgHeight = pokemon.getHeight(this);
        if (imgHeight <= 0) imgHeight = 40;

        int y = (getHeight() - imgHeight) / 2;
        g.drawImage(pokemon, x, y, imgHeight, imgHeight, this);
    }
}
