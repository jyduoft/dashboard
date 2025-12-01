package view;

import data_access.WeatherDataAccessObject;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class WeatherPanel extends JPanel {

    private final WeatherDataAccessObject weatherDAO;

    // I use the selection box here, we could update more cities if you want.
    // And don't forget to update data access in the WeatherDataAcessObject file.
    private final JComboBox<String> cityBox = new JComboBox<>(
            new String[]{"Toronto", "Vancouver", "New York", "London", "Tokyo",
                    "Beijing", "Sydney", "Dubai", "Paris"}
    );

    private final JButton loadButton = new JButton("Load");

    private final JLabel tempLabel = new JLabel("Temperature: -");
    private final JLabel descLabel = new JLabel("Description: -");
    private final JLabel humidityLabel = new JLabel("Humidity: -");
    private final JLabel statusLabel = new JLabel(" ");

    public WeatherPanel(WeatherDataAccessObject weatherDAO) {
        this.weatherDAO = weatherDAO;
        // Structure:
        // Title: Weather
        // City + Selection
        // Temperature:  ------
        // Description:  -----  + Humidity:     ------

        setLayout(new GridLayout(5, 1, 0, 4));

        // Title
        JLabel title = new JLabel("Weather", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        // City + Load
        JPanel cityRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        cityRow.add(new JLabel("City:"));
        cityRow.add(cityBox);
        cityRow.add(loadButton);

        // Temperature
        JPanel tempRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tempRow.add(tempLabel);

        // Description
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        infoRow.add(descLabel);
        infoRow.add(Box.createHorizontalStrut(10));
        infoRow.add(humidityLabel);

        // Status
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusRow.add(statusLabel);
        statusLabel.setFont(statusLabel.getFont().deriveFont(
                statusLabel.getFont().getSize2D() - 1f));

        add(title);
        add(cityRow);
        add(tempRow);
        add(infoRow);
        add(statusRow);

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
        statusLabel.setText("Loading...");

        SwingWorker<JSONObject, Void> worker = new SwingWorker<>() {
            @Override
            protected JSONObject doInBackground() throws Exception {
                return weatherDAO.fetchCurrentWeather(city);
            }

            @Override
            protected void done() {
                loadButton.setEnabled(true);
                try {
                    // Get JSON Body, detailed structure please refer to https://openweathermap.org/current#geo
                    // Or you can find a copy in the WeatherDataAccessObject file's bottom.
                    JSONObject root = get();

                    // Get Temperature and Humidity
                    JSONObject main = root.getJSONObject("main");
                    double temp = main.optDouble("temp", Double.NaN);
                    double humidity = main.optDouble("humidity", Double.NaN);

                    // Description of Weather
                    String description = "-";
                    if (root.has("weather")) {
                        var arr = root.getJSONArray("weather");
                        if (arr.length() > 0) {
                            description = arr.getJSONObject(0)
                                    .optString("description", "-");
                        }
                    }

                    // Update UI Information
                    tempLabel.setText("Temperature: " +
                            (Double.isNaN(temp) ? "-" : temp + " °C"));
                    humidityLabel.setText("Humidity: " +
                            (Double.isNaN(humidity) ? "-" : humidity + " %"));
                    descLabel.setText("Description: " + description);
                    statusLabel.setText("Updated.");

                } catch (Exception ex) {
                    tempLabel.setText("Temperature: -");
                    humidityLabel.setText("Humidity: -");
                    descLabel.setText("Description: -");
                    statusLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
