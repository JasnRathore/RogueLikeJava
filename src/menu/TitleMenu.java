package menu;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics2D;
import java.io.InputStream;
import java.io.BufferedReader;
import java.awt.image.BufferedImage; 
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import texture.TextureManager;
import java.awt.AlphaComposite;
import java.awt.Composite;


import ui.Component;
import ui.Text;
import ui.Button;


public class TitleMenu extends Component{

    BufferedImage bg;
    BufferedImage tint;
    BufferedImage base;

    Button playButton;
    Button exitButton;

    Text title;

    public TitleMenu(
        GamePanel gp,
        KeyHandler keyH,
        MouseHandler mouseH
    ) {

      super(gp,keyH,mouseH);
      bg = getImage("/res/MainMenuBackGround.png");
      base = getImage("/res/MainMenuBase.png");
      tint = getImage("/res/MenuTint.png");

      title = new Text(gp,"JUNO",14, 6, true);
      playButton = new Button(gp,keyH,mouseH, new Text(gp,"PLAY",14, 9, true) , 12,9);
      exitButton= new Button(gp,keyH,mouseH, new Text(gp,"EXIT",14, 11, true),12,11);

      playButton.setOnClick(() -> {
        gp.setStateToPlay();
      });
      exitButton.setOnClick(() -> {
        gp.quitGame();
      });
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

    public void draw(Graphics2D g2) {
      g2.drawImage(bg,0,0,gp.screenWidth, gp.screenHeight, null);
      g2.drawImage(tint,0,0,gp.screenWidth, gp.screenHeight, null);
      g2.drawImage(base,0,0,gp.screenWidth, gp.screenHeight, null);

      title.draw(g2);

      playButton.draw(g2);
      exitButton.draw(g2);
    }

    public void update() {

      playButton.update();
      exitButton.update();
    }
}
