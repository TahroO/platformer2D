package main;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;


public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game) {
        mouseInputs = new MouseInputs(this);
        // set size of gameWindow make sure size is without frameSize
        setPanelSize();
        // add moves and clicks
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        // refactored keyListener holding THIS gamePanel
        addKeyListener(new KeyboardInputs(this));
        this.game = game;
    }

    private void setPanelSize() {
        // dimension is used as parameter for setSize
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
    }

    public void updateGame() {

    }
    public void paintComponent(Graphics g) {
        // calling jPanel constructor - clearing every frame
        super.paintComponent(g);
        game.render(g);
    }
    public Game getGame() {
        return game;
    }

}
