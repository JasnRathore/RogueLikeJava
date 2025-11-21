package entity;

import main.GamePanel;
import main.MouseHandler;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage; 
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.IOException;          
import java.io.InputStream;
import javax.imageio.ImageIO;        


public class Weapon extends Entity {
  MouseHandler mouseH;
  int offsetY; 
  int offsetX; 
  Reticle reticle;
  boolean shooting = false;
  double angle = 0;  
  // Projectile management
  ArrayList<Projectile> projectiles;
  int fireRate = 10; // frames between shots (6 shots/second at 60fps)
  int fireCooldown = 0;
  int bulletSpeed = 15;
  int bulletDamage = 10;

  public Weapon(GamePanel gp, MouseHandler mouseH, int x, int y, String direction) {
    super(gp);
    this.mouseH = mouseH;

    this.x = x;
    this.y = y;
    this.direction = direction;

    offsetY = (int) gp.tileSize-gp.tileSize/10;
    offsetX = (int) gp.tileSize;
    getWeaponImage();
    reticle = new Reticle(gp, mouseH, 200);
    
    // Initialize projectile list
    projectiles = new ArrayList<>();
  }

  void getWeaponImage() {
    try {
        InputStream is = getClass().getResourceAsStream("/res/Weapons/Tiles/tile_0005.png");
        if (is == null) {
            throw new RuntimeException("Image file not found in resources: /res/Players/Tiles/tile_0005.png");
        }
        base = ImageIO.read(is);

    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public void update(int x, int y, String direction) {
    switch (direction) {
        case "right":
            x += offsetX;
            break;
        case "left":
            break;
    }

    this.x = x;
    this.y = y + offsetY;

    reticle.update(this.x, this.y);

    double dx = mouseH.mouseX - x;
    double dy = mouseH.mouseY - y;
    angle = Math.atan2(dy, dx);
    
    // Update cooldown
    if (fireCooldown > 0) {
      fireCooldown--;
    }
    
    // Check if shooting
    shooting = false;
    if (mouseH.leftButtonPressed && fireCooldown == 0) {
      shooting = true;
      shoot();
      fireCooldown = fireRate;
    }
    
    // Update all active projectiles
    Iterator<Projectile> iter = projectiles.iterator();
    while (iter.hasNext()) {
      Projectile proj = iter.next();
      proj.update();
      if (!proj.isActive()) {
        iter.remove(); // Remove inactive projectiles
      }
    }
  }
  
  public void checkEnemyCollisions(ArrayList<Enemy> enemies) {
    for (Projectile proj : projectiles) {
      if (!proj.isActive()) continue;
      
      for (Enemy enemy : enemies) {
        if (!enemy.alive) continue;
        
        if (proj.getHitbox().intersects(enemy.getHitbox())) {
          enemy.takeDamage(bulletDamage);
          proj.deactivate();
          break; // Bullet hits one enemy and stops
        }
      }
    }
  }
  
  private void shoot() {
    // Calculate angle to mouse
    double dx = mouseH.mouseX - x;
    double dy = mouseH.mouseY - y;
    double angle = Math.atan2(dy, dx);
    
    // Create new projectile
    Projectile proj = new Projectile(gp);
    proj.set(x, y, angle, bulletSpeed);
    projectiles.add(proj);
  }

  public void draw(Graphics2D g2) {
    // angle toward mouse

    // Draw weapon
    drawRotatedCentered(
        g2,
        base,
        angle,
        x,
        y,
        gp.tileSize,
        gp.tileSize
    );

    // Draw muzzle flash or shooting line (optional)
    if (shooting) {
       g2.setColor(new Color(150, 100, 255, 180));
       g2.drawLine(x, y, x + (int)(Math.cos(angle) * 20), 
                         y + (int)(Math.sin(angle) * 20));    
    }
    
    // Draw all projectiles
    for (Projectile proj : projectiles) {
      proj.draw(g2);
    }
    
    reticle.draw(g2);
  }
  
  // Getter for collision detection
  public ArrayList<Projectile> getProjectiles() {
    return projectiles;
  }
}
