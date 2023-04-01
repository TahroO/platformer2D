package main;

import gameStates.GameState;
import gameStates.Menu;
import gameStates.*;
import ui.AudioOptions;
import utils.LoadSave;


import java.awt.*;

public class Game implements Runnable {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;
    private Playing playing;
    private Menu menu;
    private GameOptions gameOptions;
    private AudioOptions audioOptions;
    public final static int TILES_DEFAULT_SIZE = 32;
    // scale should always be a round number / level and player scalable
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
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        // starting infinite gameLoop, all methods go before here
        startGameLoop();
    }

    private void initClasses() {
        audioOptions = new AudioOptions();
        menu = new Menu(this);
        playing = new Playing(this);
        gameOptions = new GameOptions(this);
    }

    // run the gameLoop in own thread to keep performance stable
    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        switch (GameState.state) {
            case MENU:
                menu.update();
                break;
            case PLAYING:
                playing.update();
                break;
            case OPTIONS:
                gameOptions.update();
                break;
            case QUIT:
            default:
                System.exit(0);
                break;

        }

    }

    public void render(Graphics g) {
        switch (GameState.state) {
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case OPTIONS:
                gameOptions.draw(g);
                break;
            default:
                break;
        }
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

    // if focus is lost stop moving etc.
    public void windowFocusLost() {
        if (GameState.state == GameState.PLAYING) {
            playing.getPlayer().resetDirBooleans();
        }
    }
    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }
    public GameOptions getGameOptions() {
        return gameOptions;
    }
    public AudioOptions getAudioOptions() {
        return audioOptions;
    }
}
