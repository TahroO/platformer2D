package entities;

import gameStates.Playing;
import levels.Level;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.EnemyConstants.*;

public class EnemyManager {
    private Playing playing;
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbies = new ArrayList<>();
    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
    }

    public void loadEnemies(Level level) {
        crabbies = level.getCrabbies();
    }

    public void update(int[][] lvlData, Player player) {
        boolean isAnyActive = false;
        for (Crabby crabby : crabbies) {
            if (crabby.isActive()) {
                crabby.update(lvlData, player);
                isAnyActive = true;
            }
        }
        if (!isAnyActive) {
            playing.setLevelCompleted(true);
        }
    }
    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
    }

    private void drawCrabs(Graphics g, int xLevelOffset) {
        for (Crabby crabby : crabbies) {
            if (crabby.isActive()) {
                g.drawImage(crabbyArr[crabby.getState()][crabby.getAniIndex()],
                        (int) crabby.getHitBox().x - xLevelOffset - CRABBY_DRAWOFFSET_X + crabby.flipX(),
                        (int) crabby.getHitBox().y - CRABBY_DRAWOFFSET_Y,
                        CRABBY_WIDTH * crabby.flipW(), CRABBY_HEIGHT, null);
/*                crabby.drawAttackBox(g, xLevelOffset);*/

            }
        }
    }
    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        for (Crabby crabby : crabbies) {
            if (crabby.isActive() && crabby.getCurrentHealth() > 0 && attackBox.intersects(crabby.getHitBox())) {
                crabby.hurt(10);
                return;
            }
        }
    }

    private void loadEnemyImgs() {
        crabbyArr = new BufferedImage[5][9];
        BufferedImage tmp = LoadSave.getSpriteAtlas(LoadSave.CRAB_SPRITE);
        for (int j = 0; j < crabbyArr.length; j++) {
            for (int i = 0; i < crabbyArr[j].length; i++) {
                crabbyArr[j][i] = tmp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT,
                        CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
            }
        }
    }

    public void resetAllEnemies() {
        for (Crabby crabby : crabbies) {
            crabby.resetEnemy();
        }
    }
}
