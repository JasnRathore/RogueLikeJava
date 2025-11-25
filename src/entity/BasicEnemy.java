package entity;

import main.GamePanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Basic melee enemy that charges at the player
 */
public class BasicEnemy extends Enemy {
    public BasicEnemy(GamePanel gp, Player target) {
        super(gp, target);
    }
    
    @Override
    public void setDefaultValues() {
        maxHealth = 25;
        health = maxHealth;
        speed = 2;
        damage = 5;
        direction = "right";
    }
    
    @Override
    public void getEnemyImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/Enemies/Tiles/tile_0012.png");
            if (is == null) {
                throw new RuntimeException("Image file not found");
            }
            right = ImageIO.read(is);
            left = horizontalFlip(right);
            base = right;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
