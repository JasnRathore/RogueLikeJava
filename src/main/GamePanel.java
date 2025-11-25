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
import texture.TextureManager;

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

  static final int originalTileSize = 16;
  static final float scale1 = 2.5f;
  static final float scale2 = 5f;
  final float scale3 = 7.5f;
	GameState gameState = GameState.TITLE;

  Font font = new Font("Arial", Font.BOLD, 36);

  public static final int tileSize = (int) (originalTileSize * scale1);
  public static final int uiTileSize = (int) (originalTileSize * scale1);
  public final int screenCol = 32;
  public final int screenRow = 18;

  public final int screenWidth = tileSize*screenCol;
  public final int screenHeight = tileSize*screenRow;

  private BufferedImage cachedFogMask;
  private int lastPlayerFogX = -1;
  private int lastPlayerFogY = -1;
  private int fogUpdateThreshold = 5;

  int FPS  = 60;
  static int liveFps = 0;
  FPSOverlay fo = new FPSOverlay(this);

  boolean debug = false; 



  public InterfaceTextureManager itm = new InterfaceTextureManager();
  public TextureManager tm = new TextureManager();

  public TileManager tileManager = new TileManager(this);

  public KeyHandler keyH = new KeyHandler();
  public MouseHandler mouseH = new MouseHandler();
  Thread gameThread;
  public CollisionChecker cChecker = new CollisionChecker(this);
	public Pathfinder pathfinder = new Pathfinder(this);
  public Player player = new Player(this,keyH, mouseH);

	TitleMenu title = new TitleMenu(this,keyH, mouseH);
	PauseMenu pause = new PauseMenu(this,keyH, mouseH);
  DeathOverlay deathOverlay = new DeathOverlay(this,keyH, mouseH);

  // Wave system
  public WaveManager waveManager;

  public ParticleSystem particleSystem = new ParticleSystem(this);  
  public LootManager lootManager = new LootManager();

  // Score and currency
  public int score = 0;
  public int coins = 0;

  // Active pickups on the map
  public java.util.ArrayList<entity.Pickup> pickups = new java.util.ArrayList<>();

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.black);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyH);
    this.addMouseMotionListener(mouseH);
    this.addMouseListener(mouseH);
    this.setFocusable(true);

    waveManager = new WaveManager(this, player);
  loadPersistentStats();

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


    if (keyH.debugPressed) {
      if (debug) {
        debug = false;
      } else {
        debug = true;
      }
    }

    // GameOver
	  if (gameState == GameState.GAMEOVER) {
        deathOverlay.update(waveManager.currentWave, 200);
      	return;
    }

    // Play
		if (gameState == GameState.PLAY) {
	  	if (!player.isAlive()) {
        gameState = GameState.GAMEOVER;
        showCursor();
      	return;
    	}
    
    	player.update();
    	waveManager.update();
      particleSystem.update();
    
	    if (!waveManager.isWaveActive() && !waveManager.isCooldownActive()) {
  	    waveManager.startNextWave();
    	}
    
    	player.gun.checkEnemyCollisions(waveManager.enemies);

        // Update pickups and check collection
        for (int i = pickups.size() - 1; i >= 0; i--) {
          entity.Pickup p = pickups.get(i);
          p.update();
          if (!p.active) pickups.remove(i);
        }

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
    pickups.clear();
    score = 0;
    coins = 0;
  }
  
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

    

    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

    //debug
    long drawStart = System.nanoTime();

	
		if (gameState == GameState.PLAY || gameState == GameState.PAUSE || gameState == GameState.GAMEOVER) {
	    tileManager.draw(g2);
  	  waveManager.draw(g2);
    	player.draw(g2);
      particleSystem.draw(g2);
      // draw pickups
      for (entity.Pickup p : pickups) {
        p.draw(g2);
      }
      
      
      //optimzing fog rendering
      if (cachedFogMask == null || 
            Math.abs(player.x - lastPlayerFogX) > fogUpdateThreshold ||
            Math.abs(player.y - lastPlayerFogY) > fogUpdateThreshold) {
            cachedFogMask = generateFogMask(player.x, player.y, 10);
            lastPlayerFogX = player.x;
            lastPlayerFogY = player.y;
        }
        
      g2.drawImage(cachedFogMask, 0, 0, screenWidth, screenHeight, null);
      //-----ddj
  	  if (gameState != GameState.GAMEOVER) {
    	  waveManager.drawWaveUI(g2);
    	  player.drawHealthBar(g2);
  // Draw score and coins
  g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
  g2.setColor(java.awt.Color.WHITE);
  g2.drawString("Score: " + score, 20, 30);
  g2.drawString("Coins: " + coins, 20, 50);
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

		fo.draw(g2);


    long drawEnd = System.nanoTime();
    long passed = drawEnd - drawStart;
    float secs = passed/1000000000f;
    float rounded = Math.round(secs * 10000f) / 10000f;

 
    if (debug) {
        System.out.println("Passed: "+rounded);
        g2.setFont(font);
        g2.setColor(Color.RED);
        g2.drawString(Double.toString(rounded), 50, 100);
    }
    

    g2.dispose();
  }
  

  public void hideCursor() {
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    this.setCursor(blankCursor);
  }

public void showCursor() {
    this.setCursor(Cursor.getDefaultCursor());
}

public BufferedImage generateFogMask(int playerX, int playerY, int viewRadiusTiles) {

    BufferedImage mask = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = mask.createGraphics();
    
    int maxRadius = viewRadiusTiles * tileSize;
    
    float[] fractions = {0.0f, 0.7f, 1.0f};
    Color[] colors = {
        new Color(242, 220, 172, 0),
        new Color(242, 220, 172, 180),
        new Color(242, 220, 172, 255)
    };
    
    RadialGradientPaint gradient = new RadialGradientPaint(
        playerX, playerY,
        maxRadius,
        fractions,
        colors
    );
    
    g2.setPaint(gradient);
    g2.fillRect(0, 0, screenWidth, screenHeight);
    g2.dispose();
    
    return mask;
}

  public void setStateToPlay() {
   gameState = GameState.PLAY;
   hideCursor();
  }
  public void setStateToTitle() {
   gameState = GameState.TITLE;
   showCursor();
  }
  public void setStateToPause() {
   gameState = GameState.PAUSE;
   showCursor();
  }
  
  public void quitGame() {
      savePersistentStats();
      System.exit(0);
  }

    public void onEnemyKilled(entity.Enemy e) {
      // Give score based on enemy strength
      int points = Math.max(10, e.maxHealth * 5);
      addScore(points);

      // Create death particles
      particleSystem.createEnergyParticles(e.x + 8, e.y + 8, new java.awt.Color(255,100,100), 12);

      // Roll for loot
      String[] loot = lootManager.rollLoot();
      if (loot != null) {
        String type = loot[0];
        int amt = Integer.parseInt(loot[1]);
        entity.Pickup.Type pt = entity.Pickup.Type.COIN;
        try {
          pt = entity.Pickup.Type.valueOf(type);
        } catch (Exception ex) {
          // default to coin
        }
        int enemySize = 32; // assume standard enemy tile size
        pickups.add(new entity.Pickup(this, e.x + enemySize/2, e.y + enemySize/2, pt, amt));
      }
    }

    public void addScore(int amt) {
      if (amt <= 0) return;
      score += amt;
    }

    public void addCoins(int amt) {
      if (amt <= 0) return;
      coins += amt;
    }

    private void loadPersistentStats() {
      // Try to load score and coins from files if present
      try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("score.txt"))) {
        String s = br.readLine(); if (s != null) score = Integer.parseInt(s.trim());
      } catch (Exception ex) { }
      try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("coins.txt"))) {
        String s = br.readLine(); if (s != null) coins = Integer.parseInt(s.trim());
      } catch (Exception ex) { }
    }

    private void savePersistentStats() {
      try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("score.txt"))) {
        pw.println(score);
      } catch (Exception ex) { }
      try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("coins.txt"))) {
        pw.println(coins);
      } catch (Exception ex) { }
    }
}
