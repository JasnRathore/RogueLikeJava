package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class LootManager {

    public static class LootEntry {
        public String type; // COIN, HEAL, SHIELD
        public double chance;
        public int min;
        public int max;
        public int value;
    }

    private ArrayList<LootEntry> entries = new ArrayList<>();
    private Random rand = new Random();

    public LootManager() {
        loadFromFile("res/loot_drops.txt");
    }

    private void loadFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                // format: TYPE chance min max [value]
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    LootEntry e = new LootEntry();
                    e.type = parts[0].toUpperCase();
                    e.chance = Double.parseDouble(parts[1]);
                    e.min = Integer.parseInt(parts[2]);
                    e.max = Integer.parseInt(parts[3]);
                    e.value = (parts.length >=5) ? Integer.parseInt(parts[4]) : 0;
                    entries.add(e);
                }
            }
        } catch (Exception ex) {
            System.out.println("Could not load loot_drops.txt: " + ex.getMessage());
        }
    }

    // Returns a generated pickup as [type,amount] or null if none
    public String[] rollLoot() {
        for (LootEntry e : entries) {
            if (rand.nextDouble() <= e.chance) {
                int amt = e.min + rand.nextInt(e.max - e.min + 1);
                return new String[]{e.type, Integer.toString(amt)};
            }
        }
        return null;
    }
}
