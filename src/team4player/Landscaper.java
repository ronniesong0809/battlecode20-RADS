package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

public class Landscaper extends Unit {
    Unit unit = new Unit(rc);
    static List<MapLocation> wallLocs = null;
    static List<MapLocation> digSpots = null;
    static List<Integer> wallLevels = null;
    static int digX = 0;
    static int digY = 0;
    static int minWallHeight = 10;
    static MapLocation wallSpot = null;
    static int personality = 0;
    static boolean initialized = false;
    static boolean dig_initialized = false;
    static int roundNum = 0;
    static int outsideSpot = 0;
    static int hqElevation = -1;

    public Landscaper(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        roundNum++;

        if (hqLoc == null) {
            findHQ();
        }

        if (hqLoc != null && hqElevation < 0 && rc.getCurrentSensorRadiusSquared() >= rc.getLocation().distanceSquaredTo(hqLoc)) {
            hqElevation = rc.senseElevation(hqLoc);
        }

        // Initialize the map locations to build on
        if (wallLocs == null && hqLoc != null) {
            initializeWallLocationsAndLevels();
            createDigLocations();
        }

        if (hqLoc != null && rc.getLocation().distanceSquaredTo(hqLoc) >= 10) {
            nav.goAround(hqLoc);
        } else {
            // Find wall spot

            if (wallSpot == null && initialized) {
                wallSpot = findWallSpot();
            } else {

                RobotInfo otherRobot = null;
                if (rc.getCurrentSensorRadiusSquared() >= rc.getLocation().distanceSquaredTo(wallSpot)) {
                    otherRobot = rc.senseRobotAtLocation(wallSpot);
                }
                if (otherRobot != null && rc.getID() != otherRobot.getID()) {
                    wallSpot = null;
                } else {
                    // Otherwise deposit dirt if you are close enough
                    if (wallSpot != null && rc.getLocation().distanceSquaredTo(wallSpot) > 0) {
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

    boolean tryDig() throws GameActionException {

        Direction dig = findDigSpot();
        if (dig != null && rc.canDigDirt(dig)) {
            rc.digDirt(dig);
            return true;
        }
        return false;
    }

    Direction findDigSpot() throws GameActionException{
        Direction dir = null;

        if (wallLocs.indexOf(wallSpot) < 8 && rc.getLocation().distanceSquaredTo(hqLoc) < 3) {
            if (rc.senseElevation(hqLoc) > hqElevation) {
                return rc.getLocation().directionTo(hqLoc);
            }
        }

        // Use new dig spot locations list to find the closest possible dig spot
        for (MapLocation d : digSpots) {
            dir = rc.getLocation().directionTo(d);
            if (rc.getLocation().distanceSquaredTo(d) <= 2) {
                if (rc.canDigDirt(dir)) {
                    return dir;
                }
            }
        }
        return null;
    }

    public void tryDeposit(MapLocation spot) throws GameActionException {

        if (spot.distanceSquaredTo(hqLoc) > 3) {
            if (spot.x < hqLoc.x && spot.y > hqLoc.y) {
                spot = spot.add(Direction.SOUTHEAST);
            } else if (spot.x < hqLoc.x && spot.y < hqLoc.y) {
                spot = spot.add(Direction.NORTHEAST);
            } else if (spot.x > hqLoc.x && spot.y < hqLoc.y) {
                spot = spot.add(Direction.NORTHWEST);
            } else if (spot.x > hqLoc.x && spot.y > hqLoc.y) {
                spot = spot.add(Direction.SOUTHWEST);
            }
        }

        int wallspot = wallLocs.indexOf(spot);
        if (wallspot < 8 && roundNum > 250) {
            // Check surrounding wall spots to see if they are lower for deposit.

            int leftIndex = wallspot+1;
            int rightIndex = wallspot-1;

            if (wallspot == 7) {
                // check location 6 and 0
                // 50% chance to check either 6 or 0
                leftIndex = 0;
                rightIndex = 6;
            }
            else if (wallspot == 0) {
                // check locations 1 and 7
                leftIndex = 1;
                rightIndex = 7;
            }

            int elevation_left = rc.senseElevation(wallLocs.get(leftIndex));
            int elevation_right = rc.senseElevation(wallLocs.get(rightIndex));

            if (elevation_left < elevation_right) {
                if (rc.senseElevation(wallLocs.get(leftIndex)) < rc.senseElevation(rc.getLocation())) {
                    spot = wallLocs.get(leftIndex);
                }
            }
            else {
                if (rc.senseElevation(wallLocs.get(rightIndex)) < rc.senseElevation(rc.getLocation())) {
                    spot = wallLocs.get(rightIndex);
                }
            }


        }

        if (rc.canDepositDirt(rc.getLocation().directionTo(spot))) {
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

        // Adding locations around the HQ
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
                default:
                    break;
            }
        }

        block_x = hqLoc.x - 2;
        block_y = hqLoc.y + 2;
        for (int i = 0; i < 12; i++) {
            wallLocs.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0:
                    block_x++;
                    break;
                case 1:
                    block_x+=2;
                    break;
                case 2:
                    block_x++;
                    break;
                case 3:
                    block_y--;
                    break;
                case 4:
                    block_y-=2;
                    break;
                case 5:
                    block_y--;
                    break;
                case 6:
                    block_x--;
                    break;
                case 7:
                    block_x-=2;
                    break;
                case 8:
                    block_x--;
                    break;
                case 9:
                    block_y++;
                    break;
                case 10:
                    block_y+=2;
                    break;
                case 11:
                    block_y++;
                    break;
                default:
                    break;
            }
        }

        // Adding external HQ locations

        initialized = true;

    }

    public void createDigLocations() {

        digSpots = new ArrayList<MapLocation>();

        // Adding locations around the HQ
        int block_x = hqLoc.x - 3;
        int block_y = hqLoc.y + 3;
        for (int i = 0; i < 24; i++) {
            digSpots.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0:
                case 1:
                    block_x++;
                    break;
                case 2:
                    block_x++;
                    block_y--;
                    break;
                case 3:
                    block_y++;
                    block_x++;
                    break;
                case 4:
                case 5:
                    block_x++;
                    break;
                case 6:
                case 7:
                    block_y--;
                    break;
                case 8:
                    block_y--;
                    block_x--;
                    break;
                case 9:
                    block_x++;
                    block_y--;
                    break;
                case 10:
                case 11:
                    block_y--;
                    break;
                case 12:
                case 13:
                    block_x--;
                    break;
                case 14:
                    block_x--;
                    block_y++;
                    break;
                case 15:
                    block_x--;
                    block_y--;
                    break;
                case 16:
                case 17:
                    block_x--;
                    break;
                case 18:
                case 19:
                    block_y++;
                    break;
                case 20:
                    block_x++;
                    block_y++;
                    break;
                case 21:
                    block_x--;
                    block_y++;
                    break;
                case 22:
                case 23:
                    block_y++;
                    break;
                default:
                    break;
            }
        }

        // Adding external HQ locations

        dig_initialized = true;

    }

    public void senseWallHeights() {
        for (int i = 0; i < wallLocs.size(); i++) {
            wallLevels.add(3);
        }
    }

    public MapLocation findWallSpot() throws GameActionException {

        for (MapLocation tileToCheck : wallLocs) {
            // Check if another landscaper is on that tile.
            if (rc.getLocation().distanceSquaredTo(tileToCheck) <= rc.getCurrentSensorRadiusSquared()) {
                RobotInfo otherRobot = rc.senseRobotAtLocation(tileToCheck);
                if (otherRobot == null) {
                    return tileToCheck;
                }
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
