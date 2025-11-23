package tile;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.awt.AlphaComposite;
import java.awt.Composite;

import texture.TextureManager;


public class TileManager {

    GamePanel gp;

    Tile colTile = new Tile(null, true); 
    Tile nonColTile = new Tile(null, false); 

    int[][] groundTileNum;
    int[][] moundTileNum;
    int[][] decorTileNum;
    int[][] shadowTileNum;
    public int[][] collisionTileNum;
    
    // Layer caching - pre-render static layers
    private BufferedImage groundLayerCache;
    private BufferedImage moundLayerCache;
    private BufferedImage decorLayerCache;
    private BufferedImage shadowLayerCache;
    private boolean layersCached = false;
    
    // Dirty flag system - only re-render when tiles change
    private boolean groundDirty = true;
    private boolean moundDirty = true;
    private boolean decorDirty = true;
    private boolean shadowDirty = true;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        
        groundTileNum = new int[gp.screenCol][gp.screenRow];
        moundTileNum = new int[gp.screenCol][gp.screenRow];
        decorTileNum = new int[gp.screenCol][gp.screenRow];
        shadowTileNum = new int[gp.screenCol][gp.screenRow];
        collisionTileNum = new int[gp.screenCol][gp.screenRow];

        loadMap("/maps/one_ground.csv", "/maps/one_mound1.csv", "/maps/one_decor.csv", 
                "/maps/one_shadow.csv", "/maps/one_collision.csv");
        
        // Pre-render all layers
        cacheAllLayers();
    }

    public void loadMap(String groundPath, String moundPath, String decorPath, 
                        String shadowPath, String collisionPath) {
        try {
            InputStream is = getClass().getResourceAsStream(groundPath);
            BufferedReader gr = new BufferedReader(new InputStreamReader(is));
            is = getClass().getResourceAsStream(moundPath);
            BufferedReader mr = new BufferedReader(new InputStreamReader(is));
            is = getClass().getResourceAsStream(decorPath);
            BufferedReader dr = new BufferedReader(new InputStreamReader(is));
            is = getClass().getResourceAsStream(shadowPath);
            BufferedReader sr = new BufferedReader(new InputStreamReader(is));
            is = getClass().getResourceAsStream(collisionPath);
            BufferedReader cr = new BufferedReader(new InputStreamReader(is));

            for (int row = 0; row < gp.screenRow; row++) {
                String grLine = gr.readLine();
                String mrLine = mr.readLine();
                String drLine = dr.readLine();
                String srLine = sr.readLine();
                String crLine = cr.readLine();

                if (grLine == null) break;
                if (mrLine == null) break;
                if (drLine == null) break;
                if (srLine == null) break;
                if (crLine == null) break;

                String[] grNums = grLine.split(",");
                String[] mrNums = mrLine.split(",");
                String[] drNums = drLine.split(",");
                String[] srNums = srLine.split(",");
                String[] crNums = crLine.split(",");

                for (int col = 0; col < gp.screenCol; col++) {
                    groundTileNum[col][row] = Integer.parseInt(grNums[col].trim());
                    moundTileNum[col][row] = Integer.parseInt(mrNums[col].trim());
                    decorTileNum[col][row] = Integer.parseInt(drNums[col].trim());
                    shadowTileNum[col][row] = Integer.parseInt(srNums[col].trim());
                    collisionTileNum[col][row] = Integer.parseInt(crNums[col].trim());
                }
            }

            gr.close();
            mr.close();
            dr.close();
            sr.close();
            cr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mark all layers as dirty after loading
        markAllDirty();
    }

    public Tile getTile(int id) {
        if (id == 0) {
            return colTile;
        } else if (id == -1) {
            return nonColTile;
        }        
        return TextureManager.getTile(id);
    }
    
    // Cache all layers as pre-rendered images
    private void cacheAllLayers() {
        System.out.println("Caching tile layers...");
        long startTime = System.currentTimeMillis();
        
        if (groundDirty) {
            groundLayerCache = renderLayer(groundTileNum);
            groundDirty = false;
        }
        if (moundDirty) {
            moundLayerCache = renderLayer(moundTileNum);
            moundDirty = false;
        }
        if (decorDirty) {
            decorLayerCache = renderLayer(decorTileNum);
            decorDirty = false;
        }
        if (shadowDirty) {
            shadowLayerCache = renderLayerWithAlpha(shadowTileNum, 0.5f);
            shadowDirty = false;
        }
        
        layersCached = true;
        long endTime = System.currentTimeMillis();
        System.out.println("Layers cached in " + (endTime - startTime) + "ms");
    }
    
    // Render a single layer to an image
    private BufferedImage renderLayer(int[][] tileNums) {
        BufferedImage layer = new BufferedImage(
            gp.screenWidth, 
            gp.screenHeight, 
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = layer.createGraphics();
        
        int x = 0;
        int y = 0;

        for (int row = 0; row < gp.screenRow; row++) {
            for (int col = 0; col < gp.screenCol; col++) {
                int id = tileNums[col][row];
                
                if (id != -1) {
                    Tile tile = getTile(id);
                    if (tile != null && tile.image != null) {
                        g2.drawImage(tile.image, x, y, null);
                    }
                }
                
                x += gp.tileSize;
            }
            x = 0;
            y += gp.tileSize;
        }
        
        g2.dispose();
        return layer;
    }
    
    // Render layer with transparency
    private BufferedImage renderLayerWithAlpha(int[][] tileNums, float alpha) {
        BufferedImage layer = new BufferedImage(
            gp.screenWidth, 
            gp.screenHeight, 
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = layer.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        int x = 0;
        int y = 0;

        for (int row = 0; row < gp.screenRow; row++) {
            for (int col = 0; col < gp.screenCol; col++) {
                int id = tileNums[col][row];
                
                if (id != -1) {
                    Tile tile = getTile(id);
                    if (tile != null && tile.image != null) {
                        g2.drawImage(tile.image, x, y, null);
                    }
                }
                
                x += gp.tileSize;
            }
            x = 0;
            y += gp.tileSize;
        }
        
        g2.dispose();
        return layer;
    }
    
    // Mark layers as needing re-render
    public void markAllDirty() {
        groundDirty = true;
        moundDirty = true;
        decorDirty = true;
        shadowDirty = true;
        layersCached = false;
    }
    
    public void markGroundDirty() {
        groundDirty = true;
    }
    
    public void markMoundDirty() {
        moundDirty = true;
    }
    
    public void markDecorDirty() {
        decorDirty = true;
    }
    
    public void markShadowDirty() {
        shadowDirty = true;
    }

    public void draw(Graphics2D g2) {
        // Re-cache any dirty layers
        if (!layersCached || groundDirty || moundDirty || decorDirty || shadowDirty) {
            cacheAllLayers();
        }
        
        // Simply draw the pre-rendered layers
        if (groundLayerCache != null) {
            g2.drawImage(groundLayerCache, 0, 0, null);
        }
        if (moundLayerCache != null) {
            g2.drawImage(moundLayerCache, 0, 0, null);
        }
        if (decorLayerCache != null) {
            g2.drawImage(decorLayerCache, 0, 0, null);
        }
        if (shadowLayerCache != null) {
            g2.drawImage(shadowLayerCache, 0, 0, null);
        }
    }
    
    // Optional: Method to update a single tile (if you need dynamic tiles)
    public void updateTile(int col, int row, int layer, int newTileId) {
        if (col < 0 || col >= gp.screenCol || row < 0 || row >= gp.screenRow) {
            return;
        }
        
        switch (layer) {
            case 0: // Ground
                groundTileNum[col][row] = newTileId;
                groundDirty = true;
                break;
            case 1: // Mound
                moundTileNum[col][row] = newTileId;
                moundDirty = true;
                break;
            case 2: // Decor
                decorTileNum[col][row] = newTileId;
                decorDirty = true;
                break;
            case 3: // Shadow
                shadowTileNum[col][row] = newTileId;
                shadowDirty = true;
                break;
            case 4: // Collision
                collisionTileNum[col][row] = newTileId;
                break;
        }
    }
    
    // Optional: Batch update for better performance
    public void batchUpdateStart() {
        // Prevent re-caching until batch is done
        layersCached = true;
    }
    
    public void batchUpdateEnd() {
        // Force re-cache after batch update
        cacheAllLayers();
    }
}
