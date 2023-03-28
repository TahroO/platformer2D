package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static utils.Constants.Directions.LEFT;
import static utils.Constants.Directions.RIGHT;
import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;

public abstract class Enemy extends Entity {
    protected int aniIndex, enemyState, enemyType;
    protected int aniTick, aniSpeed = 25;
    protected boolean firstUpdate = true;
    protected boolean inAir = false;
    protected float fallSpeed;
    protected float gravity = 0.04f * Game.SCALE;
    protected float walkSpeed = 0.35f * Game.SCALE;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;
    protected int maxHealth;
    protected int currentHealth;
    protected boolean active = true;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitBox(x, y, width, height);
        maxHealth = getMaxHealth(enemyType);
        currentHealth = maxHealth;
    }
    protected void firstUpdateCheck(int[][] lvlData) {
            if (!isEntityOnFloor(hitBox, lvlData)) {
                inAir = true;
            }
            firstUpdate = false;
    }
    protected void updateInAir(int[][] lvlData) {
        if (canMoveHere(hitBox.x, hitBox.y + fallSpeed, hitBox.width, hitBox.height,
                lvlData)) {
            hitBox.y += fallSpeed;
            fallSpeed += gravity;
        } else {
            inAir = false;
            hitBox.y = getEntityYPosUnderRoofOrAboveFloor(hitBox, fallSpeed);
            tileY = (int)(hitBox.y / Game.TILES_SIZE);
        }
    }
    protected void move(int[][] lvlData) {
        float xSpeed = 0;
        if (walkDir == LEFT) {
            xSpeed = -walkSpeed;
        } else {
            xSpeed = walkSpeed;
        }
        if (canMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData)) {
            if (isFloor(hitBox, xSpeed, lvlData)) {
                hitBox.x += xSpeed;
                return;
            }
        }
        else {
            System.out.println("cant move here");
        }
        changeWalkDir();
    }
    protected void turnTowardsPlayer (Player player) {
        if (player.hitBox.x > hitBox.x) {
            walkDir = RIGHT;
        } else {
            walkDir = LEFT;
        }
    }
    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        // same tile in y?
        int playerTileY = (int)player.getHitBox().y / Game.TILES_SIZE;
        if (playerTileY == tileY) {
            if (isPlayerInRange(player)) {
                if (isSightClear(lvlData, hitBox, player.hitBox, tileY)) {
                    return true;
                }
            }
        } return false;
    }

    protected boolean isPlayerInRange(Player player) {
        int absValue = (int) Math.abs(player.hitBox.x - hitBox.x);
        return absValue <= attackDistance * 5;
    }
    protected boolean isPlayerCloseForAttack(Player player) {
        int absValue = (int) Math.abs(player.hitBox.x - hitBox.x);
        return absValue <= attackDistance;
    }

    protected void newState(int enemyState) {
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }
    public void hurt(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            newState(DEAD);
        } else {
            newState(HIT);
        }
    }
    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;

                switch (enemyState) {
                    case ATTACK, HIT -> enemyState = IDLE;
                    case DEAD -> active = false;
                }
            }
        }
    }

    protected void changeWalkDir() {
        if (walkDir == LEFT) {
            walkDir = RIGHT;
        } else {
            walkDir = LEFT;
        }
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }
    public boolean isActive() {
        return active;
    }
}
