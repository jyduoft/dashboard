package interface_adapter;

import data_access.PokemonDataAccessObject;
import entity.Pokemon;
import entity.User;
import use_cases.PokemonManager;
import use_cases.login.LoginOutputBoundary;
import view.PokemonPanel;
import view.ViewManagerModel;

import javax.swing.*;

public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(String username, String password) {

        // --------------------------------------------------
        // 1. Build User object for the logged-in user
        // --------------------------------------------------
        User realUser = new User(username, password);
        viewManagerModel.setCurrentUser(realUser);

        // --------------------------------------------------
        // 2. Pokémon Initialization (ONLY RUNS AFTER LOGIN)
        // --------------------------------------------------
        try {
            PokemonDataAccessObject pokemonDAO = new PokemonDataAccessObject();
            PokemonManager pokemonManager = new PokemonManager(realUser);

            // Ensure user has a starter Pokémon
            if (pokemonManager.getUserInv().isEmpty()
                    || pokemonManager.getCurrentPokemon() == null) {

                Pokemon starter = new Pokemon(
                        "Charmander",
                        "src/main/resources/cache/pokemon/4.gif",
                        4,
                        1,
                        0,
                        100,
                        100
                );

                pokemonManager.getUserInv().add(starter);
                pokemonManager.setCurrentPokemon(starter);
            }
            
            // Give users 100 coins if they have 0 coins (for testing)
            if (pokemonManager.getCoins() == 0) {
                pokemonManager.setCoins(100);
            }

            // Save updated data to Firebase
            try {
                pokemonDAO.saveUserData(realUser, pokemonManager);
            } catch (Exception ex) {
                System.err.println("Error saving user data: " + ex.getMessage());
            }

            // Download sprites if missing
            pokemonDAO.fetchPokemonSprites(pokemonManager.getUserInv());

            // Build Pokémon panel
            String imgPath = pokemonManager.getCurrentPokemon().getImgFilePath();
            ImageIcon icon = new ImageIcon(imgPath);
            
            // Verify image loaded correctly
            if (icon.getIconWidth() <= 0) {
                // Try to load as file if resource path didn't work
                java.io.File imgFile = new java.io.File(imgPath);
                if (imgFile.exists()) {
                    icon = new ImageIcon(imgFile.getAbsolutePath());
                } else {
                    System.err.println("Warning: Pokemon image not found at: " + imgPath);
                }
            }
            
            PokemonPanel pokemonPanel = new PokemonPanel(icon);

            // Store panel in the ViewManagerModel so Main can use it
            viewManagerModel.setPokemonPanel(pokemonPanel);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // --------------------------------------------------
        // 3. Switch to Dashboard View
        // --------------------------------------------------
        viewManagerModel.setActiveView("Dashboard");
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        loginViewModel.setError(error);
    }
}
