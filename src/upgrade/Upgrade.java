package upgrade;

import java.awt.image.BufferedImage;
import entity.Player;

/**
 * Abstract base class for all upgrades.
 * Upgrades can modify player stats or behavior when applied.
 */
public abstract class Upgrade {
    protected String name;
    protected String description;
    protected BufferedImage icon;
    protected int rarity; // 0 = common, 1 = uncommon, 2 = rare, 3 = legendary
    
    public Upgrade(String name, String description, int rarity) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
    }
    
    /**
     * Apply the upgrade effect to the player
     */
    public abstract void apply(Player player);
    
    /**
     * Called when a new wave starts (for temporary effects)
     */
    public void onWaveStart(Player player) {
        // Override in subclasses if needed
    }
    
    /**
     * Called at the end of each frame (for continuous effects)
     */
    public void update(Player player) {
        // Override in subclasses if needed
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BufferedImage getIcon() {
        return icon;
    }
    
    public int getRarity() {
        return rarity;
    }
}
