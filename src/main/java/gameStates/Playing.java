package gameStates;

import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.PauseOverlay;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static main.Game.SCALE;

public class Playing extends State implements StateMethods {
    private Player player;
    private LevelManager levelManager;
    private boolean isPause = false;
    private PauseOverlay pauseOverlay;
    private int xLevelOffset;
    private int leftBorder = (int)(0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int)(0.8 * Game.GAME_WIDTH);
    private int lvlTilesWide = LoadSave.getLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private int maxLvlOffset = maxTilesOffset * Game.TILES_SIZE;


    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE));
        player.loadLevelData(levelManager.getCurrentLevel().getLvlData());
        pauseOverlay = new PauseOverlay(this);
    }

    @Override
    public void update() {
        if (!isPause) {
            levelManager.update();
            player.update();
            checkCloseToBorder();
        } else {
            pauseOverlay.update();
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int)(player.getHitBox().x);
        int diff = playerX - maxLvlOffset;
        if (diff > rightBorder) {
            xLevelOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLevelOffset += diff - leftBorder;
        }
    }

    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        player.render(g);
        if (isPause) {
            pauseOverlay.draw(g);
        }

    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            player.setAttacking(true);
        }
    }
    public void mouseDragged(MouseEvent event) {
        if (isPause) {
            pauseOverlay.mouseDragged(event);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (isPause) {
            pauseOverlay.mousePressed(event);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (isPause) {
            pauseOverlay.mouseReleased(event);
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (isPause) {
            pauseOverlay.mouseMoved(event);
        }
    }
    public void unpauseGame() {
        isPause = false;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        // change values to move objects
        switch (event.getKeyCode()) {
            // left
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            // right
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            // jump
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_ESCAPE:
                isPause = !isPause;
        }

    }

    @Override
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;
        }
    }

    public Player getPlayer() {
        return player;
    }

    // if focus is lost stop moving etc.
    public void windowFocusLost() {
        player.resetDirBooleans();
    }
}
