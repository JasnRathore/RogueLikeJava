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
        // Bounds check
        if (entityLeftCol < 0 || entityLeftCol >= gp.screenCol || 
            entityRightCol < 0 || entityRightCol >= gp.screenCol ||
            entityTopRow < 0 || entityTopRow >= gp.screenRow) {
          entity.collisionOn = true;
          break;
        }
        tileNum1 = gp.tileManager.collisionTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityRightCol][entityTopRow];
        if (gp.tileManager.tiles.get(tileNum1).collision == true || gp.tileManager.tiles.get(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
      case "down":
        entityBottomRow = (entityBottomY + entity.speed)/gp.tileSize;
        // Bounds check
        if (entityLeftCol < 0 || entityLeftCol >= gp.screenCol || 
            entityRightCol < 0 || entityRightCol >= gp.screenCol ||
            entityBottomRow < 0 || entityBottomRow >= gp.screenRow) {
          entity.collisionOn = true;
          break;
        }
        tileNum1 = gp.tileManager.collisionTileNum[entityLeftCol][entityBottomRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityRightCol][entityBottomRow ];
        if (gp.tileManager.tiles.get(tileNum1).collision == true || gp.tileManager.tiles.get(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
      case "left":
        entityLeftCol = (entityLeftX - entity.speed)/gp.tileSize;
        // Bounds check
        if (entityLeftCol < 0 || entityLeftCol >= gp.screenCol || 
            entityTopRow < 0 || entityTopRow >= gp.screenRow ||
            entityBottomRow < 0 || entityBottomRow >= gp.screenRow) {
          entity.collisionOn = true;
          break;
        }
        tileNum1 = gp.tileManager.collisionTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityLeftCol][entityBottomRow ];
        if (gp.tileManager.tiles.get(tileNum1).collision == true || gp.tileManager.tiles.get(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
      case "right":
        entityRightCol = (entityRightX + entity.speed)/gp.tileSize;
        // Bounds check
        if (entityRightCol < 0 || entityRightCol >= gp.screenCol || 
            entityTopRow < 0 || entityTopRow >= gp.screenRow ||
            entityBottomRow < 0 || entityBottomRow >= gp.screenRow) {
          entity.collisionOn = true;
          break;
        }
        tileNum1 = gp.tileManager.collisionTileNum[entityRightCol][entityTopRow];
        tileNum2 = gp.tileManager.collisionTileNum[entityRightCol][entityBottomRow ];
        if (gp.tileManager.tiles.get(tileNum1).collision == true || gp.tileManager.tiles.get(tileNum2).collision == true) {
          entity.collisionOn = true;
        }
        break;
    }
    
  }
}
