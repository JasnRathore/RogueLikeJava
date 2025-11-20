package entity;
import java.awt.Graphics2D;
import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage; 
import java.io.IOException;          
import javax.imageio.ImageIO;        
import java.io.InputStream;

public class Player extends Entity {
  KeyHandler keyH;
  MouseHandler mouseH;
  Weapon gun;

	int playerTileSize = 16*3;
	int maxY;
	int maxX;

	BufferedImage image = left;

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

  public void draw(Graphics2D g2) {

    if ("right".equals(direction)) {
        image = right;
    }
    if ("left".equals(direction)) {
        image = left;
    }
    
    g2.drawImage(image, x, y, playerTileSize, playerTileSize, null);
    gun.draw(g2);
  }

  
}
