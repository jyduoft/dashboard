package app;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.json.JSONObject;

// Entities
import entity.DashboardConfig;
import entity.Pokemon;
import entity.Task;
import entity.User;

// Data Access
import data_access.PokemonDataAccessObject;
import data_access.SignupDataAccessObject;
import data_access.UserDataAccessObject;

// Use Cases
import use_cases.DashboardConfigDataAccessInterface;
import use_cases.TaskDataAccessInterface;
import use_cases.PokemonManager;
import use_cases.TimerService;
import use_cases.ConfigureDashboardInputBoundary;
import use_cases.ConfigureDashboardInteractor;
import use_cases.ConfigureDashboardOutputBoundary;
import use_cases.SetTimerInteractor;

// Use Cases - Subfolders
import use_cases.login.LoginInteractor;
import use_cases.signup.SignupInteractor;

// Interface Adapters
import interface_adapter.ViewManagerModel;
import interface_adapter.DashboardViewModel;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.ConfigureDashboardPresenter;
import interface_adapter.SetTimerController;
import interface_adapter.SetTimerPresenter;

// Interface Adapters - Subfolders
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;

// Views
import view.LoginView;
import view.PokemonPanel;
import view.SignupView;
import view.ViewManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1. SETUP MAIN WINDOW
            JFrame frame = new JFrame("Dashboard Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel views = new JPanel(cardLayout);
            ViewManagerModel viewManagerModel = new ViewManagerModel();
            new ViewManager(views, cardLayout, viewManagerModel);

            // 2. DATA ACCESS
            UserDataAccessObject userDataAccessObject = new UserDataAccessObject();
            SignupDataAccessObject signupDAO = new SignupDataAccessObject();

            List<Task> allTasks = new ArrayList<>();
            allTasks.add(new Task("Finish Project"));
            TaskDataAccessInterface taskDAO = new TaskDataAccessInterface() {
                @Override public Task getTask(String title) {
                    for (Task t : allTasks) if (t.getTitle().equals(title)) return t;
                    return null;
                }
                @Override public void saveTask(Task task) {
                    System.out.println("Timer saved for: " + task.getTitle());
                }
            };

            // 3. SETUP TIMER
            SetTimerPresenter timerPresenter = new SetTimerPresenter();
            SetTimerInteractor timerInteractor = new SetTimerInteractor(taskDAO, timerPresenter);
            SetTimerController timerController = new SetTimerController(timerInteractor);

            // 4. SETUP LOGIN
            LoginViewModel loginViewModel = new LoginViewModel();
            LoginPresenter loginPresenter = new LoginPresenter(loginViewModel, viewManagerModel);
            LoginInteractor loginInteractor = new LoginInteractor(userDataAccessObject, loginPresenter);
            LoginController loginController = new LoginController(loginInteractor);

            // FIX: Pass viewManagerModel here so the 'Sign Up' button works
            LoginView loginView = new LoginView(loginViewModel, loginController, viewManagerModel);
            views.add(loginView, loginView.viewName);

            // 5. SETUP SIGNUP
            SignupViewModel signupViewModel = new SignupViewModel();
            SignupPresenter signupPresenter = new SignupPresenter(signupViewModel, viewManagerModel);
            SignupInteractor signupInteractor = new SignupInteractor(signupDAO, signupPresenter);
            SignupController signupController = new SignupController(signupInteractor);
            SignupView signupView = new SignupView(signupController, signupViewModel);
            views.add(signupView, signupView.viewName);

            // 6. SETUP DASHBOARD CONFIG
            DashboardConfigDataAccessInterface configGateway = new DashboardConfigDataAccessInterface() {
                private DashboardConfig config = new DashboardConfig(true, true, true, true, true);
                @Override public void save(DashboardConfig c) { this.config = c; }
                @Override public DashboardConfig load() { return config; }
            };
            DashboardViewModel dashboardViewModel = new DashboardViewModel();
            ConfigureDashboardOutputBoundary configPresenter = new ConfigureDashboardPresenter(dashboardViewModel);
            ConfigureDashboardInputBoundary configInteractor = new ConfigureDashboardInteractor(configGateway, configPresenter);
            ConfigureDashboardController configController = new ConfigureDashboardController(configInteractor);

            // 7. BUILD PANELS
            JPanel taskPanel = new JPanel();
            taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
            taskPanel.add(new JLabel("My To-Do List:"));
            JPanel taskRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel taskName = new JLabel("Finish Homework");
            taskRow.add(taskName);
            JButton timerButton = new JButton("⏱️");
            timerButton.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(taskPanel, "Set timer (minutes):");
                if (input != null && !input.isEmpty()) {
                    try {
                        long mins = Long.parseLong(input);
                        timerController.execute("Finish Homework", mins, 0);
                    } catch (NumberFormatException ex) { }
                }
            });
            taskRow.add(timerButton);
            taskPanel.add(taskRow);

            JPanel stockPanel = new JPanel(); stockPanel.add(new JLabel("Stocks Placeholder"));
            JPanel weatherPanel = new JPanel(); weatherPanel.add(new JLabel("Weather Placeholder"));

            // Pokemon Panel Setup
            PokemonPanel pokemonPanel = null;
            try {
                User user = new User("tony", "123456");
                PokemonDataAccessObject pokemonDAO = new PokemonDataAccessObject();
                try {
                    userDataAccessObject.loadUser(user);
                } catch (Exception e) {
                    userDataAccessObject.createUser(user);
                }
                PokemonManager pokemonManager = new PokemonManager(user);
                if (pokemonManager.getUserInv().isEmpty() || pokemonManager.getCurrentPokemon() == null) {
                    Pokemon charmander = new Pokemon("Charmander", "src/main/resources/cache/pokemon/4.gif", 4, 1, 0, 100, 100);
                    pokemonManager.getUserInv().add(charmander);
                    pokemonManager.setCurrentPokemon(charmander);
                    pokemonDAO.saveUserData(user, pokemonManager);
                }
                pokemonDAO.fetchPokemonSprites(pokemonManager.getUserInv());
                ImageIcon gifIcon = new ImageIcon(pokemonManager.getCurrentPokemon().getImgFilePath());
                pokemonPanel = new PokemonPanel(gifIcon);
            } catch (Exception e) {
                System.out.println("Pokemon Error: " + e.getMessage());
                pokemonPanel = new PokemonPanel(new ImageIcon());
            }

            // 8. ASSEMBLE DASHBOARD
            JPanel dashboardView = new JPanel(new GridLayout(2, 2));
            dashboardView.add(taskPanel);
            dashboardView.add(pokemonPanel);
            dashboardView.add(stockPanel);
            dashboardView.add(weatherPanel);

            // Register names
            views.add(dashboardView, "logged in");
            views.add(dashboardView, "dashboard");

            frame.add(views);

            // START AT LOGIN
            viewManagerModel.setActiveView(loginView.viewName);
            viewManagerModel.firePropertyChanged();

            frame.setVisible(true);

            TimerService timerService = new TimerService();
            timerService.startTimer(allTasks);
        });
    }
}