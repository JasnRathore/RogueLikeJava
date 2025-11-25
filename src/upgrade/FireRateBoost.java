package upgrade;

import entity.Player;

/**
 * Increases fire rate (reduces cooldown between shots)
 */
public class FireRateBoost extends Upgrade {
    private int fireRateDecrease; // How much to reduce fireRate cooldown
    
    public FireRateBoost(int fireRateDecrease, int rarity) {
        super("Fire Rate Boost", "Increase fire rate by " + fireRateDecrease + "%", rarity);
        this.fireRateDecrease = fireRateDecrease;
    }
    
    @Override
    public void apply(Player player) {
        // Reduce the fire rate value (lower = faster)
        player.gun.fireRate = (int) Math.max(1, player.gun.fireRate * (100 - fireRateDecrease) / 100.0);
        System.out.println("Applied: Fire Rate Boost (New fireRate: " + player.gun.fireRate + ")");
    }
    
    public int getFireRateDecrease() {
        return fireRateDecrease;
    }
}
