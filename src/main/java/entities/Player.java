package entities;

import gameStates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.ANI_SPEED;
import static utils.Constants.GRAVITY;
import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

public class Player extends Entity {
    // will contain all animations
    private BufferedImage[][] animations;
    private int width, height;
    private boolean moving = false, attacking = false;
    private boolean left, right, jump;
    // store levelData for hitBoxCollision
    private int[][] lvlData;
    // difference between sprite image corner and character image corner
    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;
    // jumping and gravity field values
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    // statusBarUi
    private BufferedImage statusBarImg;
    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);
    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);
    private int healthWidth = healthBarWidth;
    private int flipX = 0;
    private int flipW = 1;
    private boolean attackChecked;
    private Playing playing;

    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = Game.SCALE * 1.0f;
        loadAnimation();
        this.width = width;
        this.height = height;
        // reducing height by 1px for hitBox
        initHitBox(20, 27);
        initAttackBox();
    }
    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        hitBox.x = x;
        hitBox.y = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Game.SCALE), (int)(20 * Game.SCALE));
    }

    public void update() {
        updateHealthBar();
        if (currentHealth <= 0) {
            playing.setGameOver(true);
            return;
        }
        updateAttackBox();
        // move object by increasing delta values when events occur
        updatePosition();
        if (moving) {
            checkPotionTouched();
            checkSpikesTouched();
        }
        if (attacking) {
            checkAttack();
        }
        // update animation
        updateAnimationTick();
        // check what type of animation should be used
        setAnimation();

    }

    private void checkSpikesTouched() {
        playing.checkSpikesTouched(this);
    }

    private void checkPotionTouched() {
        playing.checkPotionTouched(hitBox);
    }

    private void checkAttack() {
        if (attackChecked || aniIndex != 1) {
            return;
        }
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
    }

    private void updateAttackBox() {
        if (right) {
            attackBox.x = hitBox.x + hitBox.width + (int)(Game.SCALE * 10);
        } else if (left) {
            attackBox.x = hitBox.x - hitBox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitBox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    public void render(Graphics g, int lvlOffset) {
        // cut out image from spriteAtlas also hitBoxRelevant!
        g.drawImage(animations[state][aniIndex],
                (int) (hitBox.x - xDrawOffset) - lvlOffset + flipX,
                (int) (hitBox.y - yDrawOffset),
                width * flipW, height, null);
        // draw hitBox onTop of player
//        drawHitBox(g);
/*        drawAttackBox(g, lvlOffset);*/
        drawUI(g);
    }

    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            // reset ticks
            aniTick = 0;
            // next image
            aniIndex++;
            if (aniIndex >= getSpriteAmount(state)) {
                // end of array go back to first
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    private void setAnimation() {
        int startAni = state;
        if (moving) {
            state = RUNNING;
        } else {
            state = IDLE;
        }
        if (inAir) {
            if (airSpeed < 0) {
                // going up
                state = JUMP;
            } else {
                // falling
                state = FALLING;
            }
        }
        if (attacking) {
            state = ATTACK;
            if (startAni != ATTACK) {
                aniIndex = 1;
                aniTick = 0;
                return;
            }
        }
        if (startAni != state) {
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
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
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
                airSpeed += GRAVITY;
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
    public void changeHealth(int value) {
        currentHealth += value;
        if (currentHealth <= 0) {
            currentHealth = 0;
            // game over
        } else if (currentHealth >= maxHealth) {
            currentHealth = maxHealth;
        }
    }

    private void loadAnimation() {

        BufferedImage image = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
        // 9 animations total
        animations = new BufferedImage[7][8];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                // get all images in spriteAtlas by 64 - 40 images
                animations[j][i] = image.getSubimage(i * 64, j * 40, 64, 40);
            }
        }
        statusBarImg = LoadSave.getSpriteAtlas(LoadSave.STATUS_BAR);
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

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        state = IDLE;
        currentHealth = maxHealth;
        hitBox.x = x;
        hitBox.y = y;
        if (!isEntityOnFloor(hitBox, lvlData)) {
            inAir = true;
        }
    }

    public void changePower(int bluePotionValue) {
        System.out.println("added power");
    }

    public void kill() {
        currentHealth = 0;
    }
}
