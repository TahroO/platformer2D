package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    // hitBox using rectangle2D
    protected Rectangle2D.Float hitBox;
    protected int aniTick, aniIndex;
    protected int state;
    protected float airSpeed;
    protected boolean inAir = false;
    protected int maxHealth;
    protected int currentHealth;
    protected Rectangle2D.Float attackBox;
    protected float walkSpeed = 1.0f * Game.SCALE;
    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    protected void drawHitBox(Graphics g, int xLvlOffset) {
        // for debugging hitBox
        g.setColor(Color.MAGENTA);
        g.drawRect((int)hitBox.x - xLvlOffset, (int)hitBox.y, (int)hitBox.width, (int)hitBox.height);
    }
    protected void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.RED);
        g.drawRect((int)attackBox.x - lvlOffsetX, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    protected void initHitBox(int width, int height) {
        hitBox = new Rectangle2D.Float(x, y, (int)width * Game.SCALE, (int)height * Game.SCALE);
    }

    public Rectangle2D.Float getHitBox() {
        return hitBox;
    }
    public int getState() {
        return state;
    }
    public int getAniIndex() {
        return aniIndex;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }
}
