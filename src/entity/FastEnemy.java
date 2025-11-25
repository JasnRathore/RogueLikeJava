package entity;

import main.GamePanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Fast enemy with low health but high speed
 */
public class FastEnemy extends Enemy {
    public FastEnemy(GamePanel gp, Player target) {
        super(gp, target);
    }
    
    @Override
    public void setDefaultValues() {
        maxHealth = 15; // Lower health than basic enemy
        health = maxHealth;
        speed = 4; // 2x faster than basic enemy
        damage = 4; // Lower damage
        direction = "right";
    }
    
    @Override
    public void getEnemyImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/Enemies/Tiles/tile_0006.png");
            if (is == null) {
                // Fallback to basic enemy image if not found
                is = getClass().getResourceAsStream("/res/Enemies/Tiles/tile_0012.png");
            }
            right = ImageIO.read(is);
            left = horizontalFlip(right);
            base = right;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
