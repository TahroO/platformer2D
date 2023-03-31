package objects;

import entities.Player;
import gameStates.Playing;
import levels.Level;
import main.Game;
import utils.Constants;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;
import static utils.Constants.Projectiles.CANNON_BALL_HEIGHT;
import static utils.Constants.Projectiles.CANNON_BALL_WIDTH;
import static utils.HelpMethods.CanCannonSeePlayer;
import static utils.HelpMethods.isProjectileHittingLevel;

public class ObjectManager {
    private Playing playing;
    private BufferedImage[][] potionImgs, containerImgs;
    private BufferedImage[] cannonImgs;
    private BufferedImage spikeImg, cannonBallImg;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    private ArrayList<Cannon> cannons;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public ObjectManager(Playing playing) {
        this.playing = playing;
        loadImgs();
    }

    public void checkSpikesTouched(Player player) {
        for (Spike spike : spikes) {
            if (spike.getHitBox().intersects(player.getHitBox())) {
                player.kill();
            }
        }
    }

    public void checkObjectTouched(Rectangle2D.Float hitBox) {
        for (Potion potion : potions) {
            if (potion.isActive()) {
                if (hitBox.intersects(potion.getHitBox())) {
                    potion.setActive(false);
                    applyEffectToPlayer(potion);
                }
            }
        }
    }

    public void applyEffectToPlayer(Potion potion) {
        if (potion.getObjType() == RED_POTION) {
            playing.getPlayer().changeHealth(RED_POTION_VALUE);
        } else {
            playing.getPlayer().changePower(BLUE_POTION_VALUE);
        }
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        for (GameContainer container : containers) {
            if (container.isActive() && !container.doAnimation) {
                if (container.getHitBox().intersects(attackBox)) {
                    container.setAnimation(true);
                    int type = 0;
                    if (container.getObjType() == BARREL) {
                        type = 1;
                    }
                    potions.add(new Potion((int) (container.getHitBox().x + container.getHitBox().width / 2),
                            (int) (container.getHitBox().y - container.getHitBox().height),
                            type));
                    return;
                }
            }
        }
    }

    public void loadObject(Level newLevel) {
        potions = new ArrayList<>(newLevel.getPotions());
        containers = new ArrayList<>(newLevel.getContainers());
        spikes = newLevel.getSpikes();
        cannons = newLevel.getCannons();
        projectiles.clear();
    }

    private void loadImgs() {
        BufferedImage potionSprite = LoadSave.getSpriteAtlas(LoadSave.POTION_ATLAS);
        potionImgs = new BufferedImage[2][7];
        for (int j = 0; j < potionImgs.length; j++) {
            for (int i = 0; i < potionImgs[j].length; i++) {
                potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);
            }
        }
        BufferedImage containerSprite = LoadSave.getSpriteAtlas(LoadSave.OBJECT_ATLAS);
        containerImgs = new BufferedImage[2][8];
        for (int j = 0; j < containerImgs.length; j++) {
            for (int i = 0; i < containerImgs[j].length; i++) {
                containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);
            }
        }
        spikeImg = LoadSave.getSpriteAtlas(LoadSave.TRAP_ATLAS);
        cannonImgs = new BufferedImage[7];
        BufferedImage tmp = LoadSave.getSpriteAtlas(LoadSave.CANNON_ATLAS);
        for (int i = 0; i < cannonImgs.length; i++) {
            cannonImgs[i] = tmp.getSubimage(i * 40, 0, 40, 26);
        }
        cannonBallImg = LoadSave.getSpriteAtlas(LoadSave.CANNON_BALL);
    }

    public void update(int[][] lvlData, Player player) {
        for (Potion potion : potions) {
            if (potion.isActive()) {
                potion.update();
            }
        }
        for (GameContainer container : containers) {
            if (container.isActive()) {
                container.update();
            }
        }
        updateCannons(lvlData, player);
        updateProjectiles(lvlData, player);
    }

    private void updateProjectiles(int[][] lvlData, Player player) {
        for (Projectile projectile : projectiles) {
            projectile.updatePos();
            if (projectile.getHitBox().intersects(player.getHitBox())) {
                player.changeHealth(-10);
                projectile.setActive(false);
            } else if (isProjectileHittingLevel(projectile, lvlData)) {
                projectile.setActive(false);
            }
        }
    }

    private void updateCannons(int[][] lvlData, Player player) {
        for (Cannon cannon : cannons) {
            // cannon not animated?
            if (!cannon.doAnimation) {
                // tileY is the same
                if (cannon.getTileY() == player.getTileY()) {
                    // is player in range?
                    if (isPlayerInRange(cannon, player)) {
                        // is player in front of cannon
                        if (isPlayerInFrontOfCannon(cannon, player)) {
                            // line of sight
                            if (CanCannonSeePlayer(lvlData, player.getHitBox(), cannon.getHitBox(), cannon.getTileY())) {
                                // shoot the cannon
                                cannon.setAnimation(true);
                            }
                        }
                    }
                }
            }
            cannon.update();
            if (cannon.getAniIndex() == 4 && cannon.getAniTick() == 0) {
                shootCannon(cannon);
            }
        }
    }

    private void shootCannon(Cannon cannon) {
        int dir = 1;
        if (cannon.getObjType() == CANNON_LEFT) {
            dir = -1;
        }
        projectiles.add(new Projectile((int) cannon.getHitBox().x, (int) cannon.getHitBox().y, dir));
    }

    private boolean isPlayerInFrontOfCannon(Cannon cannon, Player player) {
        if (cannon.getObjType() == CANNON_LEFT) {
            if (cannon.getHitBox().x > player.getHitBox().x) {
                return true;
            }
        } else if (cannon.getHitBox().x < player.getHitBox().x) {
            return true;
        }
        return false;
    }

    private boolean isPlayerInRange(Cannon cannon, Player player) {
        int absValue = (int) Math.abs(player.getHitBox().x - cannon.getHitBox().x);
        return absValue <= Game.TILES_SIZE * 5;
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawPotions(g, xLvlOffset);
        drawContainers(g, xLvlOffset);
        drawTraps(g, xLvlOffset);
        drawCannons(g, xLvlOffset);
        drawProjectiles(g, xLvlOffset);
    }

    private void drawProjectiles(Graphics g, int xLvlOffset) {
        for (Projectile projectile : projectiles) {
            g.drawImage(cannonBallImg, (int) (projectile.getHitBox().x - xLvlOffset),
                    (int) (projectile.getHitBox().y),
                    CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
        }
    }

    private void drawCannons(Graphics g, int xLvlOffset) {
        for (Cannon cannon : cannons) {
            int x = (int) (cannon.getHitBox().x - xLvlOffset);
            int width = CANNON_WIDTH;
            if (cannon.getObjType() == CANNON_RIGHT) {
                x += width;
                width *= -1;
            }
            g.drawImage(cannonImgs[cannon.getAniIndex()], x, (int) (cannon.getHitBox().y),
                    width, CANNON_HEIGHT, null);
        }
    }

    private void drawTraps(Graphics g, int xLvlOffset) {
        for (Spike spike : spikes) {
            g.drawImage(spikeImg, (int) (spike.getHitBox().x - xLvlOffset),
                    (int) (spike.getHitBox().y - spike.getyDrawOffset()),
                    SPIKE_WIDTH, SPIKE_HEIGHT, null);
        }
    }

    private void drawContainers(Graphics g, int xLvlOffset) {
        for (GameContainer container : containers) {
            if (container.isActive()) {
                int type = 0;

                if (container.getObjType() == BARREL) {
                    type = 1;
                }
                g.drawImage(containerImgs[type][container.getAniIndex()],
                        (int) (container.getHitBox().x - container.getxDrawOffset() - xLvlOffset),
                        (int) (container.getHitBox().y - container.getyDrawOffset()),
                        CONTAINER_WIDTH,
                        CONTAINER_HEIGHT,
                        null);
            }
        }
    }


    private void drawPotions(Graphics g, int xLvlOffset) {
        for (Potion potion : potions) {
            if (potion.isActive()) {
                int type = 0;
                if (potion.getObjType() == RED_POTION) {
                    type = 1;
                }
                g.drawImage(potionImgs[type][potion.getAniIndex()],
                        (int) (potion.getHitBox().x - potion.getxDrawOffset() - xLvlOffset),
                        (int) (potion.getHitBox().y - potion.getyDrawOffset()),
                        POTION_WIDTH,
                        POTION_HEIGHT,
                        null);
            }
        }
    }


    public void resetAllObjects() {
        loadObject(playing.getLevelManager().getCurrentLevel());
        for (Potion potion : potions) {
            potion.reset();
        }
        for (GameContainer container : containers) {
            container.reset();
        }
        for (Cannon cannon : cannons) {
            cannon.reset();
        }
    }
}
