package model.unit;

import model.util.Direction;
import model.util.Vector;

//simple unit that doesn't move
public class Rock implements Unit {

  int[] location;

  public Rock(int x, int y) {
    this.location = new int[]{x, y};
  }
  @Override
  public int[] getLocation() {
    return location;
  }

  @Override
  public Direction getDirection() {
    return Direction.DOWN;
  }

  @Override
  public String getName() {
    return "rock";
  }

  @Override
  public boolean movesAutomatically() {
    return false;
  }

  @Override
  public Vector desiredDirection(Unit[][] unitField, Unit o) {
    return new Vector(0.0, 0.0);
  }

  @Override
  public boolean move(Direction dir, Unit[] neighborsNESW) {
    return false;
  }

  @Override
  public void setDirection(Direction dir) {
    //always down
  }

  @Override
  public void setLocation(int x, int y) {
    this.location = new int[]{x, y};
  }
}
