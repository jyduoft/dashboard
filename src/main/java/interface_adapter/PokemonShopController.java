package interface_adapter;

import data_access.PokemonDataAccessObject;
import entity.Pokemon;
import entity.User;
import use_cases.PokemonManager;
import view.PokemonShop;

import javax.swing.*;
import java.awt.*;

public class PokemonShopController {

    private final PokemonManager manager;
    private final PokemonShop shopView;
    private final PokemonDataAccessObject pokemonDAO;
    private final User user;

    public PokemonShopController(PokemonManager manager, PokemonShop shopView, User user) {
        this.manager = manager;
        this.shopView = shopView;
        this.user = user;
        this.pokemonDAO = new PokemonDataAccessObject();

        // Attach button listeners
        shopView.setRollListener(e -> handleRoll());
        shopView.setBackListener(e -> handleBack());
    }

    // ============================================================
    // HANDLE ROLL LOGIC
    // ============================================================

    private void handleRoll() {

        int rollCost = 10;

        // Not enough coins
        if (manager.getCoins() < rollCost) {
            JOptionPane.showMessageDialog(shopView,
                    "Not enough coins!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Deduct coins
        manager.setCoins(manager.getCoins() - rollCost);

        // Roll Pokémon
        Pokemon rolled = manager.rollPokemon();

        // Cache sprite if missing (downloads if needed)
        pokemonDAO.fetchPokemonSprites(manager.getUserInv());

        // Ensure sprite path now points to the cached file
        String imgPath = "src/main/resources/cache/pokemon/" + rolled.getId() + ".gif";
        rolled.setImgFilePath(imgPath);

        // Save updated user + Pokémon data to Firebase
        try {
            pokemonDAO.saveUserData(user, manager);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(shopView,
                    "Failed to save data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Update coins on UI
        shopView.setCoins(manager.getCoins());

        // Show Pokémon result with image
        showPokemonRollResult(rolled);
    }
    
    private void showPokemonRollResult(Pokemon pokemon) {
        // Create a custom panel to display the Pokemon
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Load Pokemon image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            String imgPath = pokemon.getImgFilePath();
            ImageIcon icon = null;
            
            // Try multiple path strategies
            java.io.File imgFile = new java.io.File(imgPath);
            if (imgFile.exists() && imgFile.isFile()) {
                icon = new ImageIcon(imgFile.getAbsolutePath());
            } else {
                // Try standard cache location
                String projectRoot = System.getProperty("user.dir");
                String separator = java.io.File.separator;
                java.io.File cacheFile = new java.io.File(projectRoot + separator + "src" + separator + "main" + separator + "resources" + separator + "cache" + separator + "pokemon" + separator + pokemon.getId() + ".gif");
                if (cacheFile.exists()) {
                    icon = new ImageIcon(cacheFile.getAbsolutePath());
                } else {
                    // Try target directory
                    java.io.File targetFile = new java.io.File(projectRoot + separator + "target" + separator + "classes" + separator + "cache" + separator + "pokemon" + separator + pokemon.getId() + ".gif");
                    if (targetFile.exists()) {
                        icon = new ImageIcon(targetFile.getAbsolutePath());
                    }
                }
            }
            
            if (icon != null && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                // Scale image to a reasonable size for the dialog (max 200x200)
                int maxSize = 200;
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                
                // For GIFs, use original to preserve animation, but constrain the label size
                if (imgPath.toLowerCase().endsWith(".gif")) {
                    imageLabel.setIcon(icon);
                    imageLabel.setPreferredSize(new Dimension(Math.min(originalWidth, maxSize), Math.min(originalHeight, maxSize)));
                } else {
                    double scale = Math.min((double)maxSize / originalWidth, (double)maxSize / originalHeight);
                    int scaledWidth = Math.max(1, (int)(originalWidth * scale));
                    int scaledHeight = Math.max(1, (int)(originalHeight * scale));
                    Image img = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                }
            } else {
                imageLabel.setText("No Image");
                imageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            }
        } catch (Exception e) {
            imageLabel.setText("Error loading image");
            imageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            e.printStackTrace();
        }
        
        // Pokemon name label
        JLabel nameLabel = new JLabel("You rolled: " + pokemon.getName() + "!", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Pokemon info
        JLabel infoLabel = new JLabel(
            "<html><center>ID: " + pokemon.getId() + "<br>" +
            "Level: " + pokemon.getLevel() + "</center></html>",
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Layout
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        resultPanel.add(nameLabel, BorderLayout.NORTH);
        resultPanel.add(imagePanel, BorderLayout.CENTER);
        resultPanel.add(infoLabel, BorderLayout.SOUTH);
        
        // Show custom dialog
        JOptionPane.showMessageDialog(shopView,
                resultPanel,
                "Roll Result - " + pokemon.getName(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // HANDLE BACK BUTTON
    // ============================================================

    private void handleBack() {
        // Close the dialog window
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(shopView);
        if (window != null) {
            window.dispose();
        }
    }
}

