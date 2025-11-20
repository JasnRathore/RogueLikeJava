package entity;

import main.GamePanel;
import main.MouseHandler;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage; 
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.geom.Ellipse2D;

import java.io.IOException;          
import java.io.InputStream;
import javax.imageio.ImageIO;        


public class Weapon extends Entity {
  MouseHandler mouseH;
  int offsetY; 
  int offsetX; 
  Reticle reticle;
  boolean shooting = false;

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

    shooting = false;
    if (mouseH.leftButtonPressed) {
        shooting = true;
    } 

    this.x = x;
    this.y = y + offsetY;

    reticle.update(this.x,this.y);
    
  }  
public void draw(Graphics2D g2) {

    // angle toward mouse
    double dx = mouseH.mouseX - x;
    double dy = mouseH.mouseY - y;
    double angle = Math.atan2(dy, dx);


    // --- USE NEW FUNCTION ---
    drawRotatedCentered(
        g2,
        base,
        angle,
        x,
        y,
        gp.tileSize,
        gp.tileSize
    );

     int diameter = 8;
     Ellipse2D.Double circle = new Ellipse2D.Double(x, y, diameter, diameter);

     // g2.setColor(Color.RED);

     // g2.fill(circle);

    // g2.setColor(Color.WHITE);
    // 
    if (shooting) {
       g2.drawLine(x,y,reticle.x,reticle.y);    
    }
     
    reticle.draw(g2);
}

}
