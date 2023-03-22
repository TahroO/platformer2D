package utils;

import com.sun.source.tree.BreakTree;
import main.Game;

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
}
