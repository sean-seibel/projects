package model.unit;

import model.util.Direction;
import model.util.Vector;

public class Shepherd implements Unit {

  int x;
  int y;
  Direction directionFacing;

  public Shepherd(int x, int y) {
    this.x = x;
    this.y = y;
    this.directionFacing = Direction.DOWN;
  }

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
        dir = directionFacing;
        break;
    }
    directionFacing = dir;

    return success;
  }

  @Override
  public int[] getLocation() {
    return new int[]{x, y};
  }

  @Override
  public Direction getDirection() {
    return directionFacing;
  }

  @Override
  public void setDirection(Direction dir) {
    this.directionFacing = dir;
  }

  @Override
  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String getName() {
    return "shepherd";
  }

  @Override
  public boolean movesAutomatically() {
    return false;
  }

  @Override
  public Vector desiredDirection(Unit[][] unitField, Unit o) {
    return new Vector(0.0, 0.0);
  }
}
