package model.unit;

import model.util.Direction;
import model.util.Vector;

public interface Unit {

  int[] getLocation();

  Direction getDirection();

  void setDirection(Direction dir);

  void setLocation(int x, int y);

  String getName();

  boolean movesAutomatically(); // true if below will (or may change) unit's location

  Vector desiredDirection(Unit[][] unitField, Unit ofInterest);

  boolean move(Direction dir, Unit[] neighborsNESW);

}
