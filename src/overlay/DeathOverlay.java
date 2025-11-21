package overlay;


import ui.Text;
import ui.NonTileText;
import ui.Component;
import ui.Button;
import ui.CButton;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import texture.InterfaceTextureManager;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage; 
import java.awt.image.BufferedImage; 
import java.io.InputStreamReader;
import java.io.IOException;
import javax.imageio.ImageIO;


public class DeathOverlay extends Component {

    Color tint = new Color(0,0,0, 180);
    BufferedImage base;

    Button homeButton;
    CButton retryButton;


    Text title;
    Text title2;

    NonTileText waveSur;
    NonTileText waveSurValue;

    NonTileText score;
    NonTileText scoreValue;

    int tempSize = 32;

    public DeathOverlay (
        GamePanel gp,
        KeyHandler keyH,
        MouseHandler mouseH
    ){
      super(gp,keyH,mouseH);
      
      title = new Text(gp,"Game",14, 5, true);
      title2 = new Text(gp,"Over",14, 6, true);

      waveSur = new NonTileText(gp,"Waves Survived",2, 10);
      waveSurValue = new NonTileText(gp,"8",20, 10);

      score = new NonTileText(gp,"Score",2, 12);
      scoreValue = new NonTileText(gp,"234",20, 12);

      homeButton = new Button(gp,keyH,mouseH, new Text(gp,"MENU",4, 14, true) , 2,14);
      retryButton = new CButton(gp,keyH,mouseH, new Text(gp,"RETRY",21, 14, true) , 20,14);

      homeButton.setOnClick(() -> {
         gp.setStateToTitle(); 
         gp.resetGame();
      });
      retryButton.setOnClick(() -> {
         gp.setStateToPlay(); 
         gp.resetGame();
      });

      base = getImage("/res/DeathOverlayBase.png");
    } 

    public void update() {

      homeButton.update();
      retryButton.update();

    }

    private BufferedImage getTexture(int num) {
        return InterfaceTextureManager.getTexture(num);
    }

    private BufferedImage getImage(String path) {
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

    void drawSkull(Graphics2D g2,int offset) {
      int off = offset*gp.tileSize;
      g2.drawImage(getTexture(9),9*gp.tileSize + off, 5*gp.tileSize,gp.tileSize, gp.tileSize, null);
      g2.drawImage(getTexture(11),10*gp.tileSize + off, 5*gp.tileSize,gp.tileSize, gp.tileSize, null);
      g2.drawImage(getTexture(45),9*gp.tileSize + off, 6*gp.tileSize,gp.tileSize, gp.tileSize, null);
      g2.drawImage(getTexture(47),10*gp.tileSize + off, 6*gp.tileSize,gp.tileSize, gp.tileSize, null);
      g2.drawImage(getTexture(55),9*gp.tileSize + off, 5*gp.tileSize,(int) (gp.tileSize*2), (int) (gp.tileSize*2), null);
    }

    public void draw(Graphics2D g2) {
      g2.setColor(tint);
      g2.fillRect(0,0,gp.screenWidth, gp.screenHeight);
    
      drawSkull(g2, 0); 
      drawSkull(g2, 12); 

      g2.drawImage(base,0,0,gp.screenWidth, gp.screenHeight, null);

      title.draw(g2);
      title2.draw(g2);
    
      g2.drawImage(getTexture(65),tempSize,10*gp.tileSize,tempSize, gp.tileSize, null);
      for (int i = 2; i< 16; i++) {
          int offset = i*tempSize;
          g2.drawImage(getTexture(66),offset,10*gp.tileSize,tempSize, gp.tileSize, null);
      }
      g2.drawImage(getTexture(67),16*tempSize,10*gp.tileSize,tempSize, gp.tileSize, null);


      waveSur.draw(g2);

      waveSurValue.draw(g2);

      g2.drawImage(getTexture(65),tempSize,12*gp.tileSize,tempSize, gp.tileSize, null);
      for (int i = 2; i< 7; i++) {
          int offset = i*tempSize;
          g2.drawImage(getTexture(66),offset,12*gp.tileSize,tempSize, gp.tileSize, null);
      }

      g2.drawImage(getTexture(67),7*tempSize,12*gp.tileSize,tempSize, gp.tileSize, null);

      score.draw(g2);

      scoreValue.draw(g2);

      homeButton.draw(g2);
      retryButton.draw(g2);

    }
}
