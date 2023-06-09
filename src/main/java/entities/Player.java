package entities;

import audio.AudioPlayer;
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

    private int powerBarWidth = (int)  (104 * Game.SCALE);
    private int powerBarHeight = (int)  (2 * Game.SCALE);
    private int powerBarXStart= (int)  (44 * Game.SCALE);
    private int powerBarYStart = (int)  (34 * Game.SCALE);
    private int powerWidth = powerBarWidth;
    private int powerMaxValue = 200;
    private int powerValue = powerMaxValue;

    private int flipX = 0;
    private int flipW = 1;
    private boolean attackChecked;
    private Playing playing;
    private int tileY = 0;
    private boolean powerAttackActive;
    private int powerAttackTick;
    private int powerGrowSpeed = 15;
    private int powerGrowTick;

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
        updatePowerBar();
        if (currentHealth <= 0) {
            if (state != DEAD) {
                state = DEAD;
                aniTick = 0;
                aniIndex = 0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
            } else if (aniIndex == getSpriteAmount(DEAD) - 1
                    && aniTick >= ANI_SPEED - 1) {
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer
                        .GAMEOVER);
            } else {
                updateAnimationTick();
            }
            return;
        }
        updateAttackBox();
        // move object by increasing delta values when events occur
        updatePosition();
        if (moving) {
            checkPotionTouched();
            checkSpikesTouched();
            tileY = (int) (hitBox.y / Game.TILES_SIZE);
            if (powerAttackActive) {
                powerAttackTick++;
                if (powerAttackTick >= 35) {
                    powerAttackTick = 0;
                    powerAttackActive = false;
                }
            }
        }
        if (attacking || powerAttackActive) {
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
        if (powerAttackActive) {
            attackChecked = false;
        }
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();
    }

    private void updateAttackBox() {
        if (right && left) {
            if (flipW == 1) {
                attackBox.x = hitBox.x + hitBox.width + (int)(Game.SCALE * 10);
            } else {
                attackBox.x = hitBox.x - hitBox.width - (int)(Game.SCALE * 10);
            }

        } else if (right || (powerAttackActive && flipW == 1)) {
            attackBox.x = hitBox.x + hitBox.width + (int)(Game.SCALE * 10);
        } else if (left || (powerAttackActive && flipW == -1)) {
            attackBox.x = hitBox.x - hitBox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitBox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
    }
    private void updatePowerBar() {
        powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);
        powerGrowTick++;
        if (powerGrowTick >= powerGrowSpeed) {
            powerGrowTick = 0;
            changePower(1);
        }
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
        // Background ui
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        // Health bar ui
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
        // Power bar ui
        g.setColor(Color.YELLOW);
        g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
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
        if (powerAttackActive) {
            state = ATTACK;
            aniIndex = 1;
            aniTick = 0;
            return;
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
            if (!powerAttackActive) {
                if ((!left && !right) || (right && left)) {
                    return;
                }
            }
        }

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
        // in powerAttackMode
        if (powerAttackActive) {
            if (!left && !right) {
                if (flipW == -1) {
                    xSpeed = - walkSpeed;
                } else {
                    xSpeed = walkSpeed;
                }
            }
            xSpeed *= 3;
        }
        // check if we leave the floor
        if (!inAir) {
            if (!isEntityOnFloor(hitBox, lvlData)) {
                inAir = true;
            }
        }
        // powerAttack go straight forward
        if (inAir && !powerAttackActive) {
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
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
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
            // as soon a powerAttack hits something, stop it
            if (powerAttackActive) {
                powerAttackActive = false;
                powerAttackTick = 0;
            }
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
    public void kill() {
        currentHealth = 0;
    }

    public void changePower(int bluePotionValue) {
        powerValue += bluePotionValue;
        if (powerValue >= powerMaxValue) {
            powerValue = powerMaxValue;
        } else if (powerValue <= 0) {
            powerValue = 0;
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


    public int getTileY() {
        return tileY;
    }

    public void powerAttack() {
        if (powerAttackActive) {
            return;
        }
        if (powerValue >= 60) {
            powerAttackActive = true;
            changePower(-60);
        }
    }
}
