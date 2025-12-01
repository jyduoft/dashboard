package view;

import entity.DashboardConfig;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.DashboardViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MainDashboardView extends JPanel implements PropertyChangeListener {

    private final JPanel taskPanel;
    private final JPanel stockPanel;
    private final JPanel weatherPanel;
    private final JPanel mapPanel;
    private final PokemonPanel pokemonPanel;

    private final DashboardViewModel viewModel;
    private final ConfigureDashboardController controller;

    private final JPanel centerPanel = new JPanel();
    private final JButton configButton = new JButton("⚙ Customize");

    public MainDashboardView(JPanel taskPanel,
                             JPanel stockPanel,
                             JPanel weatherPanel,
                             JPanel mapPanel,
                             PokemonPanel pokemonPanel,
                             DashboardViewModel viewModel,
                             ConfigureDashboardController controller) {
        this.taskPanel = taskPanel;
        this.stockPanel = stockPanel;
        this.weatherPanel = weatherPanel;
        this.mapPanel = mapPanel;
        this.pokemonPanel = pokemonPanel;
        this.viewModel = viewModel;
        this.controller = controller;

        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBox.add(configButton);
        topBar.add(rightBox, BorderLayout.EAST);
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
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("config".equals(evt.getPropertyName())) {
            DashboardConfig config = (DashboardConfig) evt.getNewValue();
            refreshLayout(config);
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
        if (config.isShowStocks()) {
            widgets.add(stockPanel);
        }
        if (config.isShowSports()) {
            widgets.add(mapPanel);
        }
        if (config.isShowWeather()) {
            widgets.add(weatherPanel);
        }

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
