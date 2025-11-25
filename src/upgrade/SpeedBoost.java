package upgrade;

import entity.Player;

/**
 * Increases player movement speed
 */
public class SpeedBoost extends Upgrade {
    private int speedIncrease;
    
    public SpeedBoost(int speedIncrease, int rarity) {
        super("Speed Boost", "Increase movement speed by " + speedIncrease, rarity);
        this.speedIncrease = speedIncrease;
    }
    
    @Override
    public void apply(Player player) {
        player.speed += speedIncrease;
        System.out.println("Applied: Speed Boost +" + speedIncrease + " (Total: " + player.speed + ")");
    }
    
    public int getSpeedIncrease() {
        return speedIncrease;
    }
}
