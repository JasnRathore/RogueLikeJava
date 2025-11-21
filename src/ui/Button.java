package ui;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import texture.InterfaceTextureManager;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class Button extends Component {

  int width;
  boolean pressed = false;
  boolean hovering = false;
  int tileX, tileY;
  Text text;

  private Runnable onClick;
  
  public Button(
    GamePanel gp,
    KeyHandler keyH,
    MouseHandler mouseH,
    Text text,
    int tileX,
    int tileY
  ) {

    super(gp,keyH,mouseH);

    this.text = text;
    this.tileX = tileX*gp.tileSize;
    this.tileY = tileY*gp.tileSize;

  }

  public BufferedImage getTexture(int num) {
    return InterfaceTextureManager.getTexture(num);
  }

  public void setOnClick(Runnable action) {
    this.onClick = action;
  }

  public void update() {
    if (mouseH.mouseX >= tileX && mouseH.mouseY >= tileY && mouseH.mouseX <= (tileX+(7*gp.tileSize)) && mouseH.mouseY <= (tileY+(gp.tileSize))) {
     hovering = true; 
    } else {
     hovering = false; 
    }
    if (hovering && mouseH.leftButtonPressed) {
      if (!pressed && onClick != null) {
        onClick.run();  // call the attached function once on mouse press
      }
      pressed = true;
      mouseH.leftButtonPressed = false;
    } else {
      pressed  = false; 
    }
  }

  public void draw(Graphics2D g2) {

    if (hovering) {
      g2.drawImage(getTexture(79), tileX, tileY, gp.tileSize,gp.tileSize, null);
      for (int i = 1; i < 7; i++) {
        int offset = (int) (i*gp.tileSize);
        g2.drawImage(getTexture(80), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
      }
      int offset = (int) (7*gp.tileSize);
      g2.drawImage(getTexture(81), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);

    } else {

    g2.drawImage(getTexture(61), tileX, tileY, gp.tileSize,gp.tileSize, null);
    for (int i = 1; i < 7; i++) {
      int offset = (int) (i*gp.tileSize);
      g2.drawImage(getTexture(62), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
    }
    int offset = (int) (7*gp.tileSize);
    g2.drawImage(getTexture(63), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
      
    }

    

    //rendring text
    text.draw(g2);

    // if (hovering) {
    //   g2.setColor(Color.GREEN);       
    //   for (int i = 0; i < 8; i++) {
    //     int offset = (int) (i*gp.tileSize);
    //     g2.drawRect(tileX+offset, tileY, gp.tileSize,gp.tileSize);
    //   }
    // }
  }
}
