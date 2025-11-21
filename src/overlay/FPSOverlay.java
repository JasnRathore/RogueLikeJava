package overlay;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import java.awt.Graphics2D;

import ui.Text;
import ui.NonTileText;
//import ui.Component;

public class FPSOverlay {

    GamePanel gp;
    NonTileText PRE;
    NonTileText FPS;

    public FPSOverlay (GamePanel gp) {

      PRE = new NonTileText(gp,"FPS", 32, 1); 
      FPS = new NonTileText(gp,"", 36, 1); 
    }

    public void update(int fps) {
      FPS.setValue(Integer.toString(fps));
    }
    public void draw(Graphics2D g2) {
      PRE.draw(g2);
      FPS.draw(g2);
    }

  
}
