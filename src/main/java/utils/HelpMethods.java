package utils;

import com.sun.source.tree.BreakTree;
import main.Game;

import java.awt.geom.Rectangle2D;

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
        // check if we are inside the gameWindow
        if (x < 0 || x >= Game.GAME_WIDTH) {
            return true;
        }
        if (y < 0 || y >= Game.GAME_HEIGHT) {
            return true;
        }
        // figure out where we are inside the lvlDataArray
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;
        // !! x and y changed position !!
        int value = lvlData[(int) yIndex][(int) xIndex];
        // is this value a tile? - 11 is transparent
        if (value >= 48 || value < 0 || value != 11) {
            return true;
        }
        return false;
    }
    // check if rectangle collides with border of tile
    public static float getEntityXPosNextToWall(Rectangle2D.Float hitBox, float xSpeed) {
        // coordinates of current tile we are in
        int currentTile = (int)(hitBox.x / Game.TILES_SIZE);
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
        int currentTile = (int)(hitBox.y / Game.TILES_SIZE);
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
        if (!isSolid(hitBox.x, hitBox.y + hitBox.height + 1, lvlData )) {
            if (!isSolid(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData )) {
                // not on floor with hitBox corners
                return false;
            }
        }
        return true;
    }
}
