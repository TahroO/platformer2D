package levels;

public class Level {
    public int[][] lvlData;
    public Level(int[][] lvlData) {
        this.lvlData = lvlData;
    }
    public int getSpriteIndex(int x, int y) {
        // return pixelColorData at position inside array !! x and y switched position !!
        return lvlData[y][x];
    }
    public int[][] getLvlData() {
        return lvlData;
    }
}
