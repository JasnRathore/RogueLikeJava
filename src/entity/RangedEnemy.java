package entity;

import main.GamePanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Ranged enemy that stands back and shoots projectiles
 * (Can be expanded to actually shoot in future)
 */
public class RangedEnemy extends Enemy {
    private int attackRange;
    private int attackCooldown = 0;
    private int attackInterval = 90; // 1.5 seconds between attacks
    
    public RangedEnemy(GamePanel gp, Player target) {
        super(gp, target);
        this.attackRange = 300; // 5+ tiles away
    }
    
    @Override
    public void setDefaultValues() {
        maxHealth = 35;
        health = maxHealth;
        speed = 1; // Slower, prefers distance
        damage = 6; // Moderate damage
        direction = "right";
    }
    
    @Override
    public void update() {
        // First do normal enemy update
        super.update();
        
        if (!alive) return;
        
        // Ranged attack logic
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        int distToPlayer = (int) Math.sqrt(
            Math.pow(x - target.x, 2) + 
            Math.pow(y - target.y, 2)
        );
        
        if (distToPlayer < attackRange && attackCooldown == 0) {
            // Ranged attack would happen here
            attackCooldown = attackInterval;
        }
    }
    
    @Override
    public void getEnemyImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/Enemies/Tiles/tile_0024.png");
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
    
    public int getAttackRange() {
        return attackRange;
    }
}
