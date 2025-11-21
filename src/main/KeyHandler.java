package main;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {

  public boolean upPressed, downPressed, leftPressed, rightPressed;
  public boolean spacePressed, restartPressed, escapePressed;

  @Override
  public void keyTyped(KeyEvent e) {
    
  }
  
  @Override
  public void keyPressed(KeyEvent e) {

    int code = e.getKeyCode();

    if (code == KeyEvent.VK_W) {
      upPressed = true;	
    }
    if (code == KeyEvent.VK_S) {
      downPressed = true;	
    }
    if (code == KeyEvent.VK_A) {
      leftPressed = true;	
    }
    if (code == KeyEvent.VK_D) {
      rightPressed = true;	
    }
    if (code == KeyEvent.VK_SPACE) {
      spacePressed = true;
    }
    if (code == KeyEvent.VK_ESCAPE) {
      escapePressed = true;
    }
    if (code == KeyEvent.VK_R) {
      restartPressed = true;
    }

  }
  
  @Override
  public void keyReleased(KeyEvent e) {
    
    int code = e.getKeyCode();

    if (code == KeyEvent.VK_W) {
      upPressed = false;	
    }
    if (code == KeyEvent.VK_S) {
      downPressed = false;	
    }
    if (code == KeyEvent.VK_A) {
      leftPressed = false;	
    }
    if (code == KeyEvent.VK_D) {
      rightPressed = false;	
    }
    if (code == KeyEvent.VK_SPACE) {
      spacePressed = false;
    }
    if (code == KeyEvent.VK_R) {
      restartPressed = false;
    }

    if (code == KeyEvent.VK_ESCAPE) {
      escapePressed = false;
    }

  }
}
