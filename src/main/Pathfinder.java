package main;

import java.util.*;

public class Pathfinder {
    
    GamePanel gp;
    Node[][] nodes;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;
    
    public Pathfinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }
    
    public void instantiateNodes() {
        nodes = new Node[gp.screenCol][gp.screenRow];
        
        int col = 0;
        int row = 0;
        
        while (col < gp.screenCol && row < gp.screenRow) {
            nodes[col][row] = new Node(col, row);
            col++;
            if (col == gp.screenCol) {
                col = 0;
                row++;
            }
        }
    }
    
    public void resetNodes() {
        int col = 0;
        int row = 0;
        
        while (col < gp.screenCol && row < gp.screenRow) {
            // Reset cost
            nodes[col][row].gCost = 0;
            nodes[col][row].fCost = 0;
            nodes[col][row].open = false;
            nodes[col][row].checked = false;
            nodes[col][row].parent = null;
            
            col++;
            if (col == gp.screenCol) {
                col = 0;
                row++;
            }
        }
        
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }
    
    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
        resetNodes();
        
        // Bounds check
        if (startCol < 0 || startCol >= gp.screenCol || startRow < 0 || startRow >= gp.screenRow) {
            return;
        }
        if (goalCol < 0 || goalCol >= gp.screenCol || goalRow < 0 || goalRow >= gp.screenRow) {
            return;
        }
        
        startNode = nodes[startCol][startRow];
        currentNode = startNode;
        goalNode = nodes[goalCol][goalRow];
        openList.add(currentNode);
        
        int col = 0;
        int row = 0;
        
        while (col < gp.screenCol && row < gp.screenRow) {
            // Set solid nodes based on collision map
            int tileNum = gp.tileManager.collisionTileNum[col][row];
            if (gp.tileManager.getTile(tileNum) != null && 
                gp.tileManager.getTile(tileNum).collision == true) {
                nodes[col][row].solid = true;
            }
            
            // Set cost
            getCost(nodes[col][row]);
            
            col++;
            if (col == gp.screenCol) {
                col = 0;
                row++;
            }
        }
    }
    
    public void getCost(Node node) {
        // G cost (distance from start)
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;
        
        // H cost (distance to goal)
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;
        
        // F cost
        node.fCost = node.gCost + node.hCost;
    }
    
    public boolean search() {
        while (!goalReached && step < 500) { // Max 500 steps to prevent infinite loop
            int col = currentNode.col;
            int row = currentNode.row;
            
            currentNode.checked = true;
            openList.remove(currentNode);
            
            // Open Up node
            if (row - 1 >= 0) {
                openNode(nodes[col][row - 1]);
            }
            // Open Left node
            if (col - 1 >= 0) {
                openNode(nodes[col - 1][row]);
            }
            // Open Down node
            if (row + 1 < gp.screenRow) {
                openNode(nodes[col][row + 1]);
            }
            // Open Right node
            if (col + 1 < gp.screenCol) {
                openNode(nodes[col + 1][row]);
            }
            
            // Find the best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;
            
            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                } else if (openList.get(i).fCost == bestNodefCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }
            
            if (openList.size() == 0) {
                break; // No path found
            }
            
            currentNode = openList.get(bestNodeIndex);
            
            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
            
            step++;
        }
        
        return goalReached;
    }
    
    public void openNode(Node node) {
        if (!node.open && !node.checked && !node.solid) {
            node.open = true;
            node.parent = currentNode;
            openList.add(node);
        }
    }
    
    public void trackThePath() {
        Node current = goalNode;
        
        while (current != startNode) {
            pathList.add(0, current);
            current = current.parent;
        }
    }
}
