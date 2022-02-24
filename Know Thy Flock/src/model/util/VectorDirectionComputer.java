package model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorDirectionComputer {

  //gives a list of priorities for direction to take based on a vector
  public static List<Direction> vectorToDirections(Vector v) {
    Map<Integer, Direction> valInDirection = new HashMap<>();

    int[] vals = new int[]{
            ((int) (v.dotProduct(new Vector(0, -1)) * 1000.0)) * 10 + 1,
            ((int) (v.dotProduct(new Vector(0, 1)) * 1000.0)) * 10 + 2,
            ((int) (v.dotProduct(new Vector(-1, 0)) * 1000.0)) * 10 + 3,
            ((int) (v.dotProduct(new Vector(1, 0)) * 1000.0)) * 10 + 4,
            5
    }; //these values ensure no collisions, and bias towards staying


    valInDirection.put(vals[0], Direction.UP);
    valInDirection.put(vals[1], Direction.DOWN);
    valInDirection.put(vals[2], Direction.LEFT);
    valInDirection.put(vals[3], Direction.RIGHT);
    valInDirection.put(vals[4], Direction.STAY);

    Arrays.sort(vals);

    List<Direction> retList = new ArrayList<>();

    //add from most favorable, to least favorable
    for (int i = vals.length - 1; i >= 0; i--) {
      retList.add(valInDirection.remove(vals[i]));
    }

    return retList;
  }

  public static List<Direction> vectorToDirectionsFavoritesOnly(Vector v) {
    List<Direction> wholeList = vectorToDirections(v);
    List<Direction> retList = new ArrayList<>();

    for (int i = 0; i < wholeList.size(); i++) {
      retList.add(wholeList.get(i));
      if (wholeList.get(i) == Direction.STAY) {
        i = wholeList.size();
      }
    }

    return retList;
  }
}
