package ui;

import main.Game;
import main.GameWindow;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.PauseButtons.SOUND_SIZE;
import static utils.Constants.UI.URMButtons.URM_SIZE;

public class PauseOverlay {
    private BufferedImage backgroundImage;
    private int bgX, bgY, bgWidth, bgHeight;
    private SoundButton musicButton, sfxButton;
    private UrmButton menuB, replayB, unpauseB;

    public PauseOverlay() {
        loadBackground();
        createSoundButtons();
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
    }

    public void mouseDragged(MouseEvent event) {

    }

    public void mousePressed(MouseEvent event) {
        if (isIn(event, musicButton)) {
            musicButton.setMousePressed(true);
        } else if (isIn(event, sfxButton)) {
            sfxButton.setMousePressed(true);
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
        }
        musicButton.resetBooleans();
        sfxButton.resetBooleans();
    }


    public void mouseMoved(MouseEvent event) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        if (isIn(event, musicButton)) {
            musicButton.setMouseOver(true);
        } else if (isIn(event, sfxButton)) {
            sfxButton.setMouseOver(true);
        }
    }
    private boolean isIn(MouseEvent event, PauseButton button) {
        return button.getBounds().contains(event.getX(), event.getY());
    }

}
