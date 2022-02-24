package model.unit.sheep;

import model.unit.Unit;
import model.util.Direction;
import model.util.Vector;

public class SeekerSheep extends Sheep {


  public SeekerSheep(int x, int y) {
    super(x, y, Direction.RIGHT);
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
    double targetX = shep.getLocation()[0];
    double targetY = shep.getLocation()[1];
    switch (shep.getDirection()) {
      case UP: targetX -= 0.001; break;
      case DOWN: targetX += 0.001; break;
      case LEFT: targetY += 0.001; break;
      case RIGHT: targetY -= 0.001; break;
    }
    return new Vector(
            targetX - this.x,
            targetY - this.y
    ).normalize();
  }
}
