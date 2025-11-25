package entity;

import main.GamePanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Tanky enemy with high health but slower movement
 */
public class TankEnemy extends Enemy {
    public TankEnemy(GamePanel gp, Player target) {
        super(gp, target);
    }
    
    @Override
    public void setDefaultValues() {
        maxHealth = 60; // 2x basic enemy health
        health = maxHealth;
        speed = 1; // Slower than basic enemy
        damage = 8; // Slightly more damage
        direction = "right";
    }
    
    @Override
    public void getEnemyImage() {
        try {
            // Using a different tile for visual distinction
            InputStream is = getClass().getResourceAsStream("/res/Enemies/Tiles/tile_0018.png");
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
