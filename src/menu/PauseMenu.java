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
import java.awt.AlphaComposite;
import java.awt.Composite;

import ui.Component;
import ui.Text;
import ui.Button;


public class PauseMenu extends Component{

    BufferedImage tint;
    BufferedImage base;

    Button resumeButton;
    Button goToMenuButton;

    Text title;

    public PauseMenu (
        GamePanel gp,
        KeyHandler keyH,
        MouseHandler mouseH
    ) {
      super(gp,keyH,mouseH);

      title = new Text(gp,"PAUSED",13, 6, true);

      base = getImage("/res/PauseMenuBase.png");
      tint = getImage("/res/MenuTint.png");
      resumeButton= new Button(gp,keyH,mouseH, new Text(gp,"Resume",13, 9, true) , 12,9);
      goToMenuButton = new Button(gp,keyH,mouseH, new Text(gp,"menu",14, 11, true),12,11);
      resumeButton.setOnClick(() -> {
        gp.setStateToPlay();
      
      });
      goToMenuButton.setOnClick(() -> {
        gp.setStateToTitle();
        gp.resetGame();
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
      g2.drawImage(tint,0,0,gp.screenWidth, gp.screenHeight, null);
      g2.drawImage(base,0,0,gp.screenWidth, gp.screenHeight, null);

      title.draw(g2);

      resumeButton.draw(g2);
      goToMenuButton.draw(g2);

    }
    public void update() {
      resumeButton.update();
      goToMenuButton.update();
    }
}
