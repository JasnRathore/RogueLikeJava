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
    
    int enemyTileSize = 16 * 2;
    
    // Pathfinding - each enemy has its own pathfinder
    private Pathfinder pathfinder;
    private int pathfindCooldown = 0;
    private int pathfindInterval = 30; // Recalculate path every 0.5 seconds
    private int currentPathIndex = 0;

    public Enemy(GamePanel gp, Player target) {
        super(gp);
        this.target = target;
        this.alive = true;
        
        solidArea = new Rectangle(4, 4, enemyTileSize - 8, enemyTileSize - 8);
        
        // Create unique pathfinder for this enemy
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
        
        // Decrease hit flash counter
        if (hitFlashCounter > 0) {
            hitFlashCounter--;
        }
        
        // Keep enemy within screen bounds
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > gp.screenWidth - enemyTileSize) x = gp.screenWidth - enemyTileSize;
        if (y > gp.screenHeight - enemyTileSize) y = gp.screenHeight - enemyTileSize;
        
        // Pathfinding cooldown
        pathfindCooldown--;
        
        // Calculate current tile positions
        int enemyCol = (x + enemyTileSize / 2) / gp.tileSize;
        int enemyRow = (y + enemyTileSize / 2) / gp.tileSize;
        int playerCol = (target.x + 24) / gp.tileSize;
        int playerRow = (target.y + 24) / gp.tileSize;
        
        // Recalculate path periodically
        if (pathfindCooldown <= 0) {
            pathfinder.setNodes(enemyCol, enemyRow, playerCol, playerRow);
            pathfinder.search();
            currentPathIndex = 0;
            pathfindCooldown = pathfindInterval;
        }
        
        // Follow the path
        if (pathfinder.pathList.size() > 0 && currentPathIndex < pathfinder.pathList.size()) {
            Node nextNode = pathfinder.pathList.get(currentPathIndex);
            int nextX = nextNode.col * gp.tileSize;
            int nextY = nextNode.row * gp.tileSize;
            
            int enemyCenterX = x + enemyTileSize / 2;
            int enemyCenterY = y + enemyTileSize / 2;
            
            // Check if reached current waypoint
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
            
            // Move toward next waypoint
            int dx = nextX - enemyCenterX;
            int dy = nextY - enemyCenterY;
            
            // Determine direction based on larger distance
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
        } else {
            // No path found, try direct movement
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
            
            // Try to move
            collisionOn = false;
            gp.cChecker.checkTile(this);
            
            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        y -= speed;
                        break;
                    case "down":
                        y += speed;
                        break;
                    case "left":
                        x -= speed;
                        break;
                    case "right":
                        x += speed;
                        break;
                }
            }
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        hitFlashCounter = hitFlashDuration;
        
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }
    
    public void draw(Graphics2D g2) {
        if (!alive) return;
        
        BufferedImage image = direction.equals("right") ? right : left;
        
        // Flash white when hit
        if (hitFlashCounter > 0) {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, enemyTileSize, enemyTileSize);
        }
        
        g2.drawImage(image, x, y, enemyTileSize, enemyTileSize, null);
        
        drawHealthBar(g2);
        
        // drawPath(g2);
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
    
    private void drawPath(Graphics2D g2) {
        if (pathfinder.pathList.size() > 0) {
            g2.setColor(Color.RED); // Purple line
            
            // line from enemy to first waypoint
            int enemyCenterX = x + enemyTileSize / 2;
            int enemyCenterY = y + enemyTileSize / 2;
            
            if (currentPathIndex < pathfinder.pathList.size()) {
                Node firstNode = pathfinder.pathList.get(currentPathIndex);
                int firstX = firstNode.col * gp.tileSize + gp.tileSize / 2;
                int firstY = firstNode.row * gp.tileSize + gp.tileSize / 2;
                
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawLine(enemyCenterX, enemyCenterY, firstX, firstY);
                
                for (int i = currentPathIndex; i < pathfinder.pathList.size() - 1; i++) {
                    Node node = pathfinder.pathList.get(i);
                    Node nextNode = pathfinder.pathList.get(i + 1);
                    
                    int x1 = node.col * gp.tileSize + gp.tileSize / 2;
                    int y1 = node.row * gp.tileSize + gp.tileSize / 2;
                    int x2 = nextNode.col * gp.tileSize + gp.tileSize / 2;
                    int y2 = nextNode.row * gp.tileSize + gp.tileSize / 2;
                    
                    g2.drawLine(x1, y1, x2, y2);
                }
                
                g2.setStroke(new java.awt.BasicStroke(1));
                
                // Draw waypoint markers
                for (int i = currentPathIndex; i < pathfinder.pathList.size(); i++) {
                    Node node = pathfinder.pathList.get(i);
                    int pathX = node.col * gp.tileSize + gp.tileSize / 2;
                    int pathY = node.row * gp.tileSize + gp.tileSize / 2;
                    
                    // Highlight current target waypoint
                    if (i == currentPathIndex) {
                        g2.setColor(new Color(255, 255, 0, 200)); // Yellow for current target
                        g2.fillOval(pathX - 6, pathY - 6, 12, 12);

                    } else {

                        g2.setColor(Color.RED);
                        g2.fillOval(pathX - 4, pathY - 4, 8, 8);
                    }
                }
            }
        }
    }
    
    public Rectangle getHitbox() {
        return new Rectangle(x + solidArea.x, y + solidArea.y, 
                           solidArea.width, solidArea.height);
    }
}
