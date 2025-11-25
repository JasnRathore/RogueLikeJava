package entity;

import main.GamePanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Boss enemy that has high health and can split into smaller enemies
 * Spawns fewer but much stronger enemies
 */
public class BossEnemy extends Enemy {
    private int spawnThreshold; // Health at which to spawn minions
    private boolean hasSpawned = false;
    
    public BossEnemy(GamePanel gp, Player target) {
        super(gp, target);
    }
    
    @Override
    public void setDefaultValues() {
        maxHealth = 150; // Much higher health
        health = maxHealth;
        speed = 2;
        damage = 10; // Significant damage
        direction = "right";
        spawnThreshold = maxHealth / 2; // Spawn at 50% health
    }
    
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        
        // Spawn minions when health drops below threshold
        if (!hasSpawned && health <= spawnThreshold && health > 0) {
            spawnMinions();
            hasSpawned = true;
        }
    }
    
    private void spawnMinions() {
        // Spawn 2-3 faster enemies when boss is damaged enough
        int minionCount = 2 + (int)(Math.random() * 2);
        for (int i = 0; i < minionCount; i++) {
            FastEnemy minion = new FastEnemy(gp, target);
            minion.spawn(x + (i * 40), y + (i * 40));
            gp.waveManager.enemies.add(minion);
        }
        System.out.println("Boss spawned " + minionCount + " minions!");
    }
    
    @Override
    public void getEnemyImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/Enemies/Tiles/tile_0030.png");
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
    
    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        
        // Draw a special indicator (larger health bar or different color)
        // to make boss visually distinct
    }
}
