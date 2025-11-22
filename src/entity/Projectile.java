package entity;
import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Projectile extends Entity {
  private double vx;
  private double vy;
  private double px;
  private double py;
  private boolean active;
  private int lifetime;
  private int maxLifetime = 180;
  
  // Trail effect
  private ArrayList<TrailPoint> trail;
  private int maxTrailLength = 8;
  
  // Collision
  public Rectangle hitbox;

  public Projectile(GamePanel gp) {
    super(gp);
    this.active = false;
    this.trail = new ArrayList<>();
    this.hitbox = new Rectangle(0, 0, 8, 8); // 8x8 hitbox
  }

  public void set(int startX, int startY, double angle, int speed) {
    this.px = startX;
    this.py = startY;
    this.x = startX;
    this.y = startY;
    
    this.vx = Math.cos(angle) * speed;
    this.vy = Math.sin(angle) * speed;
    
    this.active = true;
    this.lifetime = 0;
    this.trail.clear();
  }

  public void update() {
    if (!active) return;
    
    // Add current position to trail
    trail.add(new TrailPoint(x, y));
    if (trail.size() > maxTrailLength) {
      trail.remove(0);
    }
    
    px += vx;
    py += vy;
    x = (int) px;
    y = (int) py;
    
    // Update hitbox position
    hitbox.x = x - 4;
    hitbox.y = y - 4;
    
    lifetime++;
    
    // Check bounds
    if (x < 0 || x > gp.screenWidth || 
        y < 0 || y > gp.screenHeight || 
        lifetime > maxLifetime) {
      active = false;
      return;
    }
    
    // Check tile collision
    checkTileCollision();
  }
  
  private void checkTileCollision() {
    // Calculate which tile the bullet is in
    int tileCol = x / gp.tileSize;
    int tileRow = y / gp.tileSize;
    
    // Check if within bounds
    if (tileCol < 0 || tileCol >= gp.screenCol || 
        tileRow < 0 || tileRow >= gp.screenRow) {
      active = false;
      return;
    }
    
    // Check collision with tile
    int tileNum = gp.tileManager.collisionTileNum[tileCol][tileRow];
    if (gp.tileManager.getTile(tileNum) != null && 
        gp.tileManager.getTile(tileNum).collision) {
      active = false; // Bullet hits wall and disappears
    }
  }
  
  public void draw(Graphics2D g2) {
    if (!active) return;
    
    // Draw trail (fading) - Cool purple/blue gradient
    for (int i = 0; i < trail.size(); i++) {
      TrailPoint tp = trail.get(i);
      float alpha = (float) i / trail.size(); // Fade from 0 to 1
      int size = 3 + (int)(5 * alpha); // Size increases toward bullet
      
      // Purple to blue gradient in trail
      int red = (int)(150 + 50 * alpha);
      int green = (int)(50 + 100 * alpha);
      int blue = 255;
      
      g2.setColor(new Color(red, green, blue, (int)(150 * alpha)));
      g2.fillOval(tp.x - size/2, tp.y - size/2, size, size);
    }
    
    // Draw main bullet with cool purple/cyan glow
    g2.setColor(new Color(100, 50, 255, 120)); // Purple outer glow
    g2.fillOval(x - 10, y - 10, 20, 20);
    
    g2.setColor(new Color(150, 100, 255)); // Bright purple/magenta
    g2.fillOval(x - 6, y - 6, 12, 12);
    
    g2.setColor(new Color(200, 200, 255)); // Cool white-blue center
    g2.fillOval(x - 3, y - 3, 6, 6);
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void deactivate() {
    active = false;
  }
  
  public Rectangle getHitbox() {
    return hitbox;
  }
  
  // Inner class for trail points
  private class TrailPoint {
    int x, y;
    TrailPoint(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
