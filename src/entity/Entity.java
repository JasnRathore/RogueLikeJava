package entity;
import java.awt.image.BufferedImage; 
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import main.GamePanel;

public class Entity {
	public int x,y;
	public int speed;

  GamePanel gp;

	public BufferedImage left, right, base;

	public String direction;

	public Rectangle solidArea;
	public boolean collisionOn = false;

	public Entity(GamePanel gp) {
		this.gp = gp;
	}

	public BufferedImage horizontalFlip(BufferedImage img) {
		AffineTransform	tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img.getWidth(null), 0);
		AffineTransformOp	op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage flipped  = op.filter(img, null);
    return flipped;
	}

	public void drawRotatedCentered(
        Graphics2D g2, 
        BufferedImage image, 
        double angle, 
        int drawX, 
        int drawY,
        int width,
        int height
 ) {
    AffineTransform old = g2.getTransform();
    AffineTransform at = new AffineTransform();

    // Move to sprite center
    at.translate(drawX, drawY);

    // Rotate around its center
    at.rotate(angle);

    // Move back by half-size (so the sprite centers correctly)
    at.translate(-width / 2, -height / 2);

    g2.setTransform(at);
    g2.drawImage(image, 0, 0, width, height, null);

    g2.setTransform(old);
}
}
