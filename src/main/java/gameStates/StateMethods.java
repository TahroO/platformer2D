package gameStates;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
// make sure all gameStates have these methods
public interface StateMethods {
    public void update();
    public void draw(Graphics g);
    public void mouseClicked(MouseEvent event);
    public void mousePressed(MouseEvent event);
    public void mouseReleased(MouseEvent event);
    public void mouseMoved(MouseEvent event);
    public void keyPressed(KeyEvent event);
    public void keyReleased(KeyEvent event);
}
