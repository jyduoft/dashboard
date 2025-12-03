package view;

import use_cases.PokemonManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PokemonShop extends JPanel {

    private PokemonManager pokemonManager;
    private JLabel coinsLabel;
    private JButton rollButton;
    private JButton backButton;

    public PokemonShop(PokemonManager pokemonManager) {
        this.pokemonManager = pokemonManager;

        // ========== 1. Layout for entire shop screen ==========
        setLayout(new BorderLayout());

        // ========== 2. TOP PANEL (Coins display) ==========
        JPanel topPanel = new JPanel(); // default FlowLayout is fine
        coinsLabel = new JLabel("Coins: " + pokemonManager.getCoins());
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(coinsLabel);

        add(topPanel, BorderLayout.NORTH);

        // ========== 3. CENTER PANEL (Gacha area) ==========
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        rollButton = new JButton("Roll");
        rollButton.setFont(new Font("Arial", Font.BOLD, 20));

        // Add components to center
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(rollButton);
        centerPanel.add(Box.createVerticalStrut(20));

        add(centerPanel, BorderLayout.CENTER);

        // ========== 4. RIGHT PANEL (Probability or info) ==========
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(200, 0)); // makes the side panel narrower

        JTextArea infoArea = new JTextArea("Probabilities:\nRare: 15%\nUncommon: 30%\nCommon: 55%");
        infoArea.setEditable(false);
        rightPanel.add(infoArea);

        add(rightPanel, BorderLayout.EAST);

        // ========== 5. BOTTOM PANEL (Back button) ==========
        JPanel bottomPanel = new JPanel();
        backButton = new JButton("Back");
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);

        //========== Title ==========
    }

    // ========== UPDATE COINS IN UI ==========
    public void setCoins(int coins) {
        coinsLabel.setText("Coins: " + coins);
    }

    // ========== CONTROLLER LISTENERS ==========
    public void setRollListener(ActionListener l) {
        rollButton.addActionListener(l);
    }

    public void setBackListener(ActionListener l) {
        backButton.addActionListener(l);
    }
}

