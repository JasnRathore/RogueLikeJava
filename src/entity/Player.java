package entity;
import java.awt.Graphics2D;
import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.image.BufferedImage; 
import java.io.IOException;          
import javax.imageio.ImageIO;        
import java.io.InputStream;

public class Player extends Entity {
  KeyHandler keyH;
  MouseHandler mouseH;
  public Weapon gun;

  int playerTileSize = 16*3;
  int maxY;
  int maxX;

  BufferedImage image = left;
  
  // Health system
  public int maxHealth = 100;
  public int health = maxHealth;
  
  // Damage cooldown (invincibility frames)
  private int damageCooldown = 0;
  private int damageCooldownDuration = 30; // 0.5 seconds at 60 FPS
  private int hitFlashCounter = 0;

  public Player(GamePanel gp, KeyHandler keyH, MouseHandler mouseH) {
    super(gp);
    this.keyH = keyH;
    this.mouseH = mouseH;
    
    maxY = gp.screenHeight-playerTileSize;  
    maxX = gp.screenWidth-32;  

    solidArea  = new Rectangle(8,16,playerTileSize-16,playerTileSize-16);
    
    setDefaultValues();
    getPlayerImage();
  }

  public void setDefaultValues() {
    x = 0;
    y = 0;
    speed = 4;
    direction  = "right";
    health = maxHealth;
    gun = new Weapon(gp,mouseH, x, y, direction);
  }

  public void getPlayerImage() {
    try {
        InputStream is = getClass().getResourceAsStream("/res/Players/Tiles/tile_0000.png");
        if (is == null) {
            throw new RuntimeException("Image file not found in resources: /res/Players/Tiles/tile_0000.png");
        }
        right = ImageIO.read(is);
        left = horizontalFlip(right);

    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public void update() {
    
    // Update damage cooldown
    if (damageCooldown > 0) {
        damageCooldown--;
    }
    if (hitFlashCounter > 0) {
        hitFlashCounter--;
    }
    
    if (keyH.upPressed && y > 0) {
      direction = "up";
    }
    else if (keyH.downPressed && y < maxY) {
      direction = "down";
    }
    else if (keyH.leftPressed && x > 0) {
      direction = "left";
    }
    else if (keyH.rightPressed && x < maxX) {
      direction = "right";
    } 
    else {
      direction = "idle";
    }

    collisionOn = false;
    gp.cChecker.checkTile(this);

    if (collisionOn == false) {
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

    gun.update(x,y, direction);
  }

  public void takeDamage(int damage) {
    if (damageCooldown > 0) return; // Still in invincibility frames
    
    health -= damage;
    damageCooldown = damageCooldownDuration;
    hitFlashCounter = 10;
    
    if (health < 0) {
        health = 0;
    }
    
    System.out.println("Player hit! Health: " + health + "/" + maxHealth);
  }
  
  public boolean canTakeDamage() {
    return damageCooldown == 0;
  }
  
  public boolean isAlive() {
    return health > 0;
  }

  public void draw(Graphics2D g2) {

    if ("right".equals(direction)) {
        image = right;
    }
    if ("left".equals(direction)) {
        image = left;
    }
    
    // Flash red when taking damage
    if (hitFlashCounter > 0 && hitFlashCounter % 4 < 2) {
        g2.setColor(new Color(255, 0, 0, 100));
        g2.fillRect(x, y, playerTileSize, playerTileSize);
    }
    
    g2.drawImage(image, x, y, playerTileSize, playerTileSize, null);
    gun.draw(g2);
    
  }
  
  public void drawHealthBar(Graphics2D g2) {
    int barWidth = 200;
    int barHeight = 20;
    int barX = 20;
    int barY = gp.screenHeight - 40;
    
    // Background
    g2.setColor(new Color(50, 50, 50));
    g2.fillRect(barX, barY, barWidth, barHeight);
    
    // Health
    int healthWidth = (int) ((double) health / maxHealth * barWidth);
    if (health > 50) {
        g2.setColor(Color.GREEN);
    } else if (health > 25) {
        g2.setColor(Color.YELLOW);
    } else {
        g2.setColor(Color.RED);
    }
    g2.fillRect(barX, barY, healthWidth, barHeight);
    
    // Border
    g2.setColor(Color.WHITE);
    g2.drawRect(barX, barY, barWidth, barHeight);
    
    // Text
    g2.setFont(new Font("Arial", Font.BOLD, 14));
    String healthText = health + " / " + maxHealth;
    g2.drawString(healthText, barX + 5, barY + 15);
  }
  
  public Rectangle getHitbox() {
    return new Rectangle(x + solidArea.x, y + solidArea.y, 
                         solidArea.width, solidArea.height);
  }
}
