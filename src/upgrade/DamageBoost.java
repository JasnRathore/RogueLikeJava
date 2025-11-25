package upgrade;

import entity.Player;

/**
 * Increases player weapon damage by a percentage or flat amount
 */
public class DamageBoost extends Upgrade {
    private int damageIncrease;
    
    public DamageBoost(int damageIncrease, int rarity) {
        super("Damage Boost", "Increase damage by " + damageIncrease, rarity);
        this.damageIncrease = damageIncrease;
    }
    
    @Override
    public void apply(Player player) {
        player.gun.bulletDamage += damageIncrease;
        System.out.println("Applied: Damage Boost +" + damageIncrease + " (Total: " + player.gun.bulletDamage + ")");
    }
    
    public int getDamageIncrease() {
        return damageIncrease;
    }
}
