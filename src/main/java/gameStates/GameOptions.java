package gameStates;

import main.Game;
import ui.AudioOptions;
import ui.PauseButton;
import ui.UrmButton;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.URM_SIZE;

public class GameOptions extends State implements StateMethods {
    private AudioOptions audioOptions;
    private BufferedImage backgroundImg, optionsBackgroundImg;
    private int bgX, bgY, bgW, bgH;
    private UrmButton menuB;

    public GameOptions(Game game) {
        super(game);
        loadImgs();
        loadButtons();
        audioOptions = game.getAudioOptions();
    }

    private void loadButtons() {
        int menuX = (int) (387 * Game.SCALE);
        int menuY = (int) (325 * Game.SCALE);
        menuB = new UrmButton(menuX, menuY, URM_SIZE, URM_SIZE, 2);
    }

    private void loadImgs() {
        backgroundImg = LoadSave.getSpriteAtlas(LoadSave.MENU_BACKGROUND_IMAGE);
        optionsBackgroundImg = LoadSave.getSpriteAtlas(LoadSave.OPTIONS_SCREEN);
        bgW = (int) (optionsBackgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (optionsBackgroundImg.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (33 * Game.SCALE);
    }

    @Override
    public void update() {
        menuB.update();
        audioOptions.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(optionsBackgroundImg, bgX, bgY, bgW, bgH, null);
        menuB.draw(g);
        audioOptions.draw(g);
    }
    public void mouseDragged(MouseEvent event) {
        audioOptions.mouseDragged(event);
    }

    @Override
    public void mouseClicked(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (isIn(event, menuB)) {
            menuB.setMousePressed(true);
        } else {
            audioOptions.mousePressed(event);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (isIn(event, menuB) && menuB.isMousePressed()) {
            GameState.state = GameState.MENU;
        } else {
            audioOptions.mouseReleased(event);
        }
        menuB.resetBooleans();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        menuB.setMouseOver(false);
        if (isIn(event, menuB)) {
            menuB.setMouseOver(true);
        } else {
            audioOptions.mouseMoved(event);
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE)  {
            GameState.state = GameState.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {

    }
    private boolean isIn(MouseEvent event, PauseButton button) {
        return button.getBounds().contains(event.getX(), event.getY());
    }
}
