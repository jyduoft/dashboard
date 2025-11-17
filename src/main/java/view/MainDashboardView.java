package view;

import entity.DashboardConfig;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.DashboardViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

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

        if (config.isShowTasks()) {
            add(taskPanel, BorderLayout.WEST);
        }

        List<JPanel> widgets = new ArrayList<>();
        if (config.isShowStocks()) widgets.add(stockPanel);
        if (config.isShowWeather()) widgets.add(weatherPanel);
        if (config.isShowMap()) widgets.add(mapPanel);

        int count = widgets.size();
        if (count == 0) {
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(new JLabel("No widgets selected.", SwingConstants.CENTER),
                    BorderLayout.CENTER);
        } else {
            centerPanel.setLayout(new GridLayout(1, count));
            for (JPanel widget : widgets) {
                centerPanel.add(widget);
            }
        }

        if (config.isShowPokemon()) {
            add(pokemonPanel, BorderLayout.SOUTH);
        }

        revalidate();
        repaint();
    }
}
