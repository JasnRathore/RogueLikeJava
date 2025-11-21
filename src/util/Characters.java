package util;

import java.util.Map;

public class Characters {

  private static Map<Character, Integer> normal = Map.ofEntries(
    Map.entry('A', 162),
    Map.entry('B', 163),
    Map.entry('C', 164),
    Map.entry('D', 165),
    Map.entry('E', 166),
    Map.entry('F', 167),
    Map.entry('G', 168),
    Map.entry('H', 169),
    Map.entry('I', 170),
    Map.entry('J', 171),
    Map.entry('K', 172),
    Map.entry('L', 173),
    Map.entry('M', 174),
    Map.entry('N', 180),
    Map.entry('O', 181),
    Map.entry('P', 182),
    Map.entry('Q', 183),
    Map.entry('R', 184),
    Map.entry('S', 185),
    Map.entry('T', 186),
    Map.entry('U', 187),
    Map.entry('V', 188),
    Map.entry('W', 189),
    Map.entry('X', 190),
    Map.entry('Y', 191),
    Map.entry('Z', 192),

    Map.entry('0', 147),
    Map.entry('1', 148),
    Map.entry('2', 149),
    Map.entry('3', 150),
    Map.entry('4', 151),
    Map.entry('5', 152),
    Map.entry('6', 153),
    Map.entry('7', 154),
    Map.entry('8', 155),
    Map.entry('9', 156)
  );


  private static Map<Character, Integer> bold = Map.ofEntries(

    Map.entry('A', 108),
    Map.entry('B', 109),
    Map.entry('C', 110),
    Map.entry('D', 111),
    Map.entry('E', 112),
    Map.entry('F', 113),
    Map.entry('G', 114),
    Map.entry('H', 115),
    Map.entry('I', 116),
    Map.entry('J', 117),
    Map.entry('K', 118),
    Map.entry('L', 119),
    Map.entry('M', 120),
    Map.entry('N', 126),
    Map.entry('O', 127),
    Map.entry('P', 128),
    Map.entry('Q', 129),
    Map.entry('R', 130),
    Map.entry('S', 131),
    Map.entry('T', 132),
    Map.entry('U', 133),
    Map.entry('V', 134),
    Map.entry('W', 135),
    Map.entry('X', 136),
    Map.entry('Y', 137),
    Map.entry('Z', 138),

    Map.entry('0', 93),
    Map.entry('1', 94),
    Map.entry('2', 95),
    Map.entry('3', 96),
    Map.entry('4', 97),
    Map.entry('5', 98),
    Map.entry('6', 99),
    Map.entry('7', 100),
    Map.entry('8', 101),
    Map.entry('9', 102)
  );
  
  public static int getNormalCharTextureID(char c) {
    if (c == ' ') {
      return 1000;
    }
    return normal.get(Character.toUpperCase(c));
  }

  public static int getBoldCharTextureID(char c) {
    if (c == ' ') {
      return 1000;
    }
    return bold.get(Character.toUpperCase(c));
  }
}
