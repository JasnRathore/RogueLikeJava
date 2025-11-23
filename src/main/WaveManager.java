package main;

import entity.Enemy;
import entity.Player;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import overlay.WaveOverlay;

public class WaveManager {
    
    GamePanel gp;
    Player player;

    WaveOverlay wo;
    
    public ArrayList<Enemy> enemies;
    private Random random;
    
    public int currentWave = 0;
    private boolean waveActive = false;
    private boolean cooldownActive = false;
    
    private int cooldownTimer = 0;
    private int cooldownDuration = 180; // 3 seconds at 60 FPS
    
    private int enemiesToSpawn = 0;
    private int spawnTimer = 0;
    private int spawnInterval = 60; // Spawn every 1 second
    
    private ArrayList<SpawnPoint> spawnPoints;
    
    // Cache player hitbox to avoid repeated Rectangle creation
    private Rectangle playerHitbox;
    
    public WaveManager(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.spawnPoints = new ArrayList<>();
        this.playerHitbox = new Rectangle();
        wo = new WaveOverlay(gp);
        
        setupSpawnPoints();
    }
    
    private void setupSpawnPoints() {
        spawnPoints.add(new SpawnPoint(0, 17));
        spawnPoints.add(new SpawnPoint(0, 10));
        spawnPoints.add(new SpawnPoint(31, 6));
        spawnPoints.add(new SpawnPoint(31, 7));
        spawnPoints.add(new SpawnPoint(16, 0));
        spawnPoints.add(new SpawnPoint(24, 17));
    }
    
    public void addSpawnPoint(int tileCol, int tileRow) {
        spawnPoints.add(new SpawnPoint(tileCol, tileRow));
    }
    
    public void clearSpawnPoints() {
        spawnPoints.clear();
    }
    
    public void startNextWave() {
        if (!waveActive && !cooldownActive) {
            currentWave++;
            waveActive = true;
            cooldownActive = false;
            
            // Scale enemies with wave number
            enemiesToSpawn = 5 + (currentWave * 3); // Wave 1: 8 enemies, Wave 2: 11, etc.
            spawnTimer = 0;
            
            System.out.println("Wave " + currentWave + " started! Enemies: " + enemiesToSpawn);
        }
    }
    
    public void update() {
        int seconds = cooldownTimer / 60 + 1;
        wo.update(currentWave, enemies.size(), waveActive, cooldownActive, seconds);

        // Update player hitbox once per frame
        Rectangle pHitbox = player.getHitbox();
        playerHitbox.setBounds(pHitbox.x, pHitbox.y, pHitbox.width, pHitbox.height);

        // Update enemies - iterate backwards for safe removal
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            
            if (enemy.alive) {
                enemy.update();
                
                // Optimize collision check
                checkPlayerEnemyCollision(enemy);
            } else {
                enemies.remove(i); // Remove dead enemies
            }
        }
        
        if (waveActive) {
            spawnTimer++;
            
            if (enemiesToSpawn > 0 && spawnTimer >= spawnInterval) {
                spawnEnemy();
                enemiesToSpawn--;
                spawnTimer = 0;
            }
            
            // Check if wave is complete
            if (enemiesToSpawn == 0 && enemies.isEmpty()) {
                waveActive = false;
                cooldownActive = true;
                cooldownTimer = cooldownDuration;
                System.out.println("Wave " + currentWave + " complete! Cooldown period...");
            }
        }
        
        // Cooldown period
        if (cooldownActive) {
            cooldownTimer--;
            if (cooldownTimer <= 0) {
                cooldownActive = false;
                // Auto-start next wave
                startNextWave();
            }
        }
    }
    
    private void checkPlayerEnemyCollision(Enemy enemy) {
        // Quick distance check first before expensive intersection
        int dx = enemy.x - player.x;
        int dy = enemy.y - player.y;
        int distSquared = dx * dx + dy * dy;
        
        // Skip if too far (rough estimate - adjust based on entity sizes)
        // Assuming max combined size is ~100 pixels, so 100^2 = 10000
        if (distSquared > 10000) return;
        
        // Now do precise collision check
        Rectangle enemyHitbox = enemy.getHitbox();
        if (enemyHitbox.intersects(playerHitbox)) {
            // Deal damage to player and push enemy back slightly
            if (player.canTakeDamage()) {
                player.takeDamage(enemy.damage);
                knockbackEnemy(enemy, player);
            }
        }
    }
    
    private void spawnEnemy() {
        if (spawnPoints.isEmpty()) {
            System.err.println("No spawn points defined!");
            return;
        }
        
        Enemy enemy = new Enemy(gp, player);
        
        // Pick a random spawn point
        SpawnPoint spawn = spawnPoints.get(random.nextInt(spawnPoints.size()));
        
        // Convert tile coordinates to pixel coordinates
        int spawnX = spawn.tileCol * gp.tileSize;
        int spawnY = spawn.tileRow * gp.tileSize;
        
        enemy.spawn(spawnX, spawnY);
        enemies.add(enemy);
    }
    
    private void knockbackEnemy(Enemy enemy, Player player) {
        // Push enemy away from player slightly
        int dx = enemy.x - player.x;
        int dy = enemy.y - player.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist > 0) {
            enemy.x += (int) (dx / dist * 10);
            enemy.y += (int) (dy / dist * 10);
        }
    }
    
    public void draw(Graphics2D g2) {
        // Frustum culling - only draw enemies on screen
        for (Enemy enemy : enemies) {
            // Check if enemy is within screen bounds (with small margin)
            if (enemy.x + 32 >= 0 && enemy.x <= gp.screenWidth &&
                enemy.y + 32 >= 0 && enemy.y <= gp.screenHeight) {
                enemy.draw(g2);
            }
        }
    }
    
    public void drawWaveUI(Graphics2D g2) {
        wo.draw(g2);      
    }
    
    private void drawSpawnPoints(Graphics2D g2) {
        g2.setColor(new Color(255, 0, 0, 100));
        for (SpawnPoint sp : spawnPoints) {
            int x = sp.tileCol * gp.tileSize;
            int y = sp.tileRow * gp.tileSize;
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }
    }
    
    public boolean isWaveActive() {
        return waveActive;
    }
    
    public boolean isCooldownActive() {
        return cooldownActive;
    }
    
    private class SpawnPoint {
        int tileCol;
        int tileRow;
        
        SpawnPoint(int tileCol, int tileRow) {
            this.tileCol = tileCol;
            this.tileRow = tileRow;
        }
    }
}
