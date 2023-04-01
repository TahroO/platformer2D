package ui;

import gameStates.GameState;
import main.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

import static utils.Constants.UI.PauseButtons.SOUND_SIZE;
import static utils.Constants.UI.VolumeButtons.SLIDER_WIDTH;
import static utils.Constants.UI.VolumeButtons.VOLUME_HEIGHT;

public class AudioOptions {
    private VolumeButton volumeButton;
    private SoundButton musicButton, sfxButton;
    public AudioOptions() {
        createSoundButtons();
        createVolumeButton();
    }
    private void createVolumeButton() {
        int vX = (int) (309 * Game.SCALE);
        int vY = (int) (278 * Game.SCALE);
        volumeButton = new VolumeButton(vX, vY, SLIDER_WIDTH, VOLUME_HEIGHT);
    }
    private void createSoundButtons() {
        int soundX = (int) (450 * Game.SCALE);
        int musicY = (int) (140 * Game.SCALE);
        int sfxY = (int) (186 * Game.SCALE);
        musicButton = new SoundButton(soundX, musicY, SOUND_SIZE, SOUND_SIZE);
        sfxButton = new SoundButton(soundX, sfxY, SOUND_SIZE, SOUND_SIZE);
    }
    public void update() {
        musicButton.update();
        sfxButton.update();
        volumeButton.update();
    }
    public void draw(Graphics g) {
        // soundButtons
        musicButton.draw(g);
        sfxButton.draw(g);
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
        }

        musicButton.resetBooleans();
        sfxButton.resetBooleans();
        volumeButton.resetBooleans();
    }


    public void mouseMoved(MouseEvent event) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        volumeButton.setMouseOver(false);
        if (isIn(event, musicButton)) {
            musicButton.setMouseOver(true);
        } else if (isIn(event, sfxButton)) {
            sfxButton.setMouseOver(true);
        } else if (isIn(event, volumeButton)) {
            volumeButton.setMouseOver(true);
        }
    }
    private boolean isIn(MouseEvent event, PauseButton button) {
        return button.getBounds().contains(event.getX(), event.getY());
    }
}
