package overlay;

import upgrade.Upgrade;
import upgrade.UpgradePool;
import ui.Text;
import ui.NonTileText;
import ui.Component;
import ui.Button;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import texture.InterfaceTextureManager;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Overlay that appears every 5 waves allowing player to select an upgrade card
 */
public class UpgradeOverlay extends Component {
    private static final int CARD_WIDTH = 140;
    private static final int CARD_HEIGHT = 200;
    private static final int CARD_SPACING = 20;
    private static final int NUM_CARDS = 3;
    
    private ArrayList<Upgrade> availableUpgrades;
    private ArrayList<UpgradeCard> cards;
    private boolean isActive = false;
    private boolean upgradeSelected = false;
    private Runnable onUpgradeSelected;
    
    private Text title;
    private NonTileText selectText;
    
    private Color overlayTint = new Color(0, 0, 0, 120);
    
    public UpgradeOverlay(GamePanel gp, KeyHandler keyH, MouseHandler mouseH) {
        super(gp, keyH, mouseH);
        this.cards = new ArrayList<>();
        this.availableUpgrades = new ArrayList<>();
        
        title = new Text(gp, "Choose an Upgrade", 10, 2, true);
        selectText = new NonTileText(gp, "Click a card to select", 6, 16);
    }
    
    public void showUpgradeSelection() {
        UpgradePool pool = new UpgradePool();
        availableUpgrades = pool.getRandomUpgrades(NUM_CARDS);
        
        cards.clear();
        int startX = (gp.screenWidth - (NUM_CARDS * CARD_WIDTH + (NUM_CARDS - 1) * CARD_SPACING)) / 2;
        int startY = (gp.screenHeight - CARD_HEIGHT) / 2;
        
        for (int i = 0; i < availableUpgrades.size(); i++) {
            Upgrade upgrade = availableUpgrades.get(i);
            int cardX = startX + (i * (CARD_WIDTH + CARD_SPACING));
            int cardY = startY;
            cards.add(new UpgradeCard(upgrade, cardX, cardY, CARD_WIDTH, CARD_HEIGHT, i));
        }
        
        isActive = true;
        upgradeSelected = false;
    }
    
    public void setOnUpgradeSelected(Runnable callback) {
        this.onUpgradeSelected = callback;
    }
    
    public void update() {
        if (!isActive) return;
        
        for (UpgradeCard card : cards) {
            card.update(mouseH);
            
            if (card.isClicked(mouseH)) {
                selectUpgrade(card.upgrade);
                upgradeSelected = true;
            }
        }
    }
    
    private void selectUpgrade(Upgrade upgrade) {
        // Apply upgrade to player
        gp.player.addUpgrade(upgrade);
        upgrade.apply(gp.player);
        
        isActive = false;
        
        if (onUpgradeSelected != null) {
            onUpgradeSelected.run();
        }
    }
    
    public void draw(Graphics2D g2) {
        if (!isActive) return;
        
        // Draw overlay tint
        g2.setColor(overlayTint);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Draw title
        title.draw(g2);
        
        // Draw cards
        for (UpgradeCard card : cards) {
            card.draw(g2);
        }
        
        // Draw select text
        selectText.draw(g2);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean hasUpgradeBeenSelected() {
        return upgradeSelected;
    }
    
    /**
     * Inner class representing a single upgrade card
     */
    private class UpgradeCard {
        private Upgrade upgrade;
        private int x, y, width, height;
        private boolean hovering = false;
        private int index;
        
        public UpgradeCard(Upgrade upgrade, int x, int y, int width, int height, int index) {
            this.upgrade = upgrade;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.index = index;
        }
        
        public void update(MouseHandler mouseH) {
            hovering = (mouseH.mouseX >= x && mouseH.mouseX <= x + width &&
                       mouseH.mouseY >= y && mouseH.mouseY <= y + height);
        }
        
        public boolean isClicked(MouseHandler mouseH) {
            return hovering && mouseH.leftButtonPressed;
        }
        
        public void draw(Graphics2D g2) {
            // Card background
            Color bgColor = getRarityColor(upgrade.getRarity());
            g2.setColor(hovering ? new Color(bgColor.getRed() + 50, bgColor.getGreen() + 50, bgColor.getBlue() + 50, 200) :
                                   new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 180));
            g2.fillRect(x, y, width, height);
            
            // Card border
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRect(x, y, width, height);
            
            if (hovering) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new java.awt.BasicStroke(3));
                g2.drawRect(x - 2, y - 2, width + 4, height + 4);
            }
            
            // Card content
            g2.setColor(Color.WHITE);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
            
            // Draw upgrade name
            String name = upgrade.getName();
            int nameX = x + 10;
            int nameY = y + 30;
            g2.drawString(name, nameX, nameY);
            
            // Draw rarity indicator
            String rarity = getRarityName(upgrade.getRarity());
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
            g2.setColor(getRarityColor(upgrade.getRarity()));
            g2.drawString(rarity, nameX, nameY + 15);
            
            // Draw description (wrapped)
            g2.setColor(Color.WHITE);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 9));
            String desc = upgrade.getDescription();
            int descX = x + 5;
            int descY = y + 70;
            
            // Simple text wrapping
            String[] words = desc.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                if ((line.toString() + word).length() > 18) {
                    g2.drawString(line.toString(), descX, descY);
                    descY += 12;
                    line = new StringBuilder(word + " ");
                } else {
                    line.append(word).append(" ");
                }
            }
            g2.drawString(line.toString(), descX, descY);
        }
        
        private Color getRarityColor(int rarity) {
            return switch(rarity) {
                case 0 -> new Color(100, 100, 100); // Common - Gray
                case 1 -> new Color(0, 128, 0); // Uncommon - Green
                case 2 -> new Color(0, 100, 255); // Rare - Blue
                case 3 -> new Color(255, 215, 0); // Legendary - Gold
                default -> new Color(100, 100, 100);
            };
        }
        
        private String getRarityName(int rarity) {
            return switch(rarity) {
                case 0 -> "Common";
                case 1 -> "Uncommon";
                case 2 -> "Rare";
                case 3 -> "Legendary";
                default -> "Unknown";
            };
        }
    }
}
