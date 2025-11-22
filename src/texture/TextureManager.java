package texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.HashMap;
import java.util.Map;
import main.GamePanel;
import tile.Tile;
import util.ImageScaler;

public class TextureManager {

    private static final String PRE  = "/res/Tiles/Tiles/tile_";
    private static final String POST = ".png";


    private static Map<Integer, Tile> textureTiles  = new HashMap<>();

    public TextureManager() {
        loadTiles();
    }

    public static BufferedImage getTexture(int num) {
        if (textureTiles == null) {
            return null;
        }

        return textureTiles.get(num).image;
    }

    public static Tile getTile(int num) {
        if (textureTiles == null) {
            return null;
        }
        return textureTiles.get(num);
    }

    private void loadTiles() {
        for(int i = 0; i <= 233; i++) {
             textureTiles.put(i,new Tile(loadTexture(i)));               
        }
    }

    private BufferedImage loadTexture(int num) {
        String path = PRE + String.format("%04d", num) + POST;
        ImageScaler is = new ImageScaler();
        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.err.println("Resource not found: " + path);
                return null;
            }
            
            return is.scaleImage(ImageIO.read(stream), 40, 40);
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

