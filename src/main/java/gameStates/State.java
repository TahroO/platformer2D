package gameStates;

import audio.AudioPlayer;
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
    public void setGameState(GameState gameState) {
        switch (gameState) {
            case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
            case PLAYING -> game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getlvl());
        }
        GameState.state = gameState;
    }
}
