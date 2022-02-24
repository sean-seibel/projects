package model.unit.sheep;

import model.unit.Unit;
import model.util.Direction;
import model.util.Vector;

//social to other sheep
public class SocialSheep extends Sheep {

  private final int range;

  public SocialSheep(int x, int y, int range) {
    super(x, y, Direction.DOWN);
    this.range = range;
  }

  @Override
  public String getName() {
    return "sheep";
  }

  @Override
  public boolean movesAutomatically() {
    return true;
  }

  @Override
  public Vector desiredDirection(Unit[][] unitField, Unit shep) {
    double vx = 0.0;
    double vy = 0.0;
    for (int nx = x - range; nx <= x + range; nx++) {
      if (nx >= 0 && nx < unitField.length) {
        for (int ny = y - range; ny <= y + range; ny++) {
          if (ny >= 0 && ny < unitField[nx].length) {
            Unit unit = unitField[nx][ny];
            if (unit != null && unit.getName().equals("sheep")) {
              vx += nx-x;
              vy += ny-y;
            }
          }
        }
      }
    }
    return new Vector(vx, vy);
  }
}
