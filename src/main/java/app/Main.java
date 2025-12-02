package app;

import data_access.UserDataAccessObject;
import entity.DashboardConfig;
import entity.Task;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.ConfigureDashboardPresenter;
import interface_adapter.DashboardViewModel;
import interface_adapter.SetTimerController;
import interface_adapter.SetTimerPresenter;
import java.util.ArrayList;
import java.util.List;
import use_cases.*;
import use_cases.ConfigureDashboardInputBoundary;
import use_cases.ConfigureDashboardInteractor;
import use_cases.ConfigureDashboardOutputBoundary;
import use_cases.DashboardConfigDataAccessInterface;
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
import data_access.WeatherDataAccessObject;
import view.WeatherPanel;
import data_access.NbaGamesDataAccessObject;
import view.NbaGamesPanel;
import data_access.MapDataAccessObject;
import view.MapPanel;
import view.ViewManager;
import view.ViewManagerModel;
import view.LoginView;
import interface_adapter.LoginViewModel;
import interface_adapter.LoginPresenter;
import interface_adapter.LoginController;
import use_cases.login.LoginInputBoundary;
import use_cases.login.LoginInteractor;
import use_cases.login.LoginOutputBoundary;
import java.awt.CardLayout;

import data_access.InMemoryTaskListDataAccessObject;
import interface_adapter.TaskListController;
import interface_adapter.TaskListPresenter;
import interface_adapter.TaskListViewModel;
import use_cases.TaskListDataAccessInterface;
import use_cases.TaskListInputBoundary;
import use_cases.TaskListOutputBoundary;
import use_cases.TaskListInteractor;
import view.TaskListView;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard App");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel views = new JPanel(cardLayout);
            frame.add(views);

            ViewManagerModel viewManagerModel = new ViewManagerModel();
            new ViewManager(views, cardLayout, viewManagerModel);

            // #################################################################################
            // ## Here is just a simple implementation of memory
            // ## We can replace this with a database-based implementation Later.
            // #################################################################################
            DashboardConfigDataAccessInterface gateway = new DashboardConfigDataAccessInterface() {
                private DashboardConfig config =
                        new DashboardConfig(true, true, true,
                                true, true, true);

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
            // 1. Create the Shared Database (One DB for everyone!)
            InMemoryTaskListDataAccessObject sharedDAO = new InMemoryTaskListDataAccessObject();

            // 2. Setup Timer (Using Shared DB)
            SetTimerOutputBoundary timerPresenter = new SetTimerPresenter();
            SetTimerInputBoundary timerInteractor = new SetTimerInteractor(sharedDAO, timerPresenter);
            SetTimerController timerController = new SetTimerController(timerInteractor);

            // 3. Start Timer Service (Using the list from Shared DB)
            TimerService timerService = new TimerService(sharedDAO);
            timerService.startTimer();

            // 4. Setup Task List (Using the SAME Shared DB)
            TaskListViewModel taskListViewModel = new TaskListViewModel();
            TaskListOutputBoundary taskListPresenter = new TaskListPresenter(taskListViewModel);
            TaskListInputBoundary taskListInteractor = new TaskListInteractor(sharedDAO, taskListPresenter);
            TaskListController taskListController = new TaskListController(taskListInteractor);

            // 5. Create the Panel
            JPanel taskPanel = new TaskListView(taskListController, taskListViewModel, timerController);
            // -------------------------------

//--------------------------------
// Small APP Panel
// -------------------------------

            StockDataAccessObject stockDAO = new StockDataAccessObject();
            StockPanel stockPanel = new StockPanel(stockDAO);

            WeatherDataAccessObject weatherDAO = new WeatherDataAccessObject();
            WeatherPanel weatherPanel = new WeatherPanel(weatherDAO);

            NbaGamesDataAccessObject nbaDAO = new NbaGamesDataAccessObject();
            NbaGamesPanel sportsPanel = new NbaGamesPanel(nbaDAO);

            MapDataAccessObject mapDAO = new MapDataAccessObject();
            MapPanel mapPanel = new MapPanel(mapDAO);


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

            LoginViewModel loginViewModel = new LoginViewModel();
            LoginOutputBoundary loginPresenter = new LoginPresenter(viewManagerModel, loginViewModel);
            LoginInputBoundary loginInteractor = new LoginInteractor(userDAO, loginPresenter);
            LoginController loginController = new LoginController(loginInteractor);

            LoginView loginView = new LoginView(loginViewModel, loginController);
            views.add(loginView, loginView.viewName);

            // -------------------------------
            // Main Panel and Frame setup
            // -------------------------------
            MainDashboardView dashboardView = new MainDashboardView(
                    taskPanel,
                    stockPanel,
                    weatherPanel,
                    sportsPanel,
                    mapPanel,
                    pokemonPanel,
                    viewModel, controller,
                    viewManagerModel
            );
            views.add(dashboardView, "Dashboard");
            viewManagerModel.setActiveView(loginView.viewName);
            viewManagerModel.firePropertyChanged();

            frame.setVisible(true);
        });
    }
}
