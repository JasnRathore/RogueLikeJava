package ui;

import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import texture.InterfaceTextureManager;
import util.Characters;

public class Text {
  enum Style {
    NORMAL,
    BOLD
  }
  GamePanel gp;
  int tileX, tileY;
  private String value;
  Style style = Style.NORMAL; 

  public Text(
    GamePanel gp,
    String value,
    int tileX,
    int tileY
  ) {
    this.gp = gp;
    this.value = value;
    this.tileX = tileX*gp.tileSize;
    this.tileY = tileY*gp.tileSize;
  }

  public Text(
    GamePanel gp,
    String value,
    int tileX,
    int tileY,
    boolean bold
  ) {
    this(gp, value, tileX, tileY);
    if (bold) {
      style = Style.BOLD;
    }
  }

  public void setBold() {
      style = Style.BOLD;
  }

  public void setNormal() {
      style = Style.NORMAL;
  }

  public void draw(Graphics2D g2) {
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      int offset = (int) (i*gp.tileSize);
      g2.drawImage(getCharacter(c), tileX+offset, tileY, gp.tileSize,gp.tileSize, null);
    }
  }
  public void setValue(String str) {
    value  = str;
  }

  private BufferedImage getCharacter(char c) {
    switch (style) {
      case Style.NORMAL:
        return InterfaceTextureManager.getTexture(Characters.getNormalCharTextureID(c));
      case Style.BOLD:
        return InterfaceTextureManager.getTexture(Characters.getBoldCharTextureID(c));
      default:
        return InterfaceTextureManager.getTexture(Characters.getNormalCharTextureID(c));
    }
  }
}
