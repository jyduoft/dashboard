package view;

import data_access.StockDataAccessObject;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

/**
 * A simple Stock price check panel
 * input the symbol of a stock (code)
 * Press Load will show the current price
 *
 * some test cases symbol
 * --Technology--
 * Apple: AAPL     Tesla: TSLA
 * NVIDIA: NVDA    Microsoft: MSFT
 * Google: GOOGL   Amazon: AMZN
 * Meta/Facebook: META
 * --Finance--
 * JPMorgan: JPM  Bank of America: BAC
 */
public class StockPanel extends JPanel {

    private final StockDataAccessObject stockDAO;

    private final JTextField symbolField = new JTextField(8);
    private final JButton loadButton = new JButton("Load");
    private final JLabel priceLabel = new JLabel("Price: -");
    private final JLabel nameLabel = new JLabel("Name: -");
    private final JLabel industryLabel = new JLabel("Industry: -");

    private final JLabel statusLabel = new JLabel(" ");

    public StockPanel(StockDataAccessObject stockDAO) {
        this.stockDAO = stockDAO;

        setLayout(new BorderLayout());

        //##------- Stock Panel Components.----------##
        JLabel title = new JLabel("Stocks");
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JLabel symbolLabel = new JLabel("Symbol:");

        JPanel symbolRow = new JPanel(new BorderLayout(5, 0));
        symbolRow.add(symbolLabel, BorderLayout.WEST);
        symbolRow.add(symbolField, BorderLayout.CENTER);
        symbolRow.add(loadButton, BorderLayout.EAST);
        symbolRow.setAlignmentX(LEFT_ALIGNMENT);

        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        infoRow.add(priceLabel);
        infoRow.add(statusLabel);
        infoRow.setAlignmentX(LEFT_ALIGNMENT);

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        nameRow.add(nameLabel);

        JPanel industryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        industryRow.add(industryLabel);

        // Set how to show in the stock panel
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(title);
        north.add(Box.createVerticalStrut(4));
        north.add(symbolRow);
        north.add(Box.createVerticalStrut(4));
        north.add(infoRow);
        north.add(Box.createVerticalStrut(4));
        north.add(nameRow);
        north.add(Box.createVerticalStrut(4));
        north.add(industryRow);

        add(north, BorderLayout.NORTH);

        loadButton.addActionListener(e -> onLoad());
    }

    private void onLoad() {
        String symbol = symbolField.getText().trim().toUpperCase();
        if (symbol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a stock symbol.");
            return;
        }

        loadButton.setEnabled(false);
        statusLabel.setText("Loading...");

        SwingWorker<Double, Void> worker = new SwingWorker<>() {
            @Override
            protected Double doInBackground() throws Exception {
                return stockDAO.fetchPrice(symbol);
            }

            @Override
            protected void done() {
                loadButton.setEnabled(true);
                try {
                    double price = get();
                    priceLabel.setText("Price: " + price);
                    statusLabel.setText("Updated.");
                    JSONObject profile = stockDAO.fetchCompanyProfile(symbolField.getText().trim().toUpperCase());
                    String name = profile.optString("name", "-");
                    String industry = profile.optString("finnhubIndustry", "-");

                    nameLabel.setText("Name: " + name);
                    industryLabel.setText("Industry: " + industry);

                } catch (Exception ex) {
                    priceLabel.setText("Price: -");
                    statusLabel.setText("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
