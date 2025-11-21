package texture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


import java.util.HashMap;
import java.util.Map;

public class InterfaceTextureManager {

    private static final String PRE  = "/res/Interface/Tiles/tile_";
    private static final String POST = ".png";

    private static Map<Integer, BufferedImage> textures  = new HashMap<>();

    public InterfaceTextureManager() {
      loadTextures();  
    } 

    static public BufferedImage getTexture(int num) {
        if (textures == null) {
            return null;
        }
        return textures.get(num);
    }

    private void loadTextures() {
        for(int i = 0; i <= 197; i++) {
             loadTexture(i);               
        }
        loadEmptyTexture(); 
    }

    private void loadEmptyTexture() {
        String path = "/res/1000.png";
        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.err.println("Resource not found: " + path);
                return;
            }
            textures.put(1000, ImageIO.read(stream));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    private void loadTexture(int num) {
        String path = PRE + String.format("%04d", num) + POST;

        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.err.println("Resource not found: " + path);
                return;
            }
            textures.put(num, ImageIO.read(stream));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
    
     
}

