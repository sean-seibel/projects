package model.unit.sheep;

import model.unit.Unit;
import model.util.Direction;
import model.util.Vector;

public abstract class Sheep implements Unit {

  protected int x;
  protected int y;
  protected Direction directionFacing;

  Sheep(int x, int y, Direction directionFacing) {
    this.x = x;
    this.y = y;
    this.directionFacing = directionFacing;
  }

  @Override
  public int[] getLocation() {
    return new int[]{x, y};
  }

  @Override
  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public Direction getDirection() {
    return directionFacing;
  }

  @Override
  public abstract String getName();

  @Override
  public abstract boolean movesAutomatically();

  @Override
  public abstract Vector desiredDirection(Unit[][] unitField, Unit shep);

  @Override
  public boolean move(Direction dir, Unit[] neighborsNESW) {
    boolean success = false;
    switch (dir) {
      case UP:
        if (neighborsNESW[0] == null) {
          y -= 1;
          success = true;
        }
        break;
      case DOWN:
        if (neighborsNESW[2] == null) {
          y += 1;
          success = true;
        }
        break;
      case LEFT:
        if (neighborsNESW[3] == null) {
          x -= 1;
          success = true;
        }
        break;
      case RIGHT:
        if (neighborsNESW[1] == null) {
          x += 1;
          success = true;
        }
        break;
      case STAY:
        break;
    }
    directionFacing = dir;

    return success;
  }

  @Override
  public void setDirection(Direction dir) {
    this.directionFacing = dir;
  }
}
