package ui;

import gameStates.GameState;
import gameStates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.URM_SIZE;

public class LevelCompleteOverlay {
    private Playing playing;
    private UrmButton menu, next;
    private BufferedImage img;
    private int bgX, bgY, bgW, bgH;
    public LevelCompleteOverlay(Playing playing) {
        this.playing = playing;
        initImg();
        initButtons();
    }

    private void initButtons() {
        int menuX = (int)(330 * Game.SCALE);
        int nextX = (int)(445 * Game.SCALE);
        int y = (int)(195 * Game.SCALE);
        next = new UrmButton(nextX, y, URM_SIZE, URM_SIZE, 0);
        menu = new UrmButton(menuX, y, URM_SIZE, URM_SIZE, 2);
    }

    private void initImg() {
        img = LoadSave.getSpriteAtlas(LoadSave.LEVEL_COMPLETE);
        bgW = (int)(img.getWidth() * Game.SCALE);
        bgH = (int)(img.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int)(75 - Game.SCALE);
    }
    public void draw(Graphics g) {
        g.drawImage(img, bgX, bgY, bgW, bgH, null);
        next.draw(g);
        menu.draw(g);
    }
    public void update() {
        next.update();
        menu.update();
    }
    private boolean isIn(UrmButton button, MouseEvent event) {
        return button.getBounds().contains(event.getX(), event.getY());
    }
    public void mouseMoved(MouseEvent event) {
        next.setMouseOver(false);
        menu.setMouseOver(false);
        if (isIn(menu, event)) {
            menu.setMouseOver(true);
        } else if (isIn(next, event)) {
            next.setMouseOver(true);
        }
    }
    public void mouseReleased(MouseEvent event) {
        if (isIn(menu, event)) {
            if (menu.isMousePressed()) {
                playing.resetAll();
                GameState.state = GameState.MENU;
            }
        } else if (isIn(next, event)) {
            if (next.isMousePressed()) {
                playing.loadNextLevel();
            }
        }
        menu.resetBooleans();
        next.resetBooleans();
    }
    public void mousePressed(MouseEvent event) {
        if (isIn(menu, event)) {
            menu.setMousePressed(true);
        } else if (isIn(next, event)) {
            next.setMousePressed(true);
        }
    }
}
