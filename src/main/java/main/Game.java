package main;

import entities.Player;
import levels.LevelManager;

import java.awt.*;

public class Game implements Runnable {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;
    private Player player;
    private LevelManager levelManager;
    public final static int TILES_DEFAULT_SIZE = 32;
    // scale should always be a round number / level and player are now scalable
    public final static float SCALE = 2f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    // actual size of tiles
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    // make game flexible sized to tileSize
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;


    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        // combine panel and frame in constructor parameter
        gameWindow = new GameWindow(gamePanel);
        // make sure inputs are recognized inside window
        gamePanel.requestFocus();
        // starting infinite gameLoop, all methods go before here
        startGameLoop();
    }

    private void initClasses() {
        player = new Player(200, 200, (int) (64 * SCALE), (int) (40 * SCALE));
        levelManager = new LevelManager(this);
    }

    // run the gameLoop in own thread to keep performance stable
    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void update() {
        levelManager.update();
        player.update();
        player.loadLevelData(levelManager.getCurrentLevel().getLvlData());

    }

    public void render(Graphics g) {
        levelManager.draw(g);
        // player should be drawn "on top" of level, or it will not be visible
        player.render(g);
    }
    // gameLoop advanced
    @Override
    public void run() {
        // how long does 1 frame last in nanoseconds?
        double timePerFrame = 1000000000.0 / FPS_SET;
        // ups makes sure game runs smooth on all systems
        double timePerUpdate = 1000000000.0 / UPS_SET;
        long previousTime = System.nanoTime();
        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();
        double deltaU = 0;
        double deltaF = 0;
        while (true) {
            // make sure we can catch up when time is "lost" or frame got faster because of render
            long currentTime = System.nanoTime();
            // will be 1 when time is lost
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                // avoid lagging because of diff architecture of drawing and image buffering
                Toolkit.getDefaultToolkit().sync();
                frames++;
                deltaF--;
            }
            // how many frames in last second?
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
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
}
