package upgrade;

import java.util.ArrayList;
import java.util.Random;

/**
 * Pool of all available upgrades that can be offered to the player
 */
public class UpgradePool {
    private ArrayList<Upgrade> commonUpgrades;
    private ArrayList<Upgrade> uncommonUpgrades;
    private ArrayList<Upgrade> rareUpgrades;
    private ArrayList<Upgrade> legendaryUpgrades;
    private Random random;
    
    public UpgradePool() {
        this.random = new Random();
        initializeUpgrades();
    }
    
    private void initializeUpgrades() {
        commonUpgrades = new ArrayList<>();
        uncommonUpgrades = new ArrayList<>();
        rareUpgrades = new ArrayList<>();
        legendaryUpgrades = new ArrayList<>();
        
        // Common upgrades
        commonUpgrades.add(new DamageBoost(5, 0));
        commonUpgrades.add(new HealthBoost(10, 0));
        commonUpgrades.add(new SpeedBoost(1, 0));
        commonUpgrades.add(new FireRateBoost(10, 0));
        
        // Uncommon upgrades
        uncommonUpgrades.add(new DamageBoost(10, 1));
        uncommonUpgrades.add(new HealthBoost(20, 1));
        uncommonUpgrades.add(new SpeedBoost(2, 1));
        uncommonUpgrades.add(new FireRateBoost(15, 1));
        uncommonUpgrades.add(new TempShield(25, 600, 1)); // 10 seconds at 60 FPS
        uncommonUpgrades.add(new PoisonGun(2, 180, 1)); // 3 seconds
        
        // Rare upgrades
        rareUpgrades.add(new DamageBoost(20, 2));
        rareUpgrades.add(new HealthBoost(30, 2));
        rareUpgrades.add(new SpeedBoost(3, 2));
        rareUpgrades.add(new FireRateBoost(25, 2));
        rareUpgrades.add(new TempShield(50, 900, 2)); // 15 seconds
        rareUpgrades.add(new PoisonGun(4, 300, 2)); // 5 seconds
        rareUpgrades.add(new NewWeapon("Rapid Cannon", 8, 5, 15, 2));
        
        // Legendary upgrades
        legendaryUpgrades.add(new DamageBoost(35, 3));
        legendaryUpgrades.add(new HealthBoost(50, 3));
        legendaryUpgrades.add(new SpeedBoost(4, 3));
        legendaryUpgrades.add(new FireRateBoost(35, 3));
        legendaryUpgrades.add(new TempShield(100, 1200, 3)); // 20 seconds
        legendaryUpgrades.add(new PoisonGun(6, 600, 3)); // 10 seconds
        legendaryUpgrades.add(new NewWeapon("Plasma Cannon", 25, 8, 18, 3));
        legendaryUpgrades.add(new NewWeapon("Sting Gun", 12, 3, 20, 3));
    }
    
    /**
     * Get random upgrades from the pool, potentially weighted by rarity
     */
    public ArrayList<Upgrade> getRandomUpgrades(int count) {
        ArrayList<Upgrade> selected = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            selected.add(getRandomUpgrade());
        }
        
        return selected;
    }
    
    /**
     * Get a single random upgrade, weighted by rarity
     */
    private Upgrade getRandomUpgrade() {
        int roll = random.nextInt(100);
        
        // Rarity weights: 50% common, 30% uncommon, 15% rare, 5% legendary
        if (roll < 50) {
            return commonUpgrades.get(random.nextInt(commonUpgrades.size())).getClass().getSimpleName().equals("DamageBoost") ? 
                   new DamageBoost(5 + random.nextInt(3), 0) : 
                   commonUpgrades.get(random.nextInt(commonUpgrades.size()));
        } else if (roll < 80) {
            return uncommonUpgrades.get(random.nextInt(uncommonUpgrades.size()));
        } else if (roll < 95) {
            return rareUpgrades.get(random.nextInt(rareUpgrades.size()));
        } else {
            return legendaryUpgrades.get(random.nextInt(legendaryUpgrades.size()));
        }
    }
    
    /**
     * Get upgrades with some randomization in values
     */
    public Upgrade getRandomUpgradeOfRarity(int rarity) {
        ArrayList<Upgrade> pool = switch(rarity) {
            case 0 -> commonUpgrades;
            case 1 -> uncommonUpgrades;
            case 2 -> rareUpgrades;
            case 3 -> legendaryUpgrades;
            default -> commonUpgrades;
        };
        
        return pool.get(random.nextInt(pool.size()));
    }
}
