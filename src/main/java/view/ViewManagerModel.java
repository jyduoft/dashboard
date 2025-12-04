package view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {
    private String activeViewName;
    private entity.User currentUser;
    private PokemonPanel pokemonPanel;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setActiveView(String activeViewName) {
        this.activeViewName = activeViewName;
    }

    public String getActiveView() {
        return activeViewName;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("view", null, activeViewName);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setCurrentUser(entity.User user) {
        entity.User oldUser = this.currentUser;
        this.currentUser = user;
        support.firePropertyChange("currentUser", oldUser, user);
    }

    public entity.User getCurrentUser() {
        return currentUser;
    }

    public void setPokemonPanel(PokemonPanel panel) {
        PokemonPanel oldPanel = this.pokemonPanel;
        this.pokemonPanel = panel;
        support.firePropertyChange("pokemonPanel", oldPanel, panel);
    }

    public PokemonPanel getPokemonPanel() {
        return pokemonPanel;
    }
}