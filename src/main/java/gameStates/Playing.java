package gameStates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompleteOverlay;
import ui.PauseOverlay;
import utils.Constants;
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
    private ObjectManager objectManager;
    private boolean isPause = false;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompleteOverlay levelCompleteOverlay;
    private int xLevelOffset;
    private int leftBorder = (int)(0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int)(0.8 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;
    private int[] smallCloudPos;
    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private Random random = new Random();
    private boolean gameOver;
    private boolean lvlCompleted;
    private boolean playerDying;
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
        calcLvlOffset();
        loadStartLevel();
    }

    public void loadNextLevel() {
        resetAll();
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }
    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObject(levelManager.getCurrentLevel());
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getMaxLvlOffsetX();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLevelData(levelManager.getCurrentLevel().getLvlData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompleteOverlay = new LevelCompleteOverlay(this);
    }

    @Override
    public void update() {
        if (isPause) {
            pauseOverlay.update();
        } else if (lvlCompleted) {
            levelCompleteOverlay.update();
        } else if (gameOver) {
            gameOverOverlay.update();
        } else if (playerDying) {
            player.update();
        } else {
            levelManager.update();
            player.update();
            objectManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkCloseToBorder();
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
        if (xLevelOffset > maxLvlOffsetX) {
            xLevelOffset = maxLvlOffsetX;
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
        objectManager.draw(g, xLevelOffset);
        if (isPause) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver) {
            gameOverOverlay.draw(g);
        } else if (lvlCompleted) {
            levelCompleteOverlay.draw(g);
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
        lvlCompleted = false;
        playerDying = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
        objectManager.resetAllObjects();
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
            } else if (lvlCompleted){
                levelCompleteOverlay.mousePressed(event);
            }
        }  else {
            gameOverOverlay.mousePressed(event);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (!gameOver) {
            if (isPause) {
                pauseOverlay.mouseReleased(event);
            } else if (lvlCompleted){
                levelCompleteOverlay.mouseReleased(event);
            }
        } else {
            gameOverOverlay.mouseReleased(event);
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (!gameOver) {
            if (isPause) {
                pauseOverlay.mouseMoved(event);
            } else if (lvlCompleted){
                levelCompleteOverlay.mouseMoved(event);
            }
        } else {
            gameOverOverlay.mouseMoved(event);
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
    public void checkPotionTouched(Rectangle2D.Float hitBox) {
        objectManager.checkObjectTouched(hitBox);
    }
    public void checkSpikesTouched(Player player) {
        objectManager.checkSpikesTouched(player);
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        objectManager.checkObjectHit(attackBox);
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public void setMaxLvlOffsetX(int lvlOffset)  {
        this.maxLvlOffsetX = lvlOffset;
    }
    public void setLevelCompleted(boolean levelCompleted) {
        this.lvlCompleted = levelCompleted;
    }
    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public LevelManager getLevelManager() { return levelManager; }

    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }
}
