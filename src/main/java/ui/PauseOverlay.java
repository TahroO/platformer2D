package ui;

import gameStates.GameState;
import gameStates.Playing;
import main.Game;
import main.GameWindow;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.PauseButtons.SOUND_SIZE;
import static utils.Constants.UI.URMButtons.URM_SIZE;
import static utils.Constants.UI.VolumeButtons.*;

public class PauseOverlay {
    private BufferedImage backgroundImage;
    private Playing playing;
    private int bgX, bgY, bgWidth, bgHeight;
    private AudioOptions audioOptions;

    private UrmButton menuB, replayB, unpauseB;


    public PauseOverlay(Playing playing) {
        this.playing = playing;
        loadBackground();
        audioOptions = playing.getGame().getAudioOptions();
        createUrmButtons();

    }



    private void createUrmButtons() {
        int menuX = (int)(313 * Game.SCALE);
        int replayX = (int)(387 * Game.SCALE);
        int unpauseX = (int)(462 * Game.SCALE);
        int bY = (int)(325 * Game.SCALE);

        menuB = new UrmButton(menuX, bY, URM_SIZE, URM_SIZE, 2);
        replayB = new UrmButton(replayX, bY, URM_SIZE, URM_SIZE, 1);
        unpauseB = new UrmButton(unpauseX, bY, URM_SIZE, URM_SIZE, 0);
    }



    private void loadBackground() {
        backgroundImage = LoadSave.getSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgWidth = (int) (backgroundImage.getWidth() * Game.SCALE);
        bgHeight = (int) (backgroundImage.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgWidth / 2;
        bgY = (int) (25 * Game.SCALE);
    }

    public void update() {

        menuB.update();
        replayB.update();
        unpauseB.update();
        audioOptions.update();

    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImage, bgX, bgY, bgWidth, bgHeight, null);

        // urmButtons
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);
        audioOptions.draw(g);

    }

    public void mouseDragged(MouseEvent event) {
        audioOptions.mouseDragged(event);
    }

    public void mousePressed(MouseEvent event) {
        if (isIn(event, menuB)) {
            menuB.setMousePressed(true);
        } else if (isIn(event, replayB)) {
            replayB.setMousePressed(true);
        } else if (isIn(event, unpauseB)) {
            unpauseB.setMousePressed(true);
        } else {
            audioOptions.mousePressed(event);
        }
    }


    public void mouseReleased(MouseEvent event) {
         if (isIn(event, menuB)) {
            if (menuB.isMousePressed()) {
                GameState.state = GameState.MENU;
                playing.unpauseGame();
            }
        } else if (isIn(event, replayB)) {
            if (replayB.isMousePressed()) {
                playing.resetAll();
                playing.unpauseGame();
            }
        } else if (isIn(event, unpauseB)) {
            if (unpauseB.isMousePressed()) {
                playing.unpauseGame();
            }
        } else {
             audioOptions.mouseReleased(event);
         }
        menuB.resetBooleans();
        replayB.resetBooleans();
        unpauseB.resetBooleans();
    }


    public void mouseMoved(MouseEvent event) {
        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);
        if (isIn(event, menuB)) {
            menuB.setMouseOver(true);
        } else if (isIn(event, replayB)) {
            replayB.setMouseOver(true);
        } else if (isIn(event, unpauseB)) {
            unpauseB.setMouseOver(true);
        } else {
            audioOptions.mouseMoved(event);
        }
    }
    private boolean isIn(MouseEvent event, PauseButton button) {
        return button.getBounds().contains(event.getX(), event.getY());
    }

}
