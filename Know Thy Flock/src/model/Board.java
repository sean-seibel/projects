package model;

import java.util.List;

import model.terrain.Terrain;
import model.unit.Unit;
import model.util.Direction;

public interface Board {

  String move(Direction dir);

  List<Unit> getUnitList();
  Terrain[][] getTerrain();
  int getMoves();

  void reset();
  void undo();

  int getWidth();
  int getHeight();
}
