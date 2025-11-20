package tile;

import java.awt.image.BufferedImage; 

public class Tile {
	public BufferedImage image;
	public boolean collision = false;

	Tile () {
		this.image = null;
		this.collision= false;
	}
	Tile (BufferedImage image, boolean collision) {
		this.image = image;
		this.collision= collision;
	}
}
