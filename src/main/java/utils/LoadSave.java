package utils;

import entities.Crabby;
import main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static utils.Constants.EnemyConstants.CRABBY;

public class LoadSave {
    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String CRAB_SPRITE = "crabby_sprite.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png";
//    public static final String LEVEL_ONE_DATA = "level_one_data.png";
    public static final String LEVEL_ONE_DATA = "level_one_data_long.png";
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String MENU_BACKGROUND_IMAGE = "background_menu.png";
    public static final String PLAYING_BACKGROUND_IMAGE = "playing_bg_img.png";
    public static final String BIG_CLOUDS = "big_clouds.png";
    public static final String SMALL_CLOUDS = "small_clouds.png";
    public static final String STATUS_BAR = "health_power_bar.png";
    public static final String LEVEL_COMPLETE = "completed_sprite.png";
    public static final String POTION_ATLAS = "potions_sprites.png";
    public static final String OBJECT_ATLAS = "objects_sprites.png";
    public static final String TRAP_ATLAS = "trap_atlas.png";
    public static final String CANNON_ATLAS = "cannon_atlas.png";
    public static final String CANNON_BALL = "ball.png";

    public static BufferedImage getSpriteAtlas(String fileName) {
        BufferedImage image = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        try {
            image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }
    public static BufferedImage[] getAllLevels() {
        URL url = LoadSave.class.getResource("/lvls");
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File[] files = file.listFiles();
        File[] filesSorted = new File[files.length];
        for (int i = 0; i < filesSorted.length; i++) {
            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equals((i + 1) + ".png")) {
                    filesSorted[i] = files[j];
                }
            }
        }
        BufferedImage[] imgs = new BufferedImage[filesSorted.length];
        for (int i = 0; i < imgs.length; i++) {
            try {
                imgs[i] = ImageIO.read(filesSorted[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return imgs;
    }



}
