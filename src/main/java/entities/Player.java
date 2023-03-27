package entities;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

public class Player extends Entity {
    // will contain all animations
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15;
    private int playerAction = IDLE;
    private int width, height;
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down, jump;
    private float playerSpeed = 1.0f * Game.SCALE;
    // store levelData for hitBoxCollision
    private int[][] lvlData;
    // difference between sprite image corner and character image corner
    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;
    // jumping and gravity field values
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;


    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimation();
        this.width = width;
        this.height = height;
        // reducing height by 1px for hitBox
        initHitBox(x, y, (int)(20 * Game.SCALE), (int)(27 * Game.SCALE));
    }

    public void update() {
        // move object by increasing delta values when events occur
        updatePosition();
        // update animation
        updateAnimationTick();
        // check what type of animation should be used
        setAnimation();

    }

    public void render(Graphics g, int lvlOffset) {
        // cut out image from spriteAtlas also hitBoxRelevant!
        g.drawImage(animations[playerAction][aniIndex], (int) (hitBox.x - xDrawOffset) - lvlOffset,
                (int) (hitBox.y - yDrawOffset),
                width, height, null);
        // draw hitBox onTop of player
//        drawHitBox(g);
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
        if (inAir) {
            if (airSpeed < 0) {
                // going up
                playerAction = JUMP;
            } else {
                // falling
                playerAction = FALLING;
            }
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
        // check if we are jumping setting inAir true
        if (jump) {
            jump();
        }
        // no move when no button is pressed at all
        if (!inAir) {
            if ((!left && !right) || (right && left)) {
                return;
            }
        }

/*        if (!left && !right && !inAir) {
            return;
        }*/
        float xSpeed = 0;
        // only move when one button is pressed directionWise
        if (left) {
            xSpeed -= playerSpeed;
        }
        if (right) {
            xSpeed += playerSpeed;
        }
        // check if we leave the floor
        if (!inAir) {
            if (!isEntityOnFloor(hitBox, lvlData)) {
                inAir = true;
            }
        }
        if (inAir) {
            if (canMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
                hitBox.y += airSpeed;
                airSpeed += gravity;
                // check left and right possible direction
                updateXPos(xSpeed);
            } else {
                // hitting roof or floor
                hitBox.y = getEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
                // moving down and hit floor
                if (airSpeed > 0) {
                    resetInAir();
                } else {
                    // hitting roof
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        } else {
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void jump() {
        // if in air do not jump again
        if (inAir) {
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        // check if we can move at next position
        if (canMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData)) {
            hitBox.x += xSpeed;
        } else {
            // place hitBox directly next to wall without gap
            hitBox.x = getEntityXPosNextToWall(hitBox, xSpeed);
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
        // check if entity is in air at start of lvl
        if (!isEntityOnFloor(hitBox, lvlData)) {
            inAir = true;
        }
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
    public void setJump(boolean jump) {
        this.jump = jump;
    }
}
