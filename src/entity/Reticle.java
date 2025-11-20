package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage; 
import java.io.IOException;          
import javax.imageio.ImageIO;        
import java.io.InputStream;

import main.GamePanel;
import main.MouseHandler;

public class Reticle extends Entity {
    MouseHandler mouseH;
    int offset;
    int maxDistance;    // The maximum allowed distance from the base

    public Reticle(GamePanel gp, MouseHandler mouseH, int maxDistance) {
		    super(gp);
        this.mouseH = mouseH;
        this.offset = gp.tileSize / 2;
        this.maxDistance = maxDistance;
        this.x = mouseH.mouseX;
        this.y = mouseH.mouseY;
        loadImage();
    }
 void loadImage() {
    try {
        InputStream is = getClass().getResourceAsStream("/res/Weapons/Tiles/tile_0025.png");
        if (is == null) {
            throw new RuntimeException("Image file not found in resources: /res/Players/Tiles/tile_0025.png");
        }
        base = ImageIO.read(is);

    } catch (IOException e) {
        e.printStackTrace();
    }
  }
    
    public void update(int baseX, int baseY) {
        // Get mouse position
        int mouseX = mouseH.mouseX;
        int mouseY = mouseH.mouseY;
        // Calculate vector from base to mouse
        int dx = mouseX - baseX;
        int dy = mouseY - baseY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Clamp to maxDistance
        if (dist > maxDistance) {
            double angle = Math.atan2(dy, dx);
            this.x = baseX + (int)(maxDistance * Math.cos(angle));
            this.y = baseY + (int)(maxDistance * Math.sin(angle));
        } else {
            this.x = mouseX;
            this.y = mouseY;
        }
    }
  public void draw(Graphics2D g2) {
    g2.drawImage(base, x-offset, y-offset, gp.tileSize, gp.tileSize, null);
  }
}
