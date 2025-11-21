package overlay;

import ui.Text;
import ui.NonTileText;
import ui.Component;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import java.awt.Graphics2D;

public class WaveOverlay {

    GamePanel gp;
    NonTileText wave;
    NonTileText currentWave;
    NonTileText enemies;
    NonTileText noOfEnemies;

    NonTileText waveComp;
    NonTileText nextWaveIn;
    NonTileText secs;

    boolean isActive = false;
    boolean cooldownActive = false;

    public WaveOverlay (GamePanel gp) {
      wave = new NonTileText(gp,"Wave", 0, 1); 
      currentWave = new NonTileText(gp,"", 9, 1); 
      enemies = new NonTileText(gp,"Enemies", 0, 2); 
      noOfEnemies = new NonTileText(gp,"", 9, 2); 


      waveComp = new NonTileText(gp,"Wave Complete", 0, 1); 
      nextWaveIn = new NonTileText(gp,"New Wave in", 0, 2); 
      secs = new NonTileText(gp,"", 12, 2); 
    }

    public void update(int cw, int en, boolean active, boolean coolActive, int se) {
      currentWave.setValue(Integer.toString(cw));
      noOfEnemies.setValue(Integer.toString(en));
      secs.setValue(Integer.toString(se));
      isActive = active;
      cooldownActive = coolActive;
    }

    public void draw(Graphics2D g2) {
      if (isActive) {
        wave.draw(g2);
        currentWave.draw(g2);
        enemies.draw(g2);
        noOfEnemies.draw(g2);
      } else if (cooldownActive) {
        waveComp.draw(g2);        
        nextWaveIn.draw(g2);
        secs.draw(g2);
      }
    }

  
  
}
