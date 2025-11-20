package tile;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics2D;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import texture.TextureManager;
import java.awt.AlphaComposite;
import java.awt.Composite;


public class TileManager {

    GamePanel gp;

    public Map<Integer, Tile> tiles = new HashMap<>();
    int[][] groundTileNum;
    int[][] moundTileNum;
    int[][] decorTileNum;
    int[][] shadowTileNum;
    public int[][] collisionTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        groundTileNum = new int[gp.screenCol][gp.screenRow];
        moundTileNum = new int[gp.screenCol][gp.screenRow];
        decorTileNum = new int[gp.screenCol][gp.screenRow];
        shadowTileNum = new int[gp.screenCol][gp.screenRow];
        collisionTileNum = new int[gp.screenCol][gp.screenRow];

        loadTileTextures();
        loadMap("/maps/one_ground.csv", "/maps/one_mound1.csv","/maps/one_decor.csv", "/maps/one_shadow.csv", "/maps/one_collision.csv");
    }

    public void loadTileTextures() {
        int[] ids = {
            1, 2, 19, 20, 37, 38, 39, 55, 56, 57, 58, 64, 73, 74, 75, 90, 91, 92, 93, 108, 109, 110, 111, 127, 128, 129, 154, 155, 172, 174, 175, 176, 177, 179, 186, 187, 191, 195, 199, 200, 202, 213, 215, 232, 233, 222, 194,173, 157,192, 184,185,161,143,66,67,214,78,79,107, 65,125,33,34,35,51,52,53,87,88,208,150,151,89,118,119,120,197,118,119,136,137,121,101,49,210,68,103,137,5, 6, 7, 15, 16, 17, 23, 24, 25, 26, 27, 44, 62, 63, 80, 81, 83, 84, 85, 86, 98, 99, 104, 114, 131, 132, 133, 138, 156, 158, 190, 221, 231,115,139
        };

        TextureManager tm = new TextureManager();

        for (int id : ids) {
            Tile t = new Tile();
            t.image = tm.getTileTexture(id);

            if (t.image == null) {
                System.err.println("⚠ WARNING: Missing tile image for ID " + id);
            }

            tiles.put(id, t);
        }

        tiles.put(0,new Tile(null, true));
        tiles.put(-1,new Tile(null, false));
    }

     // private Tile createTile(String path, boolean collision) throws IOException {
     //     Tile t = new Tile();
     //     t.image = ImageIO.read(getClass().getResourceAsStream(path));
     //     t.collision = collision;
     //     return t;
     // }

    public void loadMap(String groundPath,String moundPath,String decorPath, String shadowPath, String collisionPath) {
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

                String[] grNums= grLine.split(",");
                String[] mrNums= mrLine.split(",");
                String[] drNums= drLine.split(",");
                String[] srNums= srLine.split(",");
                String[] crNums= crLine.split(",");

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
    }

    public void draw(Graphics2D g2) {

        int x = 0;
        int y = 0;

        for (int row = 0; row < gp.screenRow; row++) {
            for (int col = 0; col < gp.screenCol; col++) {

                int gid = groundTileNum[col][row];
                int mid = moundTileNum[col][row];
                int did = decorTileNum[col][row];
                int sid = shadowTileNum[col][row];
                Tile gt = tiles.get(gid);
                Tile mt = tiles.get(mid);
                Tile dt = tiles.get(did);
                Tile st = tiles.get(sid);

                if (gt == null && gid != -1) {
                    System.err.println("⚠ Missing tile for GID: " + gid);
                    continue;
                }
                if (dt == null && did != -1) {
                    System.err.println("⚠ Missing tile for DID: " + did);
                    continue;
                }
                if (mt == null && mid != -1) {
                    System.err.println("⚠ Missing tile for MID: " + mid);
                    continue;
                }
                if (st == null && sid != -1) {
                    System.err.println("⚠ Missing tile for SID: " + sid);
                    continue;
                }

                if (gid != -1) {
                    g2.drawImage(gt.image, x, y, gp.tileSize, gp.tileSize, null);
                }
                if (mid != -1) {
                    g2.drawImage(mt.image, x, y, gp.tileSize, gp.tileSize, null);
                }
                if (did != -1) {
                    g2.drawImage(dt.image, x, y, gp.tileSize, gp.tileSize, null);
                }
                if (sid != -1) {
                    float opacity = 0.5f;
                    Composite originalComposite = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    g2.drawImage(st.image, x, y, gp.tileSize, gp.tileSize, null);
                    g2.setComposite(originalComposite);
                }

                x += gp.tileSize;
            }
            x = 0;
            y += gp.tileSize;
        }
    }
}
