package main;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem {
    
    GamePanel gp;
    private ArrayList<Particle> particles;
    private static final int MAX_PARTICLES = 300;
    private Random random;
    
    public ParticleSystem(GamePanel gp) {
        this.gp = gp;
        this.particles = new ArrayList<>();
        this.random = new Random();
    }
    
    // Particle types enum
    public enum ParticleType {
        BLOOD,           // Enemy hit
        SPARK,           // Bullet impact
        EXPLOSION,       // Enemy death
        MUZZLE_FLASH,    // Gun fire
        DUST,            // Movement
        HEAL,            // Health pickup
        ENERGY           // Generic energy effect
    }
    
    // Create pixel-art blood particles when enemy is hit
    public void createBloodSplatter(int x, int y, int velocityX, int velocityY) {
        int count = random.nextInt(3) + 5; // 5-7 particles (fewer, more visible)
        for (int i = 0; i < count; i++) {
            if (particles.size() >= MAX_PARTICLES) break;
            
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 0.8 + 0.3; // Reduced speed: 0.3-1.1
            
            // Bias direction based on hit direction
            double vx = Math.cos(angle) * speed + velocityX * 0.3;
            double vy = Math.sin(angle) * speed + velocityY * 0.3;
            
            // Simple dark red color
            Color color = new Color(139, 0, 0); // Dark red
            
            particles.add(new Particle(
                x, y, vx, vy,
                4,                          // Fixed size for pixel art (4x4 pixels)
                color,
                12 + random.nextInt(8),     // Reduced lifetime: 12-20 frames
                0.88,                        // Increased friction (was 0.96)
                0.35,                        // Increased gravity (was 0.2)
                ParticleType.BLOOD
            ));
        }
    }
    
    // Create pixel spark particles for bullet impacts
    public void createSparks(int x, int y, double angle) {
        int count = random.nextInt(2) + 4; // 4-5 particles
        for (int i = 0; i < count; i++) {
            if (particles.size() >= MAX_PARTICLES) break;
            
            // Spread around impact angle
            double spreadAngle = angle + Math.PI + (random.nextDouble() - 0.5) * Math.PI * 0.5;
            double speed = random.nextDouble() * 3 + 2;
            
            double vx = Math.cos(spreadAngle) * speed;
            double vy = Math.sin(spreadAngle) * speed;
            
            // Bright yellow-orange for sparks
            Color[] sparkColors = {
                new Color(255, 255, 0),   // Yellow
                new Color(255, 200, 0),   // Orange-yellow
                new Color(255, 150, 0)    // Orange
            };
            Color color = sparkColors[random.nextInt(sparkColors.length)];
            
            particles.add(new Particle(
                x, y, vx, vy,
                3,                          // Small pixel size
                color,
                12 + random.nextInt(8),     // Lifetime 12-20 frames (quick)
                0.94,                        // Friction
                0.15,                        // Gravity
                ParticleType.SPARK
            ));
        }
    }
    
    // Create pixel explosion particles when enemy dies
    public void createExplosion(int x, int y) {
        int count = random.nextInt(8) + 12; // 12-20 particles
        for (int i = 0; i < count; i++) {
            if (particles.size() >= MAX_PARTICLES) break;
            
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 4 + 1;
            
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            // Pixel art explosion palette
            Color[] explosionColors = {
                new Color(255, 100, 0),   // Orange
                new Color(255, 200, 0),   // Yellow
                new Color(255, 50, 0),    // Red-orange
                new Color(200, 0, 0)      // Dark red
            };
            Color color = explosionColors[random.nextInt(explosionColors.length)];
            
            particles.add(new Particle(
                x, y, vx, vy,
                5 + random.nextInt(2),      // Size 5-6 pixels
                color,
                20 + random.nextInt(20),    // Lifetime 20-40 frames
                0.96,                        // Friction
                0.08,                        // Light gravity
                ParticleType.EXPLOSION
            ));
        }
    }
    
    // Create pixel muzzle flash particles
    public void createMuzzleFlash(int x, int y, double angle) {
        int count = random.nextInt(2) + 3; // 3-4 particles
        for (int i = 0; i < count; i++) {
            if (particles.size() >= MAX_PARTICLES) break;
            
            double spreadAngle = angle + (random.nextDouble() - 0.5) * 0.3;
            double speed = random.nextDouble() * 5 + 3;
            
            double vx = Math.cos(spreadAngle) * speed;
            double vy = Math.sin(spreadAngle) * speed;
            
            // Bright purple/cyan for energy weapon
            Color[] muzzleColors = {
                new Color(150, 100, 255), // Purple
                new Color(200, 150, 255), // Light purple
                new Color(100, 200, 255)  // Cyan
            };
            Color color = muzzleColors[random.nextInt(muzzleColors.length)];
            
            particles.add(new Particle(
                x, y, vx, vy,
                4,                          // Fixed pixel size
                color,
                6 + random.nextInt(4),      // Lifetime 6-10 frames (very quick)
                0.88,                        // High friction
                0.0,                         // No gravity
                ParticleType.MUZZLE_FLASH
            ));
        }
    }
    
    // Create pixel dust particles for movement
    public void createDust(int x, int y) {
        if (random.nextInt(4) != 0) return; // Only spawn 1/4 of the time
        if (particles.size() >= MAX_PARTICLES) return;
        
        double vx = (random.nextDouble() - 0.5) * 0.5;
        double vy = -random.nextDouble() * 0.3;
        
        // Simple gray/tan dust
        Color color = new Color(160, 150, 140);
        
        particles.add(new Particle(
            x, y, vx, vy,
            3,                          // Small pixel
            color,
            15 + random.nextInt(10),    // Lifetime 15-25 frames
            0.98,                        // Very little friction
            0.0,                         // No gravity
            ParticleType.DUST
        ));
    }
    
    // Create pixel heal particles
    public void createHealEffect(int x, int y) {
        int count = random.nextInt(4) + 6; // 6-10 particles
        for (int i = 0; i < count; i++) {
            if (particles.size() >= MAX_PARTICLES) break;
            
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 1.5 + 0.5;
            
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 1.5; // Float upward
            
            // Bright green
            Color color = new Color(0, 255, 0);
            
            particles.add(new Particle(
                x, y, vx, vy,
                4,                          // Fixed pixel size
                color,
                30 + random.nextInt(20),    // Lifetime 30-50 frames
                0.98,                        // Little friction
                -0.04,                       // Negative gravity (float up)
                ParticleType.HEAL
            ));
        }
    }
    
    // Create pixel energy particles
    public void createEnergyParticles(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            if (particles.size() >= MAX_PARTICLES) break;
            
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 2 + 0.5;
            
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            particles.add(new Particle(
                x, y, vx, vy,
                3,                          // Small pixel
                color,
                20 + random.nextInt(20),    // Lifetime 20-40 frames
                0.97,                        // Friction
                0.0,                         // No gravity
                ParticleType.ENERGY
            ));
        }
    }
    
    public void update() {
        // Update all particles, remove dead ones
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (!p.isAlive()) {
                particles.remove(i);
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        for (Particle p : particles) {
            p.draw(g2);
        }
    }
    
    public int getParticleCount() {
        return particles.size();
    }
    
    public void clear() {
        particles.clear();
    }
    
    // Inner Particle class - Pixel Art Style
    private class Particle {
        double x, y;
        double vx, vy;
        int size;
        Color color;
        int lifetime;
        int maxLifetime;
        double friction;
        double gravity;
        ParticleType type;
        
        public Particle(double x, double y, double vx, double vy, int size, 
                       Color color, int lifetime, double friction, double gravity,
                       ParticleType type) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
            this.lifetime = lifetime;
            this.maxLifetime = lifetime;
            this.friction = friction;
            this.gravity = gravity;
            this.type = type;
        }
        
        public void update() {
            // Apply velocity
            x += vx;
            y += vy;
            
            // Apply friction
            vx *= friction;
            vy *= friction;
            
            // Apply gravity
            vy += gravity;
            
            // Decrease lifetime
            lifetime--;
        }
        
        public void draw(Graphics2D g2) {
            // Calculate alpha based on lifetime - more abrupt fade for pixel art
            float alpha = (float) lifetime / maxLifetime;
            
            // Pixel art style: snap fade at certain thresholds
            if (alpha > 0.7f) {
                alpha = 1.0f;
            } else if (alpha > 0.4f) {
                alpha = 0.7f;
            } else if (alpha > 0.2f) {
                alpha = 0.4f;
            } else {
                alpha = 0.2f;
            }
            
            Composite oldComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // Snap positions to pixel grid for crisp rendering
            int pixelX = (int)Math.round(x);
            int pixelY = (int)Math.round(y);
            
            // Draw based on type - all as simple pixel squares/rectangles
            switch (type) {
                case BLOOD:
                    // Draw as pixel square
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    break;
                    
                case SPARK:
                    // Draw as bright pixel with small cross
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    // Add cross shape for sparkle
                    g2.fillRect(pixelX - 1, pixelY - size, 2, size*2 + 2);
                    g2.fillRect(pixelX - size, pixelY - 1, size*2 + 2, 2);
                    break;
                    
                case EXPLOSION:
                    // Draw as pixel square
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    break;
                    
                case MUZZLE_FLASH:
                    // Draw as bright pixel square
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    break;
                    
                case DUST:
                    // Draw as small pixel
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    break;
                    
                case HEAL:
                    // Draw as pixel with plus sign
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    // Add plus shape
                    g2.fillRect(pixelX - 1, pixelY - size/2 - 2, 2, size + 4);
                    g2.fillRect(pixelX - size/2 - 2, pixelY - 1, size + 4, 2);
                    break;
                    
                case ENERGY:
                    // Draw as pixel square
                    g2.setColor(color);
                    g2.fillRect(pixelX - size/2, pixelY - size/2, size, size);
                    break;
            }
            
            g2.setComposite(oldComposite);
        }
        
        public boolean isAlive() {
            return lifetime > 0;
        }
    }
}
