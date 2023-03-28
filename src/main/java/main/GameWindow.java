package main;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class GameWindow extends JFrame {
    private JFrame jFrame;
    // combine panel and frame in constructor parameter
    public GameWindow(GamePanel gamePanel) {
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // adding panel on frame
        jFrame.add(gamePanel);
        jFrame.setResizable(false);
        // window should fit panelSize
        jFrame.pack();
        // center window
        jFrame.setLocationRelativeTo(null);
        // setVisible always last call avoid bugs
        jFrame.setVisible(true);
        // check if focus is still in window
        jFrame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });
    }
}
