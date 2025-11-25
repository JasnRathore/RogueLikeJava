package upgrade;

import entity.Player;

/**
 * Increases player maximum health and restores full health
 */
public class HealthBoost extends Upgrade {
    private int healthIncrease;
    
    public HealthBoost(int healthIncrease, int rarity) {
        super("Health Boost", "Increase max health by " + healthIncrease, rarity);
        this.healthIncrease = healthIncrease;
    }
    
    @Override
    public void apply(Player player) {
        player.maxHealth += healthIncrease;
        player.health = player.maxHealth; // Restore to full health
        System.out.println("Applied: Health Boost +" + healthIncrease + " (Total: " + player.maxHealth + ")");
    }
    
    public int getHealthIncrease() {
        return healthIncrease;
    }
}
