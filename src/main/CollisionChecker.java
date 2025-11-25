package main;

import entity.Entity;
public class CollisionChecker {
  GamePanel gp;
  public CollisionChecker(GamePanel gp) {
    this.gp = gp;
  }
  public void checkTile(Entity entity) {

    int entityLeftX = entity.x + entity.solidArea.x;
    int entityRightX = entity.x + entity.solidArea.x+ entity.solidArea.width;

    int entityTopY = entity.y + entity.solidArea.y;
    int entityBottomY = entity.y + entity.solidArea.y + entity.solidArea.height;

    int entityLeftCol = entityLeftX/gp.tileSize;
    int entityRightCol = entityRightX/gp.tileSize;
    int entityTopRow = entityTopY/gp.tileSize;
    int entityBottomRow = entityBottomY/gp.tileSize;

    int tileNum1, tileNum2;

    switch (entity.direction) {
      case "up":
        entityTopRow = (entityTopY - entity.speed)/gp.tileSize;
        // Clamp to valid range
        if (entityTopRow < 0) entityTopRow = 0;
        if (entityLeftCol < 0) entityLeftCol = 0;
        if (entityRightCol >= gp.screenCol) entityRightCol = gp.screenCol - 1;
        
        tileNum1 = gp.tileManager.collisionTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityRightCol][entityTopRow];
        if (gp.tileManager.getTile(tileNum1).collision == true || gp.tileManager.getTile(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
      case "down":
        entityBottomRow = (entityBottomY + entity.speed)/gp.tileSize;
        // Clamp to valid range
        if (entityBottomRow >= gp.screenRow) entityBottomRow = gp.screenRow - 1;
        if (entityLeftCol < 0) entityLeftCol = 0;
        if (entityRightCol >= gp.screenCol) entityRightCol = gp.screenCol - 1;
        
        tileNum1 = gp.tileManager.collisionTileNum[entityLeftCol][entityBottomRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityRightCol][entityBottomRow];
        if (gp.tileManager.getTile(tileNum1).collision == true || gp.tileManager.getTile(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
      case "left":
        entityLeftCol = (entityLeftX - entity.speed)/gp.tileSize;
        // Clamp to valid range
        if (entityLeftCol < 0) entityLeftCol = 0;
        if (entityTopRow < 0) entityTopRow = 0;
        if (entityBottomRow >= gp.screenRow) entityBottomRow = gp.screenRow - 1;
        
        tileNum1 = gp.tileManager.collisionTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityLeftCol][entityBottomRow];
        if (gp.tileManager.getTile(tileNum1).collision == true || gp.tileManager.getTile(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
      case "right":
        entityRightCol = (entityRightX + entity.speed)/gp.tileSize;
        // Clamp to valid range
        if (entityRightCol >= gp.screenCol) entityRightCol = gp.screenCol - 1;
        if (entityTopRow < 0) entityTopRow = 0;
        if (entityBottomRow >= gp.screenRow) entityBottomRow = gp.screenRow - 1;
        
        tileNum1 = gp.tileManager.collisionTileNum[entityRightCol][entityTopRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityRightCol][entityBottomRow];
        if (gp.tileManager.getTile(tileNum1).collision == true || gp.tileManager.getTile(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
    }
    
  }
}
