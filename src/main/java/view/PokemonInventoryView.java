package view;

import data_access.PokemonDataAccessObject;
import entity.Pokemon;
import entity.User;
import use_cases.PokemonManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PokemonInventoryView extends JPanel {

    private PokemonManager pokemonManager;
    private User user;
    private JLabel currentPokemonLabel;
    private JPanel inventoryPanel;
    private JScrollPane scrollPane;

    public PokemonInventoryView(PokemonManager pokemonManager, User user) {
        this.pokemonManager = pokemonManager;
        this.user = user;
        
        setLayout(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("Pokemon Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);
        
        // Current Pokemon display
        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPokemonLabel = new JLabel("Current Pokemon: " + 
            (pokemonManager.getCurrentPokemon() != null ? 
                pokemonManager.getCurrentPokemon().getName() : "None"));
        currentPokemonLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPokemonLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentPanel.add(currentPokemonLabel, BorderLayout.CENTER);
        currentPanel.setBorder(BorderFactory.createTitledBorder("Current Pokemon"));
        add(currentPanel, BorderLayout.NORTH);
        
        // Inventory list
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(inventoryPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Pokemon Collection"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with close button
        JPanel bottomPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Download missing sprites before displaying
        if (pokemonManager != null && user != null) {
            try {
                data_access.PokemonDataAccessObject pokemonDAO = new data_access.PokemonDataAccessObject();
                pokemonDAO.fetchPokemonSprites(pokemonManager.getUserInv());
            } catch (Exception e) {
                System.err.println("Error fetching Pokemon sprites: " + e.getMessage());
            }
        }
        
        // Refresh inventory display
        refreshInventory();
        
        // Close button listener (will be set by controller)
        closeButton.addActionListener(e -> {
            java.awt.Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }
    
    public void refreshInventory() {
        inventoryPanel.removeAll();
        
        ArrayList<Pokemon> inventory = pokemonManager.getUserInv();
        Pokemon currentPokemon = pokemonManager.getCurrentPokemon();
        
        if (inventory.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon in inventory. Visit the shop to get some!");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            inventoryPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : inventory) {
                JPanel pokemonCard = createPokemonCard(pokemon, pokemon.equals(currentPokemon));
                inventoryPanel.add(pokemonCard);
                inventoryPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }
    
    private JPanel createPokemonCard(Pokemon pokemon, boolean isCurrent) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(550, 120)); // Fixed height to prevent cutting
        card.setMinimumSize(new Dimension(550, 120));
        
        if (isCurrent) {
            card.setBackground(new Color(200, 255, 200)); // Light green for current
        }
        
        // Left side: Pokemon image (larger, more prominent)
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setMinimumSize(new Dimension(100, 100));
        
        try {
            String imgPath = pokemon.getImgFilePath();
            System.out.println("Loading image for " + pokemon.getName() + " (ID: " + pokemon.getId() + ")");
            System.out.println("  Stored path: " + imgPath);
            
            ImageIcon icon = null;
            java.io.File foundFile = null;
            
            // Strategy 1: Try the stored path
            java.io.File imgFile = new java.io.File(imgPath);
            if (imgFile.exists() && imgFile.isFile()) {
                System.out.println("  Found at stored path: " + imgFile.getAbsolutePath());
                icon = new ImageIcon(imgFile.getAbsolutePath());
                foundFile = imgFile;
            } else {
                // Strategy 2: Try standard cache location relative to project root
                java.io.File relFile = new java.io.File("src/main/resources/cache/pokemon/" + pokemon.getId() + ".gif");
                if (relFile.exists()) {
                    System.out.println("  Found at relative path: " + relFile.getAbsolutePath());
                    icon = new ImageIcon(relFile.getAbsolutePath());
                    foundFile = relFile;
                } else {
                    // Strategy 3: Try absolute path from project root (Windows-compatible)
                    String projectRoot = System.getProperty("user.dir");
                    String separator = java.io.File.separator;
                    java.io.File absFile = new java.io.File(projectRoot + separator + "src" + separator + "main" + separator + "resources" + separator + "cache" + separator + "pokemon" + separator + pokemon.getId() + ".gif");
                    if (absFile.exists()) {
                        System.out.println("  Found at absolute path: " + absFile.getAbsolutePath());
                        icon = new ImageIcon(absFile.getAbsolutePath());
                        foundFile = absFile;
                    } else {
                        // Strategy 4: Try target directory (for compiled resources)
                        java.io.File targetFile = new java.io.File(projectRoot + separator + "target" + separator + "classes" + separator + "cache" + separator + "pokemon" + separator + pokemon.getId() + ".gif");
                        if (targetFile.exists()) {
                            System.out.println("  Found at target path: " + targetFile.getAbsolutePath());
                            icon = new ImageIcon(targetFile.getAbsolutePath());
                            foundFile = targetFile;
                        } else {
                            System.out.println("  Image not found in any location!");
                            System.out.println("    Tried: " + imgPath);
                            System.out.println("    Tried: " + relFile.getAbsolutePath());
                            System.out.println("    Tried: " + absFile.getAbsolutePath());
                            System.out.println("    Tried: " + targetFile.getAbsolutePath());
                        }
                    }
                }
            }
            
            if (icon != null && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                System.out.println("  Image loaded successfully: " + icon.getIconWidth() + "x" + icon.getIconHeight());
                
                // For GIF files, use the original ImageIcon to preserve animation
                // Don't scale animated GIFs as it breaks the animation
                String filePath = foundFile != null ? foundFile.getAbsolutePath() : imgPath;
                boolean isGif = filePath.toLowerCase().endsWith(".gif");
                
                if (isGif) {
                    System.out.println("  Detected GIF file - using original ImageIcon to preserve animation");
                    // Use the original icon directly - ImageIcon handles animated GIFs
                    imageLabel.setIcon(icon);
                    imageLabel.setText(""); // Clear any text
                } else {
                    // For non-GIF images, scale them
                    int maxSize = 100;
                    int originalWidth = icon.getIconWidth();
                    int originalHeight = icon.getIconHeight();
                    
                    double scale = Math.min((double)maxSize / originalWidth, (double)maxSize / originalHeight);
                    int scaledWidth = Math.max(1, (int)(originalWidth * scale));
                    int scaledHeight = Math.max(1, (int)(originalHeight * scale));
                    
                    System.out.println("  Scaling to: " + scaledWidth + "x" + scaledHeight);
                    
                    // Scale image maintaining aspect ratio
                    Image img = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(img);
                    imageLabel.setIcon(scaledIcon);
                    imageLabel.setText(""); // Clear any text
                }
                
                // Verify icon was set
                if (imageLabel.getIcon() == null) {
                    System.out.println("  WARNING: Icon was not set on label!");
                } else {
                    System.out.println("  Icon successfully set on label");
                }
            } else {
                // Show placeholder text
                System.out.println("  Failed to load image - showing placeholder");
                imageLabel.setText("No Image");
                imageLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                imageLabel.setIcon(null);
            }
        } catch (Exception e) {
            System.err.println("Error loading Pokemon image for " + pokemon.getName() + ": " + e.getMessage());
            e.printStackTrace();
            imageLabel.setText("Error");
            imageLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            imageLabel.setIcon(null);
        }
        
        // Wrap image in a panel to ensure proper sizing and centering
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        
        // CRITICAL: Set explicit size constraints to prevent layout collapse
        Dimension imagePanelSize = new Dimension(120, 100);
        imagePanel.setPreferredSize(imagePanelSize);
        imagePanel.setMinimumSize(imagePanelSize);
        imagePanel.setMaximumSize(new Dimension(120, 100)); // Fixed max width
        imagePanel.setSize(imagePanelSize);
        
        // Visible border to debug
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLUE, 2), // Blue border to make it visible
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setOpaque(true);
        imagePanel.setBackground(isCurrent ? new Color(200, 255, 200) : Color.WHITE);
        
        // Ensure image label fills the panel and is visible
        imageLabel.setOpaque(false);
        imageLabel.setVisible(true);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        imageLabel.setVerticalTextPosition(SwingConstants.CENTER);
        
        // Set label size to fill panel
        imageLabel.setPreferredSize(new Dimension(110, 90));
        imageLabel.setMinimumSize(new Dimension(110, 90));
        
        // Force repaint
        imagePanel.setVisible(true);
        imagePanel.revalidate();
        imagePanel.repaint();
        imageLabel.revalidate();
        imageLabel.repaint();
        
        // Center: Pokemon info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(pokemon.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel idLabel = new JLabel("ID: " + pokemon.getId());
        JLabel levelLabel = new JLabel("Level: " + pokemon.getLevel());
        JLabel expLabel = new JLabel("EXP: " + pokemon.getExp() + " / " + pokemon.getMaxExp());
        
        if (isCurrent) {
            JLabel currentLabel = new JLabel("★ CURRENT ★");
            currentLabel.setFont(new Font("Arial", Font.BOLD, 14));
            currentLabel.setForeground(Color.RED);
            infoPanel.add(currentLabel);
        }
        
        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);
        infoPanel.add(levelLabel);
        infoPanel.add(expLabel);
        
        // Right side: Select button
        JButton selectButton = new JButton(isCurrent ? "Current" : "Select");
        selectButton.setEnabled(!isCurrent);
        selectButton.addActionListener(e -> {
            pokemonManager.setCurrentPokemon(pokemon);
            
            // Save to Firebase if user is available
            if (user != null) {
                try {
                    PokemonDataAccessObject pokemonDAO = new PokemonDataAccessObject();
                    pokemonDAO.saveUserData(user, pokemonManager);
                } catch (Exception ex) {
                    System.err.println("Error saving Pokemon selection: " + ex.getMessage());
                }
            }
            
            refreshInventory();
            updateCurrentPokemonLabel();
            JOptionPane.showMessageDialog(this,
                pokemon.getName() + " is now your current Pokemon!",
                "Pokemon Changed",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Add components to card - ensure image panel is visible
        // IMPORTANT: Add imagePanel first to ensure it gets proper space
        card.add(imagePanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(selectButton, BorderLayout.EAST);
        
        // Ensure imagePanel is visible and has proper constraints
        imagePanel.setVisible(true);
        
        // Debug: verify imagePanel is added and icon is set
        System.out.println("  Image panel added to card. Panel preferred size: " + imagePanel.getPreferredSize());
        System.out.println("  Image label icon: " + (imageLabel.getIcon() != null ? "SET (" + imageLabel.getIcon().getIconWidth() + "x" + imageLabel.getIcon().getIconHeight() + ")" : "NULL"));
        System.out.println("  Image label text: " + (imageLabel.getText().isEmpty() ? "(empty)" : imageLabel.getText()));
        System.out.println("  Image label visible: " + imageLabel.isVisible());
        System.out.println("  Image panel visible: " + imagePanel.isVisible());
        
        // Force repaint after a short delay to ensure everything is laid out
        SwingUtilities.invokeLater(() -> {
            card.revalidate();
            card.repaint();
            imagePanel.revalidate();
            imagePanel.repaint();
            imageLabel.revalidate();
            imageLabel.repaint();
        });
        
        return card;
    }
    
    private void updateCurrentPokemonLabel() {
        Pokemon current = pokemonManager.getCurrentPokemon();
        currentPokemonLabel.setText("Current Pokemon: " + 
            (current != null ? current.getName() : "None"));
    }
    
    public void setSelectListener(ActionListener listener) {
        // This can be used if we want external control
    }
}

