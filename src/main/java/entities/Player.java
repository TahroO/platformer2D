package entities;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.canMoveHere;

public class Player extends Entity {
    // will contain all animations
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15;
    private int playerAction = IDLE;
    private int width, height;
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down;
    private float playerSpeed = 2.0f;
    // store levelData for hitBoxCollision
    private int[][] lvlData;
    // difference between sprite image corner and character image corner
    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;


    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimation();
        this.width = width;
        this.height = height;
        initHitBox(x, y, 20 * Game.SCALE, 28 * Game.SCALE);
    }

    public void update() {
        // move object by increasing delta values when events occur
        updatePosition();
        // update animation
        updateAnimationTick();
        // check what type of animation should be used
        setAnimation();

    }

    public void render(Graphics g) {
        // cut out image from spriteAtlas also hitBoxRelevant!
        g.drawImage(animations[playerAction][aniIndex], (int)(hitBox.x - xDrawOffset),
                (int)(hitBox.y - yDrawOffset),
                width, height, null);
        // draw hitBox onTop of player
        drawHitBox(g);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            // reset ticks
            aniTick = 0;
            // next image
            aniIndex++;
            if (aniIndex >= getSpriteAmount(playerAction)) {
                // end of array go back to first
                aniIndex = 0;
                attacking = false;
            }
        }
    }

    private void setAnimation() {
        int startAni = playerAction;
        if (moving) {
            playerAction = RUNNING;
        } else {
            playerAction = IDLE;
        }
        if (attacking) {
            playerAction = ATTACK_1;
        }
        if (startAni != playerAction) {
            resetAniTick();
        }
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updatePosition() {
        moving = false;
        // no move when no button is pressed at all
        if (!left && !right && !up && !down) {
            return;
        }
        float xSpeed = 0, ySpeed = 0;
        // only move when one button is pressed directionWise
        if (left && !right) {
            xSpeed = -playerSpeed;
        } else if (right && !left) {
            xSpeed = playerSpeed;
        }
        if (up && !down) {
            ySpeed = -playerSpeed;
        } else if (down && !up) {
            ySpeed = playerSpeed;
        }
//        // check if we can move at next position
//        if (canMoveHere(x + xSpeed, y + ySpeed, width, height, lvlData)) {
//            this.x += xSpeed;
//            this.y += ySpeed;
//            moving = true;
//        }
        // check if we can move at next position
        if (canMoveHere(hitBox.x + xSpeed, hitBox.y + ySpeed,
                hitBox.width, hitBox.height, lvlData)) {
            hitBox.x += xSpeed;
            hitBox.y += ySpeed;
            moving = true;
        }
    }

    private void loadAnimation() {

        BufferedImage image = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
        // 9 animations total
        animations = new BufferedImage[9][6];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                // get all images in spriteAtlas by 64 - 40 images
                animations[j][i] = image.getSubimage(i * 64, j * 40, 64, 40);
            }
        }
    }

    public void loadLevelData(int[][] lvlData) {
        this.lvlData = lvlData;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
}
