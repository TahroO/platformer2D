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
    private SoundButton musicButton, sfxButton;
    private UrmButton menuB, replayB, unpauseB;
    private VolumeButton volumeButton;

    public PauseOverlay(Playing playing) {
        this.playing = playing;
        loadBackground();
        createSoundButtons();
        createUrmButtons();
        createVolumeButton();
    }

    private void createVolumeButton() {
        int vX = (int) (309 * Game.SCALE);
        int vY = (int) (278 * Game.SCALE);
        volumeButton = new VolumeButton(vX, vY, SLIDER_WIDTH, VOLUME_HEIGHT);
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

    private void createSoundButtons() {
        int soundX = (int) (450 * Game.SCALE);
        int musicY = (int) (140 * Game.SCALE);
        int sfxY = (int) (186 * Game.SCALE);
        musicButton = new SoundButton(soundX, musicY, SOUND_SIZE, SOUND_SIZE);
        sfxButton = new SoundButton(soundX, sfxY, SOUND_SIZE, SOUND_SIZE);
    }

    private void loadBackground() {
        backgroundImage = LoadSave.getSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgWidth = (int) (backgroundImage.getWidth() * Game.SCALE);
        bgHeight = (int) (backgroundImage.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgWidth / 2;
        bgY = (int) (25 * Game.SCALE);
    }

    public void update() {
        musicButton.update();
        sfxButton.update();
        menuB.update();
        replayB.update();
        unpauseB.update();
        volumeButton.update();
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImage, bgX, bgY, bgWidth, bgHeight, null);
        // soundButtons
        musicButton.draw(g);
        sfxButton.draw(g);
        // urmButtons
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);
        volumeButton.draw(g);
    }

    public void mouseDragged(MouseEvent event) {
        if (volumeButton.isMousePressed()) {
            volumeButton.changeX(event.getX());
        }
    }

    public void mousePressed(MouseEvent event) {
        if (isIn(event, musicButton)) {
            musicButton.setMousePressed(true);
        } else if (isIn(event, sfxButton)) {
            sfxButton.setMousePressed(true);
        } else if (isIn(event, menuB)) {
            menuB.setMousePressed(true);
        } else if (isIn(event, replayB)) {
            replayB.setMousePressed(true);
        } else if (isIn(event, unpauseB)) {
            unpauseB.setMousePressed(true);
        } else if (isIn(event, volumeButton)) {
        volumeButton.setMousePressed(true);
    }
    }


    public void mouseReleased(MouseEvent event) {
        if (isIn(event, musicButton)) {
            if (musicButton.isMousePressed()) {
                musicButton.setMuted(!musicButton.isMuted());
            }
        } else if (isIn(event, sfxButton)) {
            if (sfxButton.isMousePressed()) {
                sfxButton.setMuted(!sfxButton.isMuted());
            }
        } else if (isIn(event, menuB)) {
            if (menuB.isMousePressed()) {
                GameState.state = GameState.MENU;
                playing.unpauseGame();
            }
        } else if (isIn(event, replayB)) {
            if (replayB.isMousePressed()) {
                System.out.println("replay lvl!");
            }
        } else if (isIn(event, unpauseB)) {
            if (unpauseB.isMousePressed()) {
                playing.unpauseGame();
            }
        }
        musicButton.resetBooleans();
        sfxButton.resetBooleans();
        menuB.resetBooleans();
        replayB.resetBooleans();
        unpauseB.resetBooleans();
        volumeButton.resetBooleans();
    }


    public void mouseMoved(MouseEvent event) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);
        volumeButton.setMouseOver(false);
        if (isIn(event, musicButton)) {
            musicButton.setMouseOver(true);
        } else if (isIn(event, sfxButton)) {
            sfxButton.setMouseOver(true);
        } else if (isIn(event, menuB)) {
            menuB.setMouseOver(true);
        } else if (isIn(event, replayB)) {
            replayB.setMouseOver(true);
        } else if (isIn(event, unpauseB)) {
            unpauseB.setMouseOver(true);
        } else if (isIn(event, volumeButton)) {
            volumeButton.setMouseOver(true);
        }
    }
    private boolean isIn(MouseEvent event, PauseButton button) {
        return button.getBounds().contains(event.getX(), event.getY());
    }

}
