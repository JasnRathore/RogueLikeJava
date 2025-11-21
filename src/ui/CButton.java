package ui;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import texture.InterfaceTextureManager;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class CButton extends Button {

  public CButton(
    GamePanel gp,
    KeyHandler keyH,
    MouseHandler mouseH,
    Text text,
    int tileX,
    int tileY
  ) {

    super(gp,keyH,mouseH, text, tileX,tileY);

  }

  @Override
  public void draw(Graphics2D g2) {

   if (super.hovering) {
      g2.drawImage(super.getTexture(79), tileX, tileY, gp.tileSize,gp.tileSize, null);
      for (int i = 1; i < 6; i++) {
        int offset = (int) (i*gp.tileSize);
        g2.drawImage(super.getTexture(80), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
      }
      int offset = (int) (6*gp.tileSize);
      g2.drawImage(super.getTexture(81), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);

    } else {

    g2.drawImage(super.getTexture(61), tileX, tileY, gp.tileSize,gp.tileSize, null);
    for (int i = 1; i < 6; i++) {
      int offset = (int) (i*gp.tileSize);
      g2.drawImage(super.getTexture(62), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
    }
    int offset = (int) (6*gp.tileSize);
    g2.drawImage(super.getTexture(63), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
      
    }
    
    text.draw(g2);
  }
}
