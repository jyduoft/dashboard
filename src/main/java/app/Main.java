package app;

import entity.DashboardConfig;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.ConfigureDashboardPresenter;
import interface_adapter.DashboardViewModel;
import use_cases.ConfigureDashboardInputBoundary;
import use_cases.ConfigureDashboardInteractor;
import use_cases.ConfigureDashboardOutputBoundary;
import use_cases.DashboardConfigDataAccessInterface;
import view.MainDashboardView;
import view.PokemonPanel;

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

            JPanel stockPanel = new JPanel();
            stockPanel.add(new JLabel("Stocks panel"));

            JPanel weatherPanel = new JPanel();
            weatherPanel.add(new JLabel("Weather panel"));

            JPanel mapPanel = new JPanel();
            mapPanel.add(new JLabel("Map panel"));

            // -------------------------------
            // Pokemen Panels
            // Here I put a simple circle here, change it with pokemen api,
            // and also adjust parameters
            // -------------------------------
            BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.fillOval(5, 5, 30, 30);
            g2.dispose();
            PokemonPanel pokemonPanel = new PokemonPanel(img);


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
