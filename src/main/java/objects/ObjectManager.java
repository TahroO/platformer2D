package objects;

import entities.Player;
import gameStates.Playing;
import levels.Level;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;

public class ObjectManager {
    private Playing playing;
    private BufferedImage[][] potionImgs, containerImgs;
    private BufferedImage spikeImg;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;

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
    }

    public void update() {
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
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawPotions(g, xLvlOffset);
        drawContainers(g, xLvlOffset);
        drawTraps(g, xLvlOffset);
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
    }
}
