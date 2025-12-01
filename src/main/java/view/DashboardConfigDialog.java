package view;

import entity.DashboardConfig;
import interface_adapter.ConfigureDashboardController;
import interface_adapter.DashboardViewModel;

import javax.swing.*;
import java.awt.*;

public class DashboardConfigDialog extends JDialog {

    private final JCheckBox tasksBox = new JCheckBox("Tasks");
    private final JCheckBox stocksBox = new JCheckBox("Stocks");
    private final JCheckBox weatherBox = new JCheckBox("Weather");
    private final JCheckBox sportsBox = new JCheckBox("Sports");
    private final JCheckBox mapBox = new JCheckBox("Maps");
    private final JCheckBox pokemonBox = new JCheckBox("Pokemon at bottom");

    public DashboardConfigDialog(Window owner,
                                 DashboardViewModel viewModel,
                                 ConfigureDashboardController controller) {
        super(owner, "Customize Dashboard", ModalityType.APPLICATION_MODAL);

        DashboardConfig current = viewModel.getConfig();
        if (current != null) {
            tasksBox.setSelected(current.isShowTasks());
            stocksBox.setSelected(current.isShowStocks());
            weatherBox.setSelected(current.isShowWeather());
            sportsBox.setSelected(current.isShowSports());
            mapBox.setSelected(current.isShowMaps());
            pokemonBox.setSelected(current.isShowPokemon());
        } else {
            tasksBox.setSelected(true);
            stocksBox.setSelected(true);
            weatherBox.setSelected(true);
            sportsBox.setSelected(true);
            mapBox.setSelected(true);
            pokemonBox.setSelected(true);
        }

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(tasksBox);
        content.add(stocksBox);
        content.add(weatherBox);
        content.add(sportsBox);
        content.add(mapBox);
        content.add(pokemonBox);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            controller.onConfigConfirm(
                    tasksBox.isSelected(),
                    stocksBox.isSelected(),
                    weatherBox.isSelected(),
                    sportsBox.isSelected(),
                    mapBox.isSelected(),
                    pokemonBox.isSelected()
            );
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(ok);
        buttons.add(cancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(content, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}
