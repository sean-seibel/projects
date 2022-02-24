package model.unit.sheep;

import model.unit.Unit;
import model.util.Direction;
import model.util.Vector;

public class ObedientSheep extends Sheep {

  public ObedientSheep(int x, int y) {
    super(x, y, Direction.UP);
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
    switch (shep.getDirection()) {
      case RIGHT:
        return new Vector(1,0);
      case LEFT:
        return new Vector(-1,0);
      case UP:
        return new Vector(0,-1);
      case DOWN:
        return new Vector(0,1);
    }
    return new Vector(0,0);
  }
}
