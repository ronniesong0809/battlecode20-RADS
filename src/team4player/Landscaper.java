package team4player;
import battlecode.common.*;
import java.util.*;

public class Landscaper extends Unit{
    static List<MapLocation> wallLocs = null;
    static List<Integer> wallLevels = null;
    static int digX = 0;
    static int digY = 0;
    static int minWallHeight = 10;
    static int maxWallHeight = 100;
    static boolean initialWallComplete = false;
    static float chanceNewWall = 0.005f;
    static int round = 0;
    static MapLocation wallSpot = null;

    public Landscaper(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        round++;

        if (wallLocs == null) {
            initializeWallLocationsAndLevels();
        }

        // Figure out digging pattern:
        if (hqLoc.x % 2 == 0) {
            digX = 1;
        }
        if (hqLoc.y % 2 == 0) {
            digY = 1;
        }

        int currWallSpot = 0;
        // Find next spot to dig to

        for (MapLocation tileToCheck : wallLocs) {
            currWallSpot++;
            if (rc.getLocation().distanceSquaredTo(tileToCheck) < 4 && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))) {
                if (rc.senseElevation(tileToCheck) < minWallHeight) {
                    wallSpot = tileToCheck;
                    break;
                }
            }
        }

        // Gather dirt from appropriate spots
        if (rc.getDirtCarrying() == 0) {
            tryDig();
        }

        // Add more spots to deposit dirt to in lattice fashion.

        if (wallSpot != null && rc.canDepositDirt(rc.getLocation().directionTo(wallSpot))) {
            if (rc.senseElevation(wallSpot) <= minWallHeight) {
                rc.depositDirt(rc.getLocation().directionTo(wallSpot));
            }

            int newWallHeight = bc.readInitialWallComplete();
            if (newWallHeight > minWallHeight) {
                minWallHeight = newWallHeight;
            }
            if (initialWallComplete || bc.readInitialWallComplete() == maxWallHeight) {
                initialWallComplete = true;
                System.out.println("Initial wall complete! ******************");
                if (rc.senseElevation(wallSpot) >= maxWallHeight) {
                    boolean shouldStop = false;
                    for (int i = 0; i < 8; i++) {
                        MapLocation newLoc = wallSpot.add(Direction.values()[i]);
                        if (!wallLocs.contains(newLoc) &&
                                !(newLoc.x % 2 == digX && newLoc.y % 2 == digY) &&
                                newLoc.distanceSquaredTo(hqLoc) > 2 &&
                                rc.senseElevation(newLoc) <= minWallHeight) {
                            wallLocs.add(wallSpot.add(Direction.values()[i]));
                            shouldStop = true;
                        }
                    }
                    wallLocs.remove(wallSpot);
                    if (shouldStop) {
                        wallSpot = null;
                    }
                }
            }
        }

        // Either go to wall spot or go in a random direction.
        if (wallSpot != null) {
            nav.goTo(wallSpot);
        } else if (rc.getLocation().distanceSquaredTo(hqLoc) > 30) {
            nav.goTo(hqLoc);
        }
        else {
            nav.goTo(Util.randomDirection());
        }
    }

    boolean tryDig() throws GameActionException {
        Direction dir = null;
        for (Direction d : Util.directions) {
            MapLocation digSpot = rc.getLocation().add(d);
            if ((digSpot.x % 2 == digX && digSpot.y % 2 == digY) && digSpot.distanceSquaredTo(hqLoc) > 2) {
                dir = rc.getLocation().directionTo(digSpot);
                break;
            }
        }


        if (dir != null && rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    public void initializeWallLocationsAndLevels() {
        wallLocs = new ArrayList<MapLocation>();
        wallLevels = new ArrayList<Integer>();

        int block_x = hqLoc.x - 2;
        int block_y = hqLoc.y + 2;
        for (int i = 0; i < 16; i++) {
            wallLocs.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3:
                    block_x++;
                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                    block_y--;
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    block_x--;
                    break;
                case 12:
                case 13:
                case 14:
                case 15:
                    block_y++;
                    break;
            }
        }

        for (int i = 0; i < wallLocs.size(); i++) {
            wallLevels.add(3);
        }
    }
}
