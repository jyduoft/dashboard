package view;

import entity.DashboardConfig;
import entity.Pokemon;
import entity.User;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.DashboardViewModel;
import interface_adapter.PokemonShopController;
import use_cases.PokemonManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MainDashboardView extends JPanel implements PropertyChangeListener {

    private final JPanel taskPanel;
    private final JPanel stockPanel;
    private final JPanel weatherPanel;
    private final JPanel sportsPanel;
    private final JPanel mapPanel;
    private PokemonPanel pokemonPanel;

    private final DashboardViewModel viewModel;
    private final ConfigureDashboardController controller;
    private final view.ViewManagerModel viewManagerModel;
    private final JButton logoutButton = new JButton("Logout");
    private final JPanel centerPanel = new JPanel();
    private final JButton configButton = new JButton("⚙ Customize");
    private final JButton pokemonShopButton = new JButton("🛒 Pokemon Shop");
    private final JButton pokemonInventoryButton = new JButton("📦 Pokemon Inventory");

    private PokemonManager pokemonManager;
    private User user;

    public MainDashboardView(JPanel taskPanel,
                             JPanel stockPanel,
                             JPanel weatherPanel,
                             JPanel mapPanel,
                             JPanel sportsPanel,
                             PokemonPanel pokemonPanel,
                             DashboardViewModel viewModel,
                             ConfigureDashboardController controller,
                             view.ViewManagerModel viewManagerModel) {
        this.taskPanel = taskPanel;
        this.stockPanel = stockPanel;
        this.weatherPanel = weatherPanel;
        this.sportsPanel = sportsPanel;
        this.mapPanel = mapPanel;
        this.pokemonPanel = pokemonPanel;
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;

        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBox.add(configButton);
        rightBox.add(pokemonShopButton);
        rightBox.add(pokemonInventoryButton);
        rightBox.add(configButton);
        topBar.add(rightBox, BorderLayout.EAST);
        JPanel leftBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBox.add(logoutButton);
        topBar.add(leftBox, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        viewModel.addPropertyChangeListener(this);

        DashboardConfig current = viewModel.getConfig();
        if (current != null) {
            refreshLayout(current);
        }

        configButton.addActionListener(e -> {
            new DashboardConfigDialog(SwingUtilities.getWindowAncestor(this),
                    viewModel, controller).setVisible(true);

        });
        logoutButton.addActionListener(e -> {
            System.out.println("Logout button clicked!");
            // Clear current user
            viewManagerModel.setCurrentUser(null);
            // Switch to login view
            viewManagerModel.setActiveView("log in");
            viewManagerModel.firePropertyChanged();
            System.out.println("Switched to login view");
        });

        pokemonShopButton.addActionListener(e -> openPokemonShop());
        pokemonInventoryButton.addActionListener(e -> openPokemonInventory());

        // Initialize Pokemon system if user is already logged in
        User currentUser = viewManagerModel.getCurrentUser();
        if (currentUser != null) {
            initializePokemonSystem();
        }
    }

    public void updateUserData(PokemonManager pokemonManager, User user, ImageIcon pokemonIcon) {
        this.pokemonManager = pokemonManager;
        this.user = user;

        // Remove old PokemonPanel if it exists
        if (pokemonPanel != null) {
            remove(pokemonPanel);
        }

        // Create new PokemonPanel with the icon
        this.pokemonPanel = new PokemonPanel(pokemonIcon);

        // Force refresh of layout to show new panel
        refreshLayout(viewModel.getConfig());
    }

    public void updateUserData(PokemonManager pokemonManager, User user) {
        // Always set pokemonManager and user
        this.pokemonManager = pokemonManager;
        this.user = user;

        // Use PokemonPanel from ViewManagerModel if available
        PokemonPanel panelFromModel = viewManagerModel.getPokemonPanel();
        if (panelFromModel != null) {
            // Remove old panel
            if (pokemonPanel != null) {
                remove(pokemonPanel);
            }

            // Use panel from model
            this.pokemonPanel = panelFromModel;

            // Force refresh
            refreshLayout(viewModel.getConfig());
        }
    }

    private void openPokemonShop() {
        // Try to initialize from viewManagerModel if not already set
        if (pokemonManager == null || user == null) {
            if (!initializePokemonSystem()) {
                JOptionPane.showMessageDialog(this,
                        "Pokemon system not initialized. Please log in first.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Create shop view
        PokemonShop shopView = new PokemonShop(pokemonManager);

        // Create controller
        PokemonShopController shopController = new PokemonShopController(
                pokemonManager, shopView, user);

        // Create dialog window
        java.awt.Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog shopDialog;
        if (parentWindow instanceof java.awt.Frame) {
            shopDialog = new JDialog((java.awt.Frame) parentWindow, "Pokemon Shop", true);
        } else if (parentWindow instanceof java.awt.Dialog) {
            shopDialog = new JDialog((java.awt.Dialog) parentWindow, "Pokemon Shop", true);
        } else {
            // Fallback: create a non-modal dialog
            shopDialog = new JDialog();
            shopDialog.setTitle("Pokemon Shop");
            shopDialog.setModal(true);
        }

        shopDialog.setContentPane(shopView);
        shopDialog.setSize(600, 400);
        shopDialog.setLocationRelativeTo(this);
        shopDialog.setVisible(true);

        // Update coins display when dialog closes
        shopView.setCoins(pokemonManager.getCoins());
    }

    private boolean initializePokemonSystem() {
        // Try to initialize pokemonManager and user from viewManagerModel
        User currentUser = viewManagerModel.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // If already initialized with the same user, no need to reinitialize
        if (pokemonManager != null && user != null && user.equals(currentUser)) {
            return true;
        }

        try {
            data_access.PokemonDataAccessObject pokemonDAO = new data_access.PokemonDataAccessObject();
            PokemonManager manager = new PokemonManager(currentUser);
            
            // Ensure user has a current Pokémon
            if (manager.getUserInv().isEmpty() || manager.getCurrentPokemon() == null) {
                Pokemon charmander = new Pokemon(
                        "Charmander",
                        "src/main/resources/cache/pokemon/4.gif",
                        4,
                        1,
                        0,
                        100,
                        100
                );
                manager.getUserInv().add(charmander);
                manager.setCurrentPokemon(charmander);
                pokemonDAO.saveUserData(currentUser, manager);
            }

            // Download missing sprite GIFs
            pokemonDAO.fetchPokemonSprites(manager.getUserInv());
            
            this.pokemonManager = manager;
            this.user = currentUser;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openPokemonInventory() {
        // Try to initialize from viewManagerModel if not already set
        if (pokemonManager == null || user == null) {
            if (!initializePokemonSystem()) {
                JOptionPane.showMessageDialog(this,
                        "Pokemon system not initialized. Please log in first.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Create inventory view
        PokemonInventoryView inventoryView = new PokemonInventoryView(pokemonManager, user);

        // Create dialog window
        java.awt.Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog inventoryDialog;
        if (parentWindow instanceof java.awt.Frame) {
            inventoryDialog = new JDialog((java.awt.Frame) parentWindow, "Pokemon Inventory", true);
        } else if (parentWindow instanceof java.awt.Dialog) {
            inventoryDialog = new JDialog((java.awt.Dialog) parentWindow, "Pokemon Inventory", true);
        } else {
            inventoryDialog = new JDialog();
            inventoryDialog.setTitle("Pokemon Inventory");
            inventoryDialog.setModal(true);
        }

        inventoryDialog.setContentPane(inventoryView);
        inventoryDialog.setSize(600, 500);
        inventoryDialog.setLocationRelativeTo(this);

        // Add window listener to refresh PokemonPanel when dialog closes
        inventoryDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // Reload PokemonManager and update PokemonPanel with the newly selected Pokemon
                if (user != null && pokemonPanel != null) {
                    try {
                        data_access.PokemonDataAccessObject pokemonDAO = new data_access.PokemonDataAccessObject();
                        PokemonManager updatedManager = new PokemonManager(user);
                        pokemonManager = updatedManager;
                        
                        Pokemon currentPokemon = updatedManager.getCurrentPokemon();
                        if (currentPokemon != null) {
                            String imgPath = "src/main/resources/cache/pokemon/" + currentPokemon.getId() + ".gif";
                            ImageIcon icon = null;
                            
                            java.io.File imgFile = new java.io.File(imgPath);
                            if (imgFile.exists() && imgFile.isFile()) {
                                icon = new ImageIcon(imgFile.getAbsolutePath());
                            } else {
                                String projectRoot = System.getProperty("user.dir");
                                String separator = java.io.File.separator;
                                java.io.File absFile = new java.io.File(projectRoot + separator + "src" + separator + "main" + separator + "resources" + separator + "cache" + separator + "pokemon" + separator + currentPokemon.getId() + ".gif");
                                if (absFile.exists()) {
                                    icon = new ImageIcon(absFile.getAbsolutePath());
                                } else {
                                    java.io.File targetFile = new java.io.File(projectRoot + separator + "target" + separator + "classes" + separator + "cache" + separator + "pokemon" + separator + currentPokemon.getId() + ".gif");
                                    if (targetFile.exists()) {
                                        icon = new ImageIcon(targetFile.getAbsolutePath());
                                    }
                                }
                            }
                            
                            if (icon != null && icon.getIconWidth() > 0) {
                                // Update the existing panel's icon to avoid flickering
                                pokemonPanel.updateIcon(icon);
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Error refreshing PokemonPanel: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        inventoryDialog.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("config".equals(evt.getPropertyName())) {
            DashboardConfig config = (DashboardConfig) evt.getNewValue();
            refreshLayout(config);
        } else if ("pokemonPanel".equals(evt.getPropertyName())) {
            // This is triggered when LoginPresenter sets a new PokemonPanel
            // We need to update the local pokemonPanel reference and refresh layout
            PokemonPanel newPanel = (PokemonPanel) evt.getNewValue();
            if (newPanel != null) {
                if (pokemonPanel != null) {
                    remove(pokemonPanel);
                }
                this.pokemonPanel = newPanel;
                refreshLayout(viewModel.getConfig());
            }
        }
    }

    private void refreshLayout(DashboardConfig config) {
        remove(taskPanel);
        remove(pokemonPanel);
        centerPanel.removeAll();

        // Task Panel: set always on the leftmost with almost half of the screen
        if (config.isShowTasks()) {
            taskPanel.setPreferredSize(new Dimension(400, taskPanel.getHeight()));
            add(taskPanel, BorderLayout.WEST);
        }

        // Components Panel: set at right with 4*4 blocks
        java.util.List<JPanel> widgets = new ArrayList<>();
        if (config.isShowStocks()) {widgets.add(stockPanel);}
        if (config.isShowMaps()) {widgets.add(mapPanel);}
        if (config.isShowSports()) {widgets.add(sportsPanel);}
        if (config.isShowWeather()) {widgets.add(weatherPanel);}

        if (widgets.isEmpty()) {
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(new JLabel("No widgets selected.", SwingConstants.CENTER),
                    BorderLayout.CENTER);
        } else {
            centerPanel.setLayout(new GridLayout(2, 2, 10, 10));

            for (JPanel widget : widgets) {
                centerPanel.add(widget);
            }
        }

        // Set Pokemon Panel at bottem.
        if (config.isShowPokemon()) {
            add(pokemonPanel, BorderLayout.SOUTH);
        }

        revalidate();
        repaint();
    }

}
