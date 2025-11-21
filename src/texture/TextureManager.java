package texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


public class TextureManager {

    private static final String PRE  = "/res/Tiles/Tiles/tile_";
    private static final String POST = ".png";

    public BufferedImage getTileTexture(int num) {
        String path = PRE + String.format("%04d", num) + POST;

        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.err.println("Resource not found: " + path);
                return null;
            }
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

