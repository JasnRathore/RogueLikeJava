package ui;

import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import texture.InterfaceTextureManager;
import util.Characters;

public class NonTileText {
  enum Style {
    NORMAL,
    BOLD
  }
  GamePanel gp;
  int tileX, tileY;
  private String value;
  Style style = Style.NORMAL; 
  final int sizeX = 32; //40 columns

  public NonTileText (
    GamePanel gp,
    String value,
    int tileX,
    int tileY
  ) {
    this.gp = gp;
    this.value = value;
    this.tileX = tileX*sizeX;
    this.tileY = tileY*gp.uiTileSize;
  }

  public NonTileText (
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
      int offset = (int) (i*sizeX);
      g2.drawImage(getCharacter(c), tileX+offset, tileY, gp.uiTileSize,gp.uiTileSize, null);
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
