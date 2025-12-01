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
            List<Task> allTasks = new ArrayList<>();
            allTasks.add(Task.TaskFactory.createTask("Finish Homework")); // Dummy Data
            allTasks.add(Task.TaskFactory.createTask("Email Professor"));

            TaskDataAccessInterface taskDAO = new TaskDataAccessInterface() {
                @Override
                public Task getTask(String title) {
                    // Simple search logic for our list
                    for (Task t : allTasks) {
                        if (t.getTaskName().equals(title)) return t;
                    }
                    return null;
                }

                @Override
                public void saveTask(Task task) {
                    System.out.println("Task saved: " + task.getTaskName());
                }
            };

            SetTimerOutputBoundary timerPresenter = new SetTimerPresenter();
            SetTimerInputBoundary timerInteractor = new SetTimerInteractor(taskDAO, timerPresenter);
            SetTimerController timerController = new SetTimerController(timerInteractor);

            TimerService timerService = new TimerService();
            timerService.startTimer(allTasks);

            // -------------------------------
            // Program Panels
            // I just write a string here as example, we could substitute it
            // with each functional implementation
            // -------------------------------
//            JPanel taskPanel = new JPanel();
//            taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
//            taskPanel.add(new JLabel("My To-Do List:"));
//            JPanel taskRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            taskRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
//            JLabel taskName = new JLabel("Finish Homework");
//            taskRow.add(taskName);
//            JButton timerButton = new JButton("⏱️");
//            timerButton.setToolTipText("Set Timer");
//            timerButton.addActionListener(e -> {
//                String input = JOptionPane.showInputDialog(taskPanel, "Set timer (minutes):");
//                if (input != null && !input.isEmpty()) {
//                    try {
//                        long mins = Long.parseLong(input);
//                        timerController.execute("Finish Homework", mins, 0);
//                    } catch (NumberFormatException ex) {
//                        JOptionPane.showMessageDialog(taskPanel, "Please enter a valid number.");
//                    }
//                }
//            });
//
//            taskRow.add(timerButton);
//
//            taskPanel.add(taskRow);

            // IMPORTANT TODO: Replace with Firebase
            TaskListDataAccessInterface taskListDAO = new InMemoryTaskListDataAccessObject();

            TaskListViewModel taskListViewModel = new TaskListViewModel();
            TaskListOutputBoundary taskListPresenter = new TaskListPresenter(taskListViewModel);

            TaskListInputBoundary taskListInteractor =
                    new TaskListInteractor(taskListDAO, taskListPresenter);
            TaskListController taskListController =
                    new TaskListController(taskListInteractor);

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
