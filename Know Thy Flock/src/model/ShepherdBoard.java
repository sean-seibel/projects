package model;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.DinicMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import model.terrain.Portal;
import model.terrain.Terrain;
import model.unit.Rock;
import model.unit.sheep.ObedientSheep;
import model.unit.sheep.SeekerSheep;
import model.unit.Shepherd;
import model.unit.Unit;
import model.unit.sheep.SheepySheep;
import model.unit.sheep.SocialSheep;
import model.util.DefaultMap;
import model.util.Direction;
import model.util.Point;
import model.util.VectorDirectionComputer;

public class ShepherdBoard implements Board {

  private Unit[][] unitGrid;
  private final Terrain[][] terrainGrid;
  private final Unit shep;
  private final List<Unit> units;
  private int moves;

  private final List<Unit[][]> unitLocHistory;
  private final List<Direction[][]> unitDirecHistory;

  public ShepherdBoard(List<String> code) {
    Map<Character, BiFunction<Integer, Integer, Unit>> charToUnit =
            new DefaultMap<>((x, y) -> null);
    charToUnit.put('S', (x, y) -> new Shepherd(x, y));
    charToUnit.put('2', (x, y) -> new ObedientSheep(x, y));
    charToUnit.put('3', (x, y) -> new SeekerSheep(x, y));
    charToUnit.put('4', (x, y) -> new SheepySheep(x, y, 2));
    charToUnit.put('5', (x, y) -> new SocialSheep(x, y, 3));
    charToUnit.put('6', (x, y) -> new SocialSheep(x, y, 2));
    charToUnit.put('0', (x, y) -> new Rock(x, y));
    Map<Character, Supplier<Terrain>> charToTerrain =
            new DefaultMap<>(() -> null);
    charToTerrain.put('#', () -> new Portal());

    Scanner xy = new Scanner(code.remove(0));

    int maximumX = xy.nextInt();
    int maximumY = xy.nextInt();

    unitGrid = new Unit[maximumX][maximumY];
    terrainGrid = new Terrain[maximumX][maximumY];
    Unit shepUnit = null;
    units = new ArrayList<>();
    moves = 0;

    for (int y = 0; y < maximumY; y++) {
      for (int x = 0; x < maximumX; x++) {
        char c = code.get(y).charAt(x);
        Unit unit = charToUnit.get(c).apply(x, y);
        if (unit != null) {
          if (unit.getName().equals("shepherd")) {
            shepUnit = unit;
          }
          units.add(unit);
        }
        unitGrid[x][y] = unit;
        terrainGrid[x][y] = charToTerrain.get(c).get();
      }
    }
    shep = shepUnit;
    unitLocHistory = new ArrayList<>();
    unitDirecHistory = new ArrayList<>();
    recordState();
  }

  @Override
  public String move(Direction dir) {
    String result = "";

    int[] shepLoc = shep.getLocation();

    Unit[] surroundingNESW = grabNeighborsNESW(shepLoc[0], shepLoc[1]);
    boolean moveSuccessful = shep.move(dir, surroundingNESW);

    if (moveSuccessful) {
      unitGrid[shepLoc[0]][shepLoc[1]] = null; // vacate the area
      unitGrid[shep.getLocation()[0]][shep.getLocation()[1]] = shep;
    }

    this.unitGrid = autoMoveUnitsFlowNet(); //slightly different logic would be needed to have
    // shep fill a space which will be vacated
    // might have to do all moving in one go, not shep first
    // (not the best to do with the sheep, would have to hardcode something to evaluate the shep first)

    // if any sheep are on the magic win squares -> teleport them away

    int sheepLeft = 0;

    for (int x = 0; x < terrainGrid.length; x++) {
      for (int y = 0; y < terrainGrid[x].length; y++) {
        if (unitGrid[x][y] != null &&
                unitGrid[x][y].getName().equalsIgnoreCase("sheep")) {
          if (terrainGrid[x][y] != null &&
                  terrainGrid[x][y].getName().equals("portal")) {
            unitGrid[x][y].setLocation(unitGrid.length / 2, -1);
            unitGrid[x][y] = null;
          } else {
            sheepLeft++;
          }
        }
      }
    }

    //run win seq
    //return some win output?
    if (sheepLeft == 0) {
      result = "win";
    }

    moves++;
    recordState();

    return result;
  }

  @Override
  public List<Unit> getUnitList() {
    return new ArrayList<>(units);
  }

  @Override
  public Terrain[][] getTerrain() {
    return terrainGrid;
  }

  public int getMoves() {
    return moves;
  }

  @Override
  public void reset() {
    loadState(unitLocHistory.get(0), unitDirecHistory.get(0));
    unitLocHistory.clear();
    unitDirecHistory.clear();
    moves = 0;
    recordState();
  }

  @Override
  public void undo() {
    if (unitLocHistory.size() > 1) {
      unitLocHistory.remove(unitLocHistory.size() - 1);
      unitDirecHistory.remove(unitDirecHistory.size() - 1);
      moves--;
      loadState(
              unitLocHistory.get(unitLocHistory.size() - 1),
              unitDirecHistory.get(unitDirecHistory.size() - 1));
    }
  }

  @Override
  public int getWidth() {
    return unitGrid.length;
  }

  @Override
  public int getHeight() {
    return unitGrid[0].length;
  }

  private Unit[][] autoMoveUnitsFlowNet() {
    Unit[][] oldGrid = new Unit[unitGrid.length][unitGrid[0].length];
    Unit[][] newGrid = new Unit[unitGrid.length][unitGrid[0].length];

    List<Unit> unitsOnBoard = new ArrayList<>();
    List<Unit> toBePlaced = new ArrayList<>();

    Map<Unit, List<Direction>> movingUnitsChoices = new HashMap<>();
    Map<Unit, Direction> stayerFirstChoices = new HashMap<>();

    // the maxFlow should equal this value
    int unitsToMove = 0;

    // VERTICES:
    //   "source" -> String equals
    //   Units -> equals will be literal same Object
    //   Points -> correspond to where in the array the unit can move,
    //             equals checks for same x, y
    //   "sink" -> String equals
    Graph<Object, DefaultEdge> placementGraph =
            new SimpleDirectedWeightedGraph<Object, DefaultEdge>(DefaultEdge.class);
    placementGraph.addVertex("source");
    placementGraph.addVertex("sink");
    for (int x = 0; x < unitGrid.length; x++) {
      for (int y = 0; y < unitGrid[x].length; y++) {
        Point p = new Point(x, y);
        placementGraph.addVertex(p);
        placementGraph.addEdge(p, "sink"); //DEFAULT_EDGE_WEIGHT is already 1.0

        if (unitGrid[x][y] != null) {
          unitsOnBoard.add(unitGrid[x][y]);
        }
      }
    }

    //set up graph for units to be moved
    // also stores lists of possible directions (in preferred order) for each moving unit in the map
    for (Unit unit : unitsOnBoard) {
      if (unit.movesAutomatically()) {
        toBePlaced.add(unit);
        placeUnit(unit,oldGrid);
        List<Direction> directionChoices = new ArrayList<>(
                VectorDirectionComputer.vectorToDirectionsFavoritesOnly(
                unit.desiredDirection(unitGrid, shep)));
        movingUnitsChoices.put(unit, directionChoices); //should store the ref
        stayerFirstChoices.put(unit, directionChoices.get(0));

        placementGraph.addVertex(unit);
        placementGraph.addEdge("source", unit);

        int fromX = unit.getLocation()[0];
        int fromY = unit.getLocation()[1];
        for (int i = 0; i < directionChoices.size(); i++) {
          int toX = fromX;
          int toY = fromY;
          switch (directionChoices.get(i)) {
            case DOWN: toY++; break;
            case UP: toY--; break;
            case LEFT: toX--; break;
            case RIGHT: toX++; break;
            case STAY: break;
          }
          if (toX < 0 || toY < 0 || toX >= unitGrid.length || toY >= unitGrid[toX].length) {
            //directionChoices.remove(i);
            //i--; //dont check this direction again if it leads out of bounds /////////////////////marginal af
          } else {
            placementGraph.addEdge(unit, new Point(toX, toY));
          }
        }
      }
    }

    // PAST THIS POINT, ALL EDGES, NODES ARE IN (now we can remove freely)

    for (Unit unit : unitsOnBoard) {
      if (!unit.movesAutomatically()) {
        placeUnit(unit, newGrid);
        placementGraph.removeVertex(new Point(unit.getLocation()[0], unit.getLocation()[1]));
        // ^ no unit can move to this space, it's already taken.
      } else {
        //check if directionChoices.size() == 1, just move it right away
        // this can only mean the unit is staying (its other direction choices lead it out of bounds)
        if (movingUnitsChoices.get(unit).size() == 1) {
          int fromX = unit.getLocation()[0];
          int fromY = unit.getLocation()[1];
          placementGraph.removeVertex(unit); //remove unit node
          placementGraph.removeVertex(new Point(fromX, fromY)); //remove node for point unit goes to
          oldGrid[fromX][fromY] = null; //remove from old grid
          placeUnit(unit, newGrid); // put in new grid
          toBePlaced.remove(unit);// one less unit to place now
        }
      }
    }

    unitsToMove = toBePlaced.size();

    DinicMFImpl<Object, DefaultEdge> dinic = new DinicMFImpl<>(placementGraph);
    MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> maxFlow = dinic.getMaximumFlow("source", "sink");
    // computed when needed in checkmove function, and returned by that function so it can be
    //   referenced by it in the future.

    // make new checkmove function that uses network flow calculation + maxFlow
    //  can check if the flow on the direction of choice is 1
    //    if yes, we know its legit
    //      should speed up some parts (speeds up single option moves)

    List<int[]> coordsToHit = coordsToHit(oldGrid);
    for (int i = 0; i < coordsToHit.size(); i++) {
      int x = coordsToHit.get(i)[0];
      int y = coordsToHit.get(i)[1];
        Unit unit = oldGrid[x][y];
        if (unit != null) {
          boolean foundGoodPlace = false;
          while (!foundGoodPlace) {
            Direction toMove = Direction.STAY;
            try {
              toMove = movingUnitsChoices.get(unit).remove(0);
            } catch (IndexOutOfBoundsException e) {
              System.out.println(unit.getName());
              System.out.println(unit.getLocation()[0] + " " + unit.getLocation()[1]);
              throw e;
            }
            MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> newFlow =
                    checkMoveFromFlowNet(dinic, maxFlow, placementGraph, unitsToMove, unit, toMove);
            if (newFlow != null) {
              foundGoodPlace = true;
              maxFlow = newFlow;
              unit.move(toMove, new Unit[]{null, null, null, null});
              placeUnit(unit, newGrid);
              placementGraph.removeVertex(unit);
              switch (toMove) {
                case DOWN: placementGraph.removeVertex(new Point(x, y+1)); break;
                case UP: placementGraph.removeVertex(new Point(x, y-1)); break;
                case LEFT: placementGraph.removeVertex(new Point(x-1, y)); break;
                case RIGHT: placementGraph.removeVertex(new Point(x+1, y)); break;
                case STAY: placementGraph.removeVertex(new Point(x, y)); break;
              }
              unitsToMove--;
            }
          }
        }
    }

    //set directions of stayers \/
    for (Unit unit : unitsOnBoard) {
      if (unit.getDirection() == Direction.STAY) {
        unit.setDirection(stayerFirstChoices.get(unit));
      }
    }

    return newGrid;
  }

  //return null if fail
  //return updated maximum flow if move was legal
  // mfAlg must be pointing to graph
  private MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> checkMoveFromFlowNet(
          MaximumFlowAlgorithm<Object, DefaultEdge> mfAlg,
          MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> prevFlow,
          Graph<Object, DefaultEdge> graph,
          int unitsToMove,
          Unit unit,
          Direction dir
  ) {
    int x = unit.getLocation()[0];
    int y = unit.getLocation()[1];

    switch (dir) {
      case DOWN: y++; break;
      case UP: y--; break;
      case LEFT: x--; break;
      case RIGHT: x++; break;
      case STAY: break;
    }

    if (!graph.containsEdge(unit, new Point(x, y))) {
      return null;
    }

    MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> newFlow = null;

    if(Math.round((float) prevFlow.getFlow(graph.getEdge(unit, new Point(x, y)))) == 1) {
      // success
      return prevFlow;
    } else {
      for (DefaultEdge e : graph.outgoingEdgesOf(unit)) {
        graph.setEdgeWeight(e, 0.0);
      }
      graph.setEdgeWeight(unit, new Point(x, y), 1.0);
      newFlow = mfAlg.getMaximumFlow("source", "sink");
      if (Math.round((float) newFlow.getValue().doubleValue()) == unitsToMove) {
        // success
      } else {
        // failure
        for (DefaultEdge e : graph.outgoingEdgesOf(unit)) {
          graph.setEdgeWeight(e, 1.0); //reset graph
        }
        newFlow = null;
      }
    }

    return newFlow;
  }

  private void recordState() {
    Unit[][] locs = new Unit[unitGrid.length][unitGrid[0].length];
    Direction[][] direcs = new Direction[unitGrid.length][unitGrid[0].length];

    for (int x = 0; x < unitGrid.length; x++) {
      for (int y = 0; y < unitGrid[x].length; y++) {
        if (unitGrid[x][y] != null) {
          locs[x][y] = unitGrid[x][y];
          direcs[x][y] = unitGrid[x][y].getDirection();
        }
      }
    }

    unitLocHistory.add(locs);
    unitDirecHistory.add(direcs);
  }

  //seems to work now
  private void loadState(Unit[][] unitLocs, Direction[][] unitDirections) {
    Unit[][] newGrid = new Unit[unitLocs.length][unitLocs[0].length];
    for (int x = 0; x < unitLocs.length; x++) {
      for (int y = 0; y < unitLocs[x].length; y++) {
        if (unitLocs[x][y] != null) {
          newGrid[x][y] = unitLocs[x][y];
          newGrid[x][y].setDirection(unitDirections[x][y]);
          newGrid[x][y].setLocation(x, y);
        }
      }
    }
    unitGrid = newGrid;
  }

  //coords will all be within bounds
  private List<int[]> coordsToHit(Unit[][] oldGrid) {
    List<int[]> coords = new ArrayList<>();
    for (int x = 0; x < unitGrid.length; x++) {
      for (int y = 0; y < unitGrid[x].length; y++) {
        if (oldGrid[x][y] != null) {
          coords.add(new int[]{x, y});
        }
      }
    }

    int[][] coordsArr = new int[coords.size()][2];
    coordsArr = coords.toArray(coordsArr);

    Arrays.sort(coordsArr, new ClosestToShep(shep));

    return Arrays.asList(coordsArr);
  }

  private class ClosestToShep implements Comparator<int[]> {

    private double x;
    private double y;
    private double xFactor;
    private double yFactor;

    ClosestToShep(Unit shep) {
      x = shep.getLocation()[0];
      y = shep.getLocation()[1];
      xFactor = 0.0;
      yFactor = 0.0;
      // shep is left-handed (sheep closer to crook move first)
//      1st tiebreak: manhattan distance
//      2nd tiebreak: most in direction shep is facing
//      3rd tiebreak: closest to crook (should only break tie btw 2 spots)
      switch (shep.getDirection()) {
        case LEFT:
          xFactor = 0.001;
          y += 0.0001;
          break;
        case RIGHT:
          xFactor = -0.001;
          y -= 0.0001;
          break;
        case UP:
          yFactor = 0.001;
          x -= 0.0001;
          break;
        case DOWN:
          yFactor = -0.001;
          x += 0.0001;
          break;
        default: throw new RuntimeException("didn't know shep could be stay");
      }
    }

    //o1 < o2 : negative
    //o1 > o2 : positive
    //o1 = o2 : equal (try to avoid this)
    @Override
    public int compare(int[] o1, int[] o2) {
      double diff = value(o1) - value(o2); //if o1 is closer, diff is negative
      if (diff == 0.0) {
        throw new RuntimeException("found same distance from points " +
                o1[0] + "," + o1[1] + " and " + o2[0] + "," + o2[1] + " from shep location " +
                x + "," + y);
      }
      return Double.compare(diff, 0.0);
    }

    //manhattan distance from crook location * factors
    public double value(int[] co) {
      return Math.abs(co[0] - x) + Math.abs(co[1] - y) +
              (co[0] - x) * xFactor + (co[1] - y) * yFactor;
    }
  }

  private void placeUnit(Unit unit, Unit[][] field) {
    field[unit.getLocation()[0]][unit.getLocation()[1]] = unit;
  }

  //will have some nulls
  private Unit[] grabNeighborsNESW(int x, int y) {
    Unit[] neighbors = new Unit[4];

    if (y > -0) {
      neighbors[0] = unitGrid[x][y - 1]; //n
    } else {
      neighbors[0] = new Rock(x, y - 1);
    }
    if (x < unitGrid.length - 1) {
      neighbors[1] = unitGrid[x + 1][y]; //e
    } else {
      neighbors[1] = new Rock(x + 1, y);
    }
    if (y < unitGrid[x].length - 1) {
      neighbors[2] = unitGrid[x][y + 1]; //s
    } else {
      neighbors[2] = new Rock(x, y + 1);
    }
    if (x > 0) {
      neighbors[3] = unitGrid[x - 1][y]; //w
    } else {
      neighbors[3] = new Rock(x - 1, y);
    }

    return neighbors;
  }

}
