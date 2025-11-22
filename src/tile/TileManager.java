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
import java.awt.AlphaComposite;
import java.awt.Composite;

import texture.TextureManager;


public class TileManager {

    GamePanel gp;

    Tile colTile =new Tile(null, true); 
    Tile nonColTile =new Tile(null, false); 

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

        loadMap("/maps/one_ground.csv", "/maps/one_mound1.csv","/maps/one_decor.csv", "/maps/one_shadow.csv", "/maps/one_collision.csv");
    }


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

    public Tile getTile(int id) {
        if (id == 0) {
            return colTile;
        } else if (id == -1) {
            return nonColTile;
        }        
        return TextureManager.getTile(id);
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
                Tile gt = getTile(gid);
                Tile mt = getTile(mid);
                Tile dt = getTile(did);
                Tile st = getTile(sid);

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
                    g2.drawImage(gt.image, x, y,null);
                }
                if (mid != -1) {
                    g2.drawImage(mt.image, x, y, null);
                }
                if (did != -1) {
                    g2.drawImage(dt.image, x, y, null);
                }
                if (sid != -1) {
                    float opacity = 0.5f;
                    Composite originalComposite = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    g2.drawImage(st.image, x, y, null);
                    g2.setComposite(originalComposite);
                }

                x += gp.tileSize;
            }
            x = 0;
            y += gp.tileSize;
        }
    }
}
