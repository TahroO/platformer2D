package levels;

import entities.Crabby;
import main.Game;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import utils.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.HelpMethods.*;

public class Level {
    private BufferedImage img;
    public int[][] lvlData;
    private ArrayList<Crabby> crabbies;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    private int lvlTilesWide;
    private int maxTilesOffset;
    private int maxLvlOffsetX;
    private Point playerSpawn;

    public Level(BufferedImage img) {
        this.img = img;
        createLevelData();
        createEnemies();
        createPotions();
        createContainers();
        createSpikes();
        calcLvlOffset();
        calcPlayerSpawn();
    }

    private void createSpikes() {
        spikes = HelpMethods.getSpikes(img);
    }

    private void createContainers() {
        containers = HelpMethods.getContainers(img);
    }

    private void createPotions() {
        potions = HelpMethods.getPotions(img);
    }

    private void calcPlayerSpawn() {
        playerSpawn = HelpMethods.getPlayerSpawn(img);
    }

    private void calcLvlOffset() {
        lvlTilesWide = img.getWidth();
        maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
    }

    private void createEnemies() {
        crabbies = getCrabs(img);
    }

    private void createLevelData() {
        lvlData = getLevelData(img);
    }

    public int getSpriteIndex(int x, int y) {
        // return pixelColorData at position inside array !! x and y switched position !!
        return lvlData[y][x];
    }
    public int[][] getLvlData() {
        return lvlData;
    }
    public int getMaxLvlOffsetX() {
        return maxLvlOffsetX;
    }
    public ArrayList<Crabby> getCrabbies() {
        return crabbies;
    }
    public Point getPlayerSpawn() {
        return playerSpawn;
    }
    public ArrayList<Potion> getPotions() {
        return potions;
    }
    public ArrayList<GameContainer> getContainers() {
        return containers;
    }
    public ArrayList<Spike> getSpikes() {
        return spikes;
    }
}
