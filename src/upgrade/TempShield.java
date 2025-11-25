package upgrade;

import entity.Player;

/**
 * Grants a temporary shield at the start of each wave that absorbs damage
 */
public class TempShield extends Upgrade {
    private int shieldAmount;
    private int shieldDuration; // frames
    private int currentShield = 0;
    private int remainingDuration = 0;
    
    public TempShield(int shieldAmount, int shieldDuration, int rarity) {
        super("Temporal Shield", "Start each wave with " + shieldAmount + " shield HP", rarity);
        this.shieldAmount = shieldAmount;
        this.shieldDuration = shieldDuration;
    }
    
    @Override
    public void apply(Player player) {
        // Shield will be applied at wave start
        System.out.println("Applied: Temporal Shield (will activate at next wave)");
    }
    
    @Override
    public void onWaveStart(Player player) {
        currentShield = shieldAmount;
        remainingDuration = shieldDuration;
        System.out.println("Shield activated: " + currentShield);
    }
    
    @Override
    public void update(Player player) {
        if (currentShield > 0 && remainingDuration > 0) {
            remainingDuration--;
        } else if (remainingDuration <= 0) {
            currentShield = 0;
        }
    }
    
    public int getCurrentShield() {
        return currentShield;
    }
    
    public void takeDamage(int damage) {
        if (currentShield > 0) {
            currentShield -= damage;
            if (currentShield < 0) currentShield = 0;
        }
    }

    // Add shield to the current active shield (e.g., from pickups)
    public void addToCurrentShield(int amount) {
        if (amount <= 0) return;
        this.currentShield += amount;
    }
    
    public int getShieldAmount() {
        return shieldAmount;
    }
    
    public int getShieldDuration() {
        return shieldDuration;
    }
}
