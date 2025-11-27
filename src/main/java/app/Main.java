package app;

import data_access.UserDataAccessObject;
import entity.DashboardConfig;
import entity.User;
import entity.Pokemon;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.ConfigureDashboardPresenter;
import interface_adapter.DashboardViewModel;
import org.json.JSONObject;
import use_cases.*;
import view.MainDashboardView;
import view.PokemonPanel;
import data_access.PokemonDataAccessObject;
import view.StockPanel;
import data_access.StockDataAccessObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // #################################################################################
            // ## Here is just a simple implementation of memory
            // ## We can replace this with a database-based implementation Later.
            // #################################################################################
            DashboardConfigDataAccessInterface gateway = new DashboardConfigDataAccessInterface() {
                private DashboardConfig config =
                        new DashboardConfig(true, true, true, true, true);

                @Override
                public void save(DashboardConfig c) {
                    this.config = c;
                }

                @Override
                public DashboardConfig load() {
                    return config;
                }
            };
            // #################################################################################

            // -------------------------------
            // VIEW MODEL
            // Stores UI state and notifies views when it changes.
            // -------------------------------
            DashboardViewModel viewModel = new DashboardViewModel();

            ConfigureDashboardOutputBoundary presenter =
                    new ConfigureDashboardPresenter(viewModel);
            ConfigureDashboardInputBoundary interactor =
                    new ConfigureDashboardInteractor(gateway, presenter);
            ConfigureDashboardController controller =
                    new ConfigureDashboardController(interactor);

            viewModel.setConfig(gateway.load());

            // -------------------------------
            // Program Panels
            // I just write a string here as example, we could substitute it
            // with each functional implementation
            // -------------------------------
            JPanel taskPanel = new JPanel();
            taskPanel.add(new JLabel("Tasks panel"));

//--------------------------------
// Small APP Panel
// -------------------------------

            StockDataAccessObject stockDAO = new StockDataAccessObject();
            StockPanel stockPanel = new StockPanel(stockDAO);

            JPanel weatherPanel = new JPanel();
            weatherPanel.add(new JLabel("Weather panel"));

            JPanel mapPanel = new JPanel();
            mapPanel.add(new JLabel("Map panel"));

//--------------------------------
// Pokémon Panel
// -------------------------------
            User user = new User("tony", "123456");
            UserDataAccessObject userDAO = new UserDataAccessObject();
            PokemonDataAccessObject pokemonDAO = new PokemonDataAccessObject();

            //load or create Firebase user
            JSONObject userJson;
            try {
                userJson = userDAO.loadUser(user);     // user exists → load data
                System.out.println("Loaded existing Firebase user.");
            } catch (Exception e) {
                System.out.println("User does not exist. Creating new Firebase user...");
                try {
                    userDAO.createUser(user);          // create user
                    userJson = userDAO.loadUser(user); // reload
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to create user: " + ex.getMessage());
                }
            }

            //create PokémonManager using loaded JSON
            PokemonManager pokemonManager = null;
            try {
                pokemonManager = new PokemonManager(user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //ensure user has a current Pokémon
            if (pokemonManager.getUserInv().isEmpty() || pokemonManager.getCurrentPokemon() == null) {

                Pokemon charmander = new Pokemon(
                        "Charmander",
                        "src/main/resources/cache/pokemon/4.gif",
                        4,
                        1,
                        0,
                        100,
                        100
                );

                pokemonManager.getUserInv().add(charmander);
                pokemonManager.setCurrentPokemon(charmander);

                // Save back to Firebase
                try {
                    pokemonDAO.saveUserData(user, pokemonManager);
                } catch (Exception ex) {
                    System.out.println("Error saving default Pokémon: " + ex.getMessage());
                }
            }

            //download missing sprite GIFs
            pokemonDAO.fetchPokemonSprites(pokemonManager.getUserInv());

            //load the GIF from local cache
            String imgPath = pokemonManager.getCurrentPokemon().getImgFilePath();
            ImageIcon gifIcon = new ImageIcon(imgPath);

            //build Pokémon panel
            PokemonPanel pokemonPanel = new PokemonPanel(gifIcon);


            // -------------------------------
            // Main Panel and Frame setup
            // -------------------------------
            MainDashboardView dashboardView = new MainDashboardView(
                    taskPanel,
                    stockPanel,
                    weatherPanel,
                    mapPanel,
                    pokemonPanel,
                    viewModel, controller
            );

            JFrame frame = new JFrame("Dashboard Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(dashboardView, BorderLayout.CENTER);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
