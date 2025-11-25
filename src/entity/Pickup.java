package entity;

import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

public class Pickup {
    public enum Type { COIN, HEAL, SHIELD }

    public int x, y;
    public Type type;
    public int amount;
    public boolean active = true;

    private int size = 12;
    private GamePanel gp;

    public Pickup(GamePanel gp, int x, int y, Type type, int amount) {
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.type = type;
        this.amount = amount;
    }

    public void update() {
        if (!active) return;
        // Simple gravity/float could be implemented; keep static for now
        // Check collision with player
        if (gp.player.getHitbox().intersects(getHitbox())) {
            collect();
        }
    }

    private void collect() {
        active = false;
        switch (type) {
            case COIN:
                gp.addCoins(amount);
                gp.addScore(amount * 5); // coins give score too
                gp.particleSystem.createEnergyParticles(x, y, new Color(255, 215, 0), 6);
                break;
            case HEAL:
                gp.player.health += amount;
                if (gp.player.health > gp.player.maxHealth) gp.player.health = gp.player.maxHealth;
                gp.particleSystem.createEnergyParticles(x, y, new Color(100,255,100), 8);
                gp.particleSystem.createHealingNumber(amount, x, y - 6);
                gp.addScore(amount * 2);
                break;
            case SHIELD:
                // If player has TempShield upgrade, add to it; otherwise create a short-lived shield
                if (gp.player.getTempShield() != null) {
                    gp.player.getTempShield().addToCurrentShield(amount);
                } else {
                    // Create a temporary shield upgrade instance to hold the pickup shield
                    upgrade.TempShield ps = new upgrade.TempShield(amount, 300, 0);
                    ps.addToCurrentShield(amount);
                    gp.player.addUpgrade(ps);
                }
                gp.particleSystem.createEnergyParticles(x, y, new Color(100,200,255), 6);
                gp.particleSystem.createShieldDamageNumber(amount, x, y - 6);
                gp.addScore(amount * 3);
                break;
        }
    }

    public void draw(Graphics2D g2) {
        if (!active) return;
        switch (type) {
            case COIN:
                g2.setColor(new Color(255, 215, 0));
                g2.fillOval(x - size/2, y - size/2, size, size);
                break;
            case HEAL:
                g2.setColor(new Color(100,255,100));
                g2.fillRect(x - size/2, y - size/2, size, size);
                break;
            case SHIELD:
                g2.setColor(new Color(100,200,255));
                g2.fillRect(x - size/2, y - size/2, size, size);
                break;
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x - size/2, y - size/2, size, size);
    }
}
