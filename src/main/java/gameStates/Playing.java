package gameStates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverlay;
import ui.PauseOverlay;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static main.Game.SCALE;
import static utils.Constants.Environment.*;

public class Playing extends State implements StateMethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private boolean isPause = false;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private int xLevelOffset;
    private int leftBorder = (int)(0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int)(0.8 * Game.GAME_WIDTH);
    private int lvlTilesWide = LoadSave.getLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private int maxLvlOffset = maxTilesOffset * Game.TILES_SIZE;
    private int[] smallCloudPos;
    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private Random random = new Random();
    private boolean gameOver;
    public Playing(Game game) {
        super(game);
        initClasses();
        backgroundImg = LoadSave.getSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMAGE);
        bigCloud = LoadSave.getSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.getSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudPos = new int[8];
        for(int i = 0; i < smallCloudPos.length; i++) {
            smallCloudPos[i] = (int)(90 * SCALE) + random.nextInt((int)(100 * SCALE));
        }
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLevelData(levelManager.getCurrentLevel().getLvlData());
        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
    }

    @Override
    public void update() {
        if (!isPause && !gameOver) {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkCloseToBorder();
        } else {
            pauseOverlay.update();
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int)(player.getHitBox().x);
        int diff = playerX - xLevelOffset;
        if (diff > rightBorder) {
          xLevelOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLevelOffset += diff - leftBorder;
        }
        if (xLevelOffset > maxLvlOffset) {
            xLevelOffset = maxLvlOffset;
        } else if (xLevelOffset < 0) {
            xLevelOffset = 0;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);
        levelManager.draw(g, xLevelOffset);
        player.render(g, xLevelOffset);
        enemyManager.draw(g, xLevelOffset);
        if (isPause) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver) {
            gameOverOverlay.draw(g);
        }
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 3; i++) {
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int)(xLevelOffset * 0.3), (int)(204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);
        }
        for (int i = 0; i < smallCloudPos.length; i++) {
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int)(xLevelOffset * 0.7), smallCloudPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
        }
    }
    public void resetAll() {
        // reset player, enemy, lvl etc.
        gameOver = false;
        isPause = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (!gameOver) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                player.setAttacking(true);
            }
        }
    }
    public void mouseDragged(MouseEvent event) {
        if (!gameOver) {
            if (isPause) {
                pauseOverlay.mouseDragged(event);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (!gameOver) {
            if (isPause) {
                pauseOverlay.mousePressed(event);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (!gameOver) {
            if (isPause) {
                pauseOverlay.mouseReleased(event);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (!gameOver) {
            if (isPause) {
                pauseOverlay.mouseMoved(event);
            }
        }
    }
    public void unpauseGame() {
        isPause = false;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (gameOver) {
            gameOverOverlay.keyPressed(event);
        } else {
            // change values to move objects
            switch (event.getKeyCode()) {
                // left
                case KeyEvent.VK_A -> player.setLeft(true);

                // right
                case KeyEvent.VK_D -> player.setRight(true);

                // jump
                case KeyEvent.VK_SPACE -> player.setJump(true);
                case KeyEvent.VK_ESCAPE -> isPause = !isPause;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (!gameOver) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_A -> player.setLeft(false);
                case KeyEvent.VK_D -> player.setRight(false);
                case KeyEvent.VK_SPACE -> player.setJump(false);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    // if focus is lost stop moving etc.
    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
