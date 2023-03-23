package gameStates;

import main.Game;
import ui.MenuButton;

import java.awt.event.MouseEvent;

public class State {
    protected Game game;
    public State(Game game) {
        this.game = game;
    }

    public boolean isIn(MouseEvent event, MenuButton button) {
        return button.getBounds().contains(event.getX(), event.getY());
    }
    public Game getGame() {
        return game;
    }
}
