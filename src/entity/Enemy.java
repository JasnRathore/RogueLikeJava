package entity;

import main.GamePanel;
import main.Pathfinder;
import main.Node;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class Enemy extends Entity {
    
    public int maxHealth;
    public int health;
    public int damage;
    public boolean alive;
    
    private int hitFlashCounter = 0;
    private int hitFlashDuration = 5;
    
    private Player target;
    int lastPlayerCol;
    int lastPlayerRow;

    int enemyTileSize = 16 * 2;
    
    // Pathfinding
    private Pathfinder pathfinder;
    private int pathfindCooldown = 0;
    private int pathfindInterval = 60;
    private int currentPathIndex = 0;

    // ----------------------
    // Knockback Variables
    // ----------------------
    private int knockbackCounter = 0;
    private int knockbackDuration = 10;
    private double knockbackX = 0;
    private double knockbackY = 0;
    private double knockbackStrength = 4;


    public Enemy(GamePanel gp, Player target) {
        super(gp);
        this.target = target;
        this.alive = true;
        
        solidArea = new Rectangle(4, 4, enemyTileSize - 8, enemyTileSize - 8);

        int playerCol = (target.x + 24) / gp.tileSize;
        int playerRow = (target.y + 24) / gp.tileSize;

        this.pathfinder = new Pathfinder(gp);
        
        setDefaultValues();
        getEnemyImage();
    }
    
    public void setDefaultValues() {
        maxHealth = 30;
        health = maxHealth;
        speed = 2;
        damage = 5;
        direction = "right";
    }
    
    public void spawn(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = maxHealth;
        this.alive = true;
        this.pathfindCooldown = 0;
        this.currentPathIndex = 0;
    }
    
    public void getEnemyImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/Players/Tiles/tile_0000.png");
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
    
    public void update() {
        if (!alive) return;
        
        // Hit flash
        if (hitFlashCounter > 0) hitFlashCounter--;

        // -----------------------------------
        // KNOCKBACK MOVEMENT (before AI)
        // -----------------------------------
        if (knockbackCounter > 0) {
            knockbackCounter--;

            int kbX = (int) knockbackX;
            int kbY = (int) knockbackY;

            // Move X
            x += kbX;
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (collisionOn) x -= kbX;

            // Move Y
            y += kbY;
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (collisionOn) y -= kbY;

            return; // skip AI while in knockback
        }
        
        // Clamp enemy inside screen
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > gp.screenWidth - enemyTileSize) x = gp.screenWidth - enemyTileSize;
        if (y > gp.screenHeight - enemyTileSize) y = gp.screenHeight - enemyTileSize;
        
        // Pathfinding cooldown
        pathfindCooldown--;

        int enemyCol = (x + enemyTileSize / 2) / gp.tileSize;
        int enemyRow = (y + enemyTileSize / 2) / gp.tileSize;
        int playerCol = (target.x + 24) / gp.tileSize;
        int playerRow = (target.y + 24) / gp.tileSize;

        if (pathfindCooldown <= 0) {

            if (Math.abs(playerCol - lastPlayerCol) > 2 || 
                Math.abs(playerRow - lastPlayerRow) > 2) {
                pathfinder.setNodes(enemyCol, enemyRow, playerCol, playerRow);
                pathfinder.search();
                currentPathIndex = 0;
                lastPlayerCol = playerCol;
                lastPlayerRow = playerRow;
            }
            pathfindCooldown = pathfindInterval;
        }
        
        
        // Follow path
        if (pathfinder.pathList.size() > 0 && currentPathIndex < pathfinder.pathList.size()) {
            Node nextNode = pathfinder.pathList.get(currentPathIndex);
            int nextX = nextNode.col * gp.tileSize;
            int nextY = nextNode.row * gp.tileSize;
            
            int enemyCenterX = x + enemyTileSize / 2;
            int enemyCenterY = y + enemyTileSize / 2;
            
            if (Math.abs(enemyCenterX - nextX) < speed * 2 && 
                Math.abs(enemyCenterY - nextY) < speed * 2) {
                currentPathIndex++;
                if (currentPathIndex >= pathfinder.pathList.size()) {
                    return;
                }
                nextNode = pathfinder.pathList.get(currentPathIndex);
                nextX = nextNode.col * gp.tileSize;
                nextY = nextNode.row * gp.tileSize;
            }
            
            int dx = nextX - enemyCenterX;
            int dy = nextY - enemyCenterY;
            
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    direction = "right";
                    x += speed;
                } else {
                    direction = "left";
                    x -= speed;
                }
            } else {
                if (dy > 0) {
                    direction = "down";
                    y += speed;
                } else {
                    direction = "up";
                    y -= speed;
                }
            }
        } 
        
        else {
            int playerCenterX = target.x + 24;
            int playerCenterY = target.y + 24;
            int enemyCenterX = x + enemyTileSize / 2;
            int enemyCenterY = y + enemyTileSize / 2;
            
            int dx = playerCenterX - enemyCenterX;
            int dy = playerCenterY - enemyCenterY;
            
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = (dx > 0) ? "right" : "left";
            } else {
                direction = (dy > 0) ? "down" : "up";
            }
            
            collisionOn = false;
            gp.cChecker.checkTile(this);
            
            if (!collisionOn) {
                switch (direction) {
                    case "up":    y -= speed; break;
                    case "down":  y += speed; break;
                    case "left":  x -= speed; break;
                    case "right": x += speed; break;
                }
            }
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        hitFlashCounter = hitFlashDuration;

        // -----------------------------
        // Apply Knockback from Player
        // -----------------------------
        int enemyCenterX = x + enemyTileSize / 2;
        int enemyCenterY = y + enemyTileSize / 2;
        int playerCenterX = target.x + 24;
        int playerCenterY = target.y + 24;

        int dx = enemyCenterX - playerCenterX;
        int dy = enemyCenterY - playerCenterY;

        double length = Math.sqrt(dx*dx + dy*dy);
        if (length != 0) {
            knockbackX = (dx / length) * knockbackStrength;
            knockbackY = (dy / length) * knockbackStrength;
        }

        knockbackCounter = knockbackDuration;

        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }
    
    public void draw(Graphics2D g2) {
        if (!alive) return;
        
        BufferedImage image = direction.equals("right") ? right : left;
        
        if (hitFlashCounter > 0) {
            gp.particleSystem.createBloodSplatter(x, y, 2, 3);
        }
        
        g2.drawImage(image, x, y, enemyTileSize, enemyTileSize, null);
        
        drawHealthBar(g2);
    }
    
    private void drawHealthBar(Graphics2D g2) {
        int barWidth = enemyTileSize;
        int barHeight = 4;
        int barX = x;
        int barY = y - 8;
        
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, barWidth, barHeight);
        
        int healthWidth = (int) ((double) health / maxHealth * barWidth);
        g2.setColor(Color.GREEN);
        g2.fillRect(barX, barY, healthWidth, barHeight);
        
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }
    
    public Rectangle getHitbox() {
        return new Rectangle(
            x + solidArea.x, 
            y + solidArea.y, 
            solidArea.width, 
            solidArea.height
        );
    }
}
