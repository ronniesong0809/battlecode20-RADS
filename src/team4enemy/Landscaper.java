package team4enemy;

import battlecode.common.*;

import java.util.*;

public class Landscaper extends Unit {
    static List<MapLocation> wallLocs = null;
    static List<Integer> wallLevels = null;
    static int digX = 0;
    static int digY = 0;
    static int minWallHeight = 10;
    static MapLocation wallSpot = null;
    static int personality = 0;
    static boolean initialized = false;

    public Landscaper(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (hqLoc == null) {
            findHQ();
        }

        // Initialize the map locations to build on
        if (wallLocs == null && hqLoc != null) {
            initializeWallLocationsAndLevels();
        }

        if (rc.getLocation().distanceSquaredTo(hqLoc) >= 10) {
            nav.goAround(hqLoc);
        } else {
            // Find wall spot

            if (wallSpot == null && initialized) {
                wallSpot = findWallSpot();
            } else {
                RobotInfo otherRobot = rc.senseRobotAtLocation(wallSpot);
                if (otherRobot != null && rc.getID() != otherRobot.getID()) {
                    wallSpot = null;
                } else {
                    // Otherwise deposit dirt if you are close enough
                    if (rc.getLocation().distanceSquaredTo(wallSpot) >= 1) {
                        nav.goAround(wallSpot);
                    } else {
                        // Try to dig dirt if you are carrying none
                        if (rc.getDirtCarrying() == 0) {
                            tryDig();
                        } else {
                            tryDeposit(wallSpot);
                        }
                    }
                }
            }
        }
    }


    public void runHqWallBuilding() {

    }

    boolean tryDig() throws GameActionException {
        Direction dir = null;
        for (Direction d : Util.directions) {
            MapLocation digSpot = rc.getLocation().add(d);
            if (digSpot.distanceSquaredTo(hqLoc) > 2) {
                dir = rc.getLocation().directionTo(digSpot);
                if (rc.canDigDirt(dir)) {
                    break;
                }
            }
        }

        if (dir != null && rc.canDigDirt(dir) && rc.senseRobotAtLocation(rc.getLocation().add(dir)) == null) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    public void tryDeposit(MapLocation spot) throws GameActionException {
        if (spot != null && rc.canDepositDirt(rc.getLocation().directionTo(spot))) {
            rc.depositDirt(rc.getLocation().directionTo(spot));
        }
    }

    public void initializeWallLocationsAndLevels() {
        // Figure out digging pattern:
        if (hqLoc.x % 2 == 0) {
            digX = 1;
        }
        if (hqLoc.y % 2 == 0) {
            digY = 1;
        }

        wallLocs = new ArrayList<MapLocation>();
        wallLevels = new ArrayList<Integer>();

        int block_x = hqLoc.x - 1;
        int block_y = hqLoc.y + 1;
        for (int i = 0; i < 8; i++) {
            wallLocs.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0:
                case 1:
                    block_x++;
                    break;
                case 2:
                case 3:
                    block_y--;
                    break;
                case 4:
                case 5:
                    block_x--;
                    break;
                case 6:
                case 7:
                    block_y++;
                    break;
            }
        }

        initialized = true;

    }

    public void senseWallHeights() {
        for (int i = 0; i < wallLocs.size(); i++) {
            wallLevels.add(3);
        }
    }

    public MapLocation findWallSpot() throws GameActionException {
        for (MapLocation tileToCheck : wallLocs) {
            // Check if another landscaper is on that tile.
            RobotInfo otherRobot = rc.senseRobotAtLocation(tileToCheck);
            if (otherRobot == null) {
                return tileToCheck;
            }
        }
        return null;
    }

//    public void digLattice() {
//        if (wallSpot != null && rc.canDepositDirt(rc.getLocation().directionTo(wallSpot))) {
//            if (rc.senseElevation(wallSpot) <= minWallHeight) {
//                rc.depositDirt(rc.getLocation().directionTo(wallSpot));
//            }
//            int newWallHeight = bc.readInitialWallComplete();
//            if (newWallHeight > minWallHeight) {
//                minWallHeight = newWallHeight;
//            }
//            if (initialWallComplete || bc.readInitialWallComplete() == maxWallHeight) {
//                initialWallComplete = true;
//                System.out.println("Initial wall complete! ******************");
//                if (rc.senseElevation(wallSpot) >= maxWallHeight) {
//                    boolean shouldStop = false;
//                    for (int i = 0; i < 8; i++) {
//                        MapLocation newLoc = wallSpot.add(Direction.values()[i]);
//                        if (!wallLocs.contains(newLoc) &&
//                                !(newLoc.x % 2 == digX && newLoc.y % 2 == digY) &&
//                                newLoc.distanceSquaredTo(hqLoc) > 2 &&
//                                rc.senseElevation(newLoc) <= minWallHeight) {
//                            wallLocs.add(wallSpot.add(Direction.values()[i]));
//                            shouldStop = true;
//                        }
//                    }
//                    wallLocs.remove(wallSpot);
//                    if (shouldStop) {
//                        wallSpot = null;
//                    }
//                }
//            }
//        }
//    }
}
