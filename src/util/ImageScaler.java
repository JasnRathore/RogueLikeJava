package util;


import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;

public class ImageScaler {
  public BufferedImage scaleImage(BufferedImage original, int width, int height) {

    BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = scaled.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g.drawImage(original, 0, 0, width, height, null);
    g.dispose();
    return scaled;
  }
}
