package upgrade;

import entity.Player;

/**
 * Gives projectiles poison effect that damages enemies over time
 */
public class PoisonGun extends Upgrade {
    private int poisonDamagePerTick;
    private int poisonDuration;
    
    public PoisonGun(int poisonDamagePerTick, int poisonDuration, int rarity) {
        super("Poison Gun", "Enemies poisoned take " + poisonDamagePerTick + " damage/sec", rarity);
        this.poisonDamagePerTick = poisonDamagePerTick;
        this.poisonDuration = poisonDuration;
    }
    
    @Override
    public void apply(Player player) {
        player.gun.enablePoisonEffect(poisonDamagePerTick, poisonDuration);
        System.out.println("Applied: Poison Gun (Poison Damage: " + poisonDamagePerTick + " per sec)");
    }
    
    public int getPoisonDamagePerTick() {
        return poisonDamagePerTick;
    }
    
    public int getPoisonDuration() {
        return poisonDuration;
    }
}
