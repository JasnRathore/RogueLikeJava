package ui;
import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

public class Component {
  public GamePanel gp;
  public KeyHandler keyH;
  public MouseHandler mouseH;

  public Component(
    GamePanel gp,
    KeyHandler keyH,
    MouseHandler mouseH
  ) {
    this.gp = gp;
    this.keyH = keyH;
    this.mouseH = mouseH;
  }
    
}
