package view;

import use_cases.MapDataAccessInterface;

import javax.swing.*;
import java.awt.*;

/**
 * A simple map panel, here we implement a simple static map, that can show the graph map of that city
 * For simplicity, here I implement by same city list with weather panel, these cities are given with
 * specified local coordinates.
 * NOTE1: we can simply expand it by adding more cities in the list.
 * NOTE2: Similarly, I was considering to use live-map with an IP based API to get current location,
 *        but for the complexity and privacy, we left the future expand here.
 */
public class MapPanel extends JPanel {

    private final MapDataAccessInterface mapDAO;

    private final JComboBox<String> cityBox = new JComboBox<>(
            new String[]{
                    "Toronto",
                    "Vancouver",
                    "New York",
                    "London",
                    "Tokyo",
                    "Beijing",
                    "Sydney",
                    "Dubai",
                    "Paris"
            }
    );
    private final JButton loadButton = new JButton("Load");

    private final JLabel mapLabel = new JLabel();  // 显示地图
    private final JLabel statusLabel = new JLabel(" ");

    public MapPanel(MapDataAccessInterface mapDAO) {
        this.mapDAO = mapDAO;

        setLayout(new BorderLayout(5, 5));

        // Title
        JLabel title = new JLabel("Map", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        // City + Load
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topRow.add(new JLabel("City:"));
        topRow.add(cityBox);
        topRow.add(loadButton);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        northPanel.add(title);
        northPanel.add(Box.createVerticalStrut(4));
        northPanel.add(topRow);

        // Map Area
        mapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mapLabel.setPreferredSize(new Dimension(400, 250));
        JScrollPane scroll = new JScrollPane(mapLabel);

        statusLabel.setFont(statusLabel.getFont().deriveFont(
                statusLabel.getFont().getSize2D() - 1f));

        add(northPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> onLoad());
        cityBox.addActionListener(e -> onLoad());

        onLoad();
    }

    private void onLoad() {
        String city = (String) cityBox.getSelectedItem();
        if (city == null || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a city.");
            return;
        }

        loadButton.setEnabled(false);
        statusLabel.setText("Loading map...");

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return mapDAO.fetchMapForCity(city);
            }

            @Override
            protected void done() {
                loadButton.setEnabled(true);
                try {
                    ImageIcon icon = get();
                    mapLabel.setIcon(icon);
                    statusLabel.setText("Updated.");
                } catch (Exception ex) {
                    mapLabel.setIcon(null);
                    statusLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }
}
