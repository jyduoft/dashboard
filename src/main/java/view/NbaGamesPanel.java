package view;

import org.json.JSONArray;
import org.json.JSONObject;
import use_cases.NbaGamesDataAccessInterface;

import javax.swing.*;
import java.awt.*;
/**
 * This is sports panel, user can check the live nba games via a live-box
 * NOTE1: the API I founded and used here can not retrieve live data with a free account.
 *        So I write some mock data here to show how it (possibly) functions.
 * NOTE2: I have left the API interface here, when one day we get the access to retrieve live data,
 *        We could simply replace these keys and visit trail.
 * NOTE3: Maybe we could further expand this part that can include more sports (like first select
 *        soccer, NBA, MLB, etc.) and also provides the options to set (Favorite Team), but these are
 *        the future work haha. :)
 */

public class NbaGamesPanel extends JPanel {

    private final NbaGamesDataAccessInterface nbaDAO;

    private final JButton refreshButton = new JButton("Refresh");
    private final JTextArea gamesArea = new JTextArea(8, 24);
    private final JLabel statusLabel = new JLabel(" ");

    private final Timer autoRefreshTimer;

    public NbaGamesPanel(NbaGamesDataAccessInterface nbaDAO) {
        this.nbaDAO = nbaDAO;

        setLayout(new BorderLayout(5, 5));

        // Title
        JLabel title = new JLabel("NBA Live Scores", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topRow.add(refreshButton);
        topRow.add(new JLabel("(Simulated data)"));

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(title);
        northPanel.add(topRow);

        gamesArea.setEditable(false);
        gamesArea.setLineWrap(true);
        gamesArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(gamesArea);

        statusLabel.setFont(statusLabel.getFont().deriveFont(
                statusLabel.getFont().getSize2D() - 1f));

        add(northPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.SOUTH);

        // set refresh button
        refreshButton.addActionListener(e -> loadLiveGames());

        autoRefreshTimer = new Timer(30_000, e -> loadLiveGames());
        autoRefreshTimer.start();

        loadLiveGames();
    }

    private void loadLiveGames() {
        refreshButton.setEnabled(false);
        statusLabel.setText("Loading...");

        SwingWorker<JSONArray, Void> worker = new SwingWorker<>() {
            @Override
            protected JSONArray doInBackground() throws Exception {
                return nbaDAO.fetchLiveBoxScores();
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                try {
                    JSONArray games = get();

                    if (games.isEmpty()) {
                        gamesArea.setText("No live games right now.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < games.length(); i++) {
                            JSONObject g = games.getJSONObject(i);

                            JSONObject home = g.getJSONObject("home_team");
                            JSONObject visitor = g.getJSONObject("visitor_team");

                            String homeName = home.optString("abbreviation",
                                    home.optString("name", "HOME"));
                            String visitorName = visitor.optString("abbreviation",
                                    visitor.optString("name", "AWAY"));

                            int homeScore = g.optInt("home_team_score", 0);
                            int visitorScore = g.optInt("visitor_team_score", 0);
                            String status = g.optString("status", "");
                            int period = g.optInt("period", 0);
                            String time = g.optString("time", "");

                            sb.append(visitorName)
                                    .append(" ")
                                    .append(visitorScore)
                                    .append("  @  ")
                                    .append(homeName)
                                    .append(" ")
                                    .append(homeScore);

                            sb.append("   [")
                                    .append(status);
                            if (period > 0) {
                                sb.append(", Q").append(period);
                            }
                            if (!time.isEmpty()) {
                                sb.append(" ").append(time);
                            }
                            sb.append("]\n");
                        }
                        gamesArea.setText(sb.toString());
                    }

                    statusLabel.setText("Updated.");
                } catch (Exception ex) {
                    gamesArea.setText("");
                    statusLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }
}
