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
import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
	final int originalTileSize = 16; //16x16 px
	final float scale1 = 2.5f; //for 360p
	final float scale2 = 5f; //for 720p
	final float scale3 = 7.5f; //for 1080p

	public final int tileSize = (int) (originalTileSize * scale1); //40
  public final int screenCol = 32;
  public final int screenRow = 18;

  public final int viewCol=  18;
  public final int viewRow = 10;

  public final int screenWidth = tileSize*screenCol;  //768
  public final int screenHeight = tileSize*screenRow; //576

  public final int viewWidth = tileSize*viewCol;
  public final int viewHeight=  tileSize*viewRow;

	// final Color fogColor = new Color(243, 205, 172);

	BufferedImage fogBuffer;


  int FPS  = 60;
  static int liveFPS = 0;

	public TileManager tileManager = new TileManager(this);
  KeyHandler keyH = new KeyHandler();
  MouseHandler mouseH = new MouseHandler();
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	Player player = new Player(this,keyH, mouseH);


	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseMotionListener(mouseH);
		this.addMouseListener(mouseH);
		this.setFocusable(true);
		fogBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

		hideCursor();
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
				liveFPS = drawCount;
				drawCount = 0;
				timer = 0;
			}

		}

	}

	public void update() {
		player.update();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		tileManager.draw(g2);
		player.draw(g2);
		//drawFadeFog(g2,player.x,player.y, viewWidth,viewHeight);
		BufferedImage fogMask = generateFogMask(player.x,player.y, 10); 
		g2.drawImage(fogMask,0,0,screenWidth, screenHeight, null);
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
            // Calculate distance to player position
            double dist = Point.distance(centerX, centerY, x, y);
            int alpha;

            if (dist < maxRadius) {
                // Closer to player, less alpha (more visible)
                alpha = (int) (255 * (dist / maxRadius)); // linear fade
            } else {
                // Outside view area, full alpha (fog)
                alpha = 255;
            }
						

            // Black color with computed alpha
            int pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
            mask.setRGB(x, y, pixel);
        }
    }
    return mask;
}



}
