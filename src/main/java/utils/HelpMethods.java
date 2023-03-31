package utils;

import com.sun.source.tree.BreakTree;
import entities.Crabby;
import main.Game;
import objects.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.EnemyConstants.CRABBY;
import static utils.Constants.ObjectConstants.*;

public class HelpMethods {
    // check collision with hitBox corners
    public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        // topLeft
        if (!isSolid(x, y, lvlData)) {
            // bottomRight
            if (!isSolid(x + width, y + height, lvlData)) {
                // topRight
                if (!isSolid(x + width, y, lvlData)) {
                    // bottomLeft
                    if (!isSolid(x, y + height, lvlData)) {
                        // no collision with tiles
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isSolid(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        // check if we are inside the gameWindow
        if (x < 0 || x >= maxWidth) {
            return true;
        }
        if (y < 0 || y >= Game.GAME_HEIGHT) {
            return true;
        }
        // figure out where we are inside the lvlDataArray
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;
        return isTileSolid((int) xIndex, (int) yIndex, lvlData);
    }

    public static boolean isTileSolid(int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile];
        // is this value a tile? - 11 is transparent
        if (value >= 48 || value < 0 || value != 11) {
            return true;
        }
        return false;
    }
    public static boolean isProjectileHittingLevel(Projectile projectile, int[][] lvlData) {
        return isSolid(projectile.getHitBox().x + projectile.getHitBox().width / 2,
                projectile.getHitBox().y + projectile.getHitBox().height / 2,
                lvlData);
    }

    // check if rectangle collides with border of tile
    public static float getEntityXPosNextToWall(Rectangle2D.Float hitBox, float xSpeed) {
        // coordinates of current tile we are in
        int currentTile = (int) (hitBox.x / Game.TILES_SIZE);
        if (xSpeed > 0) {
            // right
            // pixelValue for current tile
            int tileXPos = currentTile * Game.TILES_SIZE;
            // offset between playerHitBox and tileBorder
            int xOffset = (int) (Game.TILES_SIZE - hitBox.width);
            // actual position -1 to not cross tileBorder with hitBoxBorder
            return tileXPos + xOffset - 1;
        } else {
            // left
            return currentTile * Game.TILES_SIZE;
        }
    }

    // places hitBox right above the floor or under roof
    public static float getEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitBox, float airSpeed) {
        // coordinates of current tile we are in
        int currentTile = (int) (hitBox.y / Game.TILES_SIZE);
        if (airSpeed > 0) {
            // falling - touching floor
            // pixelValue for current tile
            int tileYPos = currentTile * Game.TILES_SIZE;
            // offset between playerHitBox and tileBorder
            int yOffset = (int) (Game.TILES_SIZE - hitBox.height);
            // actual position -1 to not cross tileBorder with hitBoxBorder
            return tileYPos + yOffset - 1;
        } else {
            // jumping
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitBox, int[][] lvlData) {
        // check pixel below bottomLeft and bottomRight + 1px
        if (!isSolid(hitBox.x, hitBox.y + hitBox.height + 1, lvlData)) {
            if (!isSolid(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData)) {
                // not on floor with hitBox corners
                return false;
            }
        }
        return true;
    }

    public static boolean isFloor(Rectangle2D.Float hitBox, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0) {
            return isSolid(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData);
        } else {
            return isSolid(hitBox.x + xSpeed, hitBox.y + hitBox.height + 1, lvlData);
        }
    }
    public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitBox,
                                             Rectangle2D.Float secondHitBox, int yTile) {
        int firstXTile = (int) (firstHitBox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitBox.x / Game.TILES_SIZE);
        if (firstXTile > secondXTile) {
            return isAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
        } else {
            return isAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
        }
    }
    public static boolean isAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++) {
            if (isTileSolid(xStart + i, y, lvlData)) {
                return false;
            }
        }
        return true;
    }
    public static boolean isAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        if (isAllTilesClear(xStart, xEnd, y, lvlData)) {
            for (int i = 0; i < xEnd - xStart; i++) {
                if (!isTileSolid(xStart + i, y + 1, lvlData)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isSightClear(int[][] lvlData, Rectangle2D.Float firstHitBox,
                                       Rectangle2D.Float secondHitBox, int yTile) {
        int firstXTile = (int) (firstHitBox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitBox.x / Game.TILES_SIZE);
        if (firstXTile > secondXTile) {
            return isAllTileWalkable(secondXTile, firstXTile, yTile, lvlData);
        } else {
            return isAllTileWalkable(firstXTile, secondXTile, yTile, lvlData);
        }
    }
    // store lvl data in pixels representing the tiles (red, green, blue)
    public static int[][] getLevelData(BufferedImage img) {
        // this holds the lvlInformation in pixelColors
        int[][] lvlData = new int[img.getHeight()][img.getWidth()];

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getRed();
                if (value >= 48) {
                    value = 0;
                }
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }
    public static ArrayList<Crabby> getCrabs(BufferedImage img) {
        ArrayList<Crabby> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getGreen();
                if (value == CRABBY) {
                    list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
                }
            }
        }
        return list;
    }
    public static Point getPlayerSpawn(BufferedImage img) {
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getGreen();
                if (value == 100) {
                    return new Point(i * Game.TILES_SIZE, j * Game.TILES_SIZE);
                }
            }
        }
        return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
    }
    public static ArrayList<Potion> getPotions(BufferedImage img) {
        ArrayList<Potion> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                if (value == RED_POTION || value == BLUE_POTION) {
                    list.add(new Potion(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
                }
            }
        }
        return list;
    }
    public static ArrayList<GameContainer> getContainers(BufferedImage img) {
        ArrayList<GameContainer> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                if (value == BARREL || value == BOX) {
                    list.add(new GameContainer(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
                }
            }
        }
        return list;
    }

    public static ArrayList<Spike> getSpikes(BufferedImage img) {
        ArrayList<Spike> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                if (value == SPIKE) {
                    list.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, SPIKE));
                }
            }
        }
        return list;
    }
    public static ArrayList<Cannon> getCannons(BufferedImage img) {
        ArrayList<Cannon> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                // transferring the pixelColor from img !! i and j switched positions !!
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                if (value == CANNON_LEFT || value == CANNON_RIGHT) {
                    list.add(new Cannon(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
                }
            }
        }
        return list;
    }
}