package main;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.RadialGradientPaint;
import java.awt.Font;
import java.awt.RenderingHints;

import entity.Player;

import tile.TileManager;

import texture.InterfaceTextureManager;

import menu.TitleMenu;
import menu.PauseMenu;

import overlay.FPSOverlay;
import overlay.DeathOverlay;

public class GamePanel extends JPanel implements Runnable {
	enum GameState {
		PLAY,
		TITLE,
		GAMEOVER,
		PAUSE
	}
  final int originalTileSize = 16;
  final float scale1 = 2.5f;
  final float scale2 = 5f;
  final float scale3 = 7.5f;
	GameState gameState = GameState.TITLE;

  public final int tileSize = (int) (originalTileSize * scale1);
  public final int screenCol = 32;
  public final int screenRow = 18;

  public final int viewCol=  18;
  public final int viewRow = 10;

  public final int screenWidth = tileSize*screenCol;
  public final int screenHeight = tileSize*screenRow;

  public final int viewWidth = tileSize*viewCol;
  public final int viewHeight=  tileSize*viewRow;

  BufferedImage fogBuffer;

	Font pixelFont = new Font("Monospaced", Font.PLAIN, 20);

  int FPS  = 60;
  static int liveFps = 0;
  FPSOverlay fo = new FPSOverlay(this);

  public TileManager tileManager = new TileManager(this);
  public InterfaceTextureManager itm = new InterfaceTextureManager();
  KeyHandler keyH = new KeyHandler();
  MouseHandler mouseH = new MouseHandler();
  Thread gameThread;
  public CollisionChecker cChecker = new CollisionChecker(this);
	public Pathfinder pathfinder = new Pathfinder(this);
  Player player = new Player(this,keyH, mouseH);

	TitleMenu title = new TitleMenu(this,keyH, mouseH);
	PauseMenu pause = new PauseMenu(this,keyH, mouseH);
  DeathOverlay deathOverlay = new DeathOverlay(this,keyH, mouseH);

  // Wave system
  WaveManager waveManager;
  

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.black);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyH);
    this.addMouseMotionListener(mouseH);
    this.addMouseListener(mouseH);
    this.setFocusable(true);
    fogBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

    waveManager = new WaveManager(this, player);

    //hideCursor();
    details();
  }

  public void details() {
    System.out.println("tileSize: " +tileSize);
    System.out.println("Width: " + screenWidth+ " Height: " + screenHeight);
  }

  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {

    double drawInterval = 1000000000/FPS;
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;
    long timer = 0;
    int drawCount = 0;

    while (gameThread != null) {

      currentTime = System.nanoTime();
      delta += (currentTime - lastTime)/drawInterval;
      timer += (currentTime - lastTime);
      lastTime = currentTime;
      if (delta >= 1) {
        update();
        repaint();
        delta--;
        drawCount++;
      }
      if (timer >= 1000000000) {
        liveFps = drawCount;
        drawCount = 0;
        timer = 0;
      }

    }

  }

  public void update() {

    

    fo.update(liveFps);

    // GameOver
	  if (gameState == GameState.GAMEOVER) {
        deathOverlay.update();
      	return;
    }

    // Play
		if (gameState == GameState.PLAY) {
	  	if (!player.isAlive()) {
        gameState = GameState.GAMEOVER;
      	return;
    	}
    
    	player.update();
    	waveManager.update();
    
	    if (!waveManager.isWaveActive() && !waveManager.isCooldownActive()) {
  	    waveManager.startNextWave();
    	}
    
    	player.gun.checkEnemyCollisions(waveManager.enemies);

      if (keyH.escapePressed) {
        setStateToPause();
      }
		}

		//Title state
		if (gameState == GameState.TITLE) {
      title.update();
		}

    if (gameState == GameState.PAUSE) {
      pause.update();
    }

  }
  
  public void resetGame() {
    player.setDefaultValues();
    waveManager = new WaveManager(this, player);
  }
  
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

	
		if (gameState == GameState.PLAY || gameState == GameState.PAUSE || gameState == GameState.GAMEOVER) {
	    tileManager.draw(g2);
  	  waveManager.draw(g2);
    	player.draw(g2);
    
    	BufferedImage fogMask = generateFogMask(player.x,player.y, 10); 
    	g2.drawImage(fogMask,0,0,screenWidth, screenHeight, null);

  	  if (gameState != GameState.GAMEOVER) {
    	  waveManager.drawWaveUI(g2);
    	  player.drawHealthBar(g2);
      }
    }

	  if (gameState == GameState.GAMEOVER) {
      deathOverlay.draw(g2);
  	}

		if (gameState == GameState.PAUSE) {
			pause.draw(g2);
		}

		if (gameState == GameState.TITLE) {
			title.draw(g2);
		}

		// g2.setFont(pixelFont);
		// g2.setColor(Color.WHITE);
		// g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		// g2.drawString("FPS: " + Integer.toString(liveFPS), screenWidth-(3*tileSize), 40);
		fo.draw(g2);

    g2.dispose();
  }
  

  public void hideCursor() {
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    this.setCursor(blankCursor);
  }

  public BufferedImage generateFogMask(int playerX, int playerY, int viewRadiusTiles) {
    BufferedImage mask = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
    int centerX = playerX;
    int centerY = playerY;
    int maxRadius = viewRadiusTiles * tileSize;

    final Color fogColor = new Color(243, 205, 172);

    int red = 242;
    int green = 220;
    int blue = 172;

    for (int y = 0; y < screenHeight; y++) {
        for (int x = 0; x < screenWidth; x++) {
            double dist = Point.distance(centerX, centerY, x, y);
            int alpha;

            if (dist < maxRadius) {
                alpha = (int) (255 * (dist / maxRadius));
            } else {
                alpha = 255;
            }

            int pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
            mask.setRGB(x, y, pixel);
        }
    }
    return mask;
  }

  public void setStateToPlay() {
   gameState = GameState.PLAY;
  }
  public void setStateToTitle() {
   gameState = GameState.TITLE;
  }
  public void setStateToPause() {
   gameState = GameState.PAUSE;
  }
  
  public void quitGame() {
    System.exit(0);
  }
}
