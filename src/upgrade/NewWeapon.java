package upgrade;

import entity.Player;

/**
 * Replaces the current gun with a new one with different stats
 */
public class NewWeapon extends Upgrade {
    private String weaponName;
    private int damage;
    private int fireRate;
    private int bulletSpeed;
    
    public NewWeapon(String weaponName, int damage, int fireRate, int bulletSpeed, int rarity) {
        super(weaponName, "Switch to " + weaponName, rarity);
        this.weaponName = weaponName;
        this.damage = damage;
        this.fireRate = fireRate;
        this.bulletSpeed = bulletSpeed;
    }
    
    @Override
    public void apply(Player player) {
        // Keep the gun but update stats
        player.gun.bulletDamage = damage;
        player.gun.fireRate = fireRate;
        player.gun.bulletSpeed = bulletSpeed;
        System.out.println("Applied: New Weapon - " + weaponName + 
                         " (Damage: " + damage + ", FireRate: " + fireRate + ")");
    }
    
    public String getWeaponName() {
        return weaponName;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public int getFireRate() {
        return fireRate;
    }
    
    public int getBulletSpeed() {
        return bulletSpeed;
    }
}
