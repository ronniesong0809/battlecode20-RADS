package team4player;
import battlecode.common.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Drone extends Unit {
    static int currState = 0;
    static List<MapLocation> enemyHQlocs = null;
    static List<MapLocation> droneCircle = null;
    static int enemyLocationToCheck = 0;
    static boolean foundEnemyHQ = false;
    static MapLocation enemyHQ = null;
    static boolean initialized = false;

    public Drone(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (!initialized && enemyHQ != null) {
            initializeCircle();
            initialized = true;
        }

        Team enemy = rc.getTeam().opponent();
//        if (!rc.isCurrentlyHoldingUnit()) {
//            // See if there are any enemy robots within capturing range
//            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);
//
//            if (robots.length > 0) {
//                // Pick up a first robot within range
//                rc.pickUpUnit(robots[0].getID());
//                System.out.println("I picked up " + robots[0].getID() + "!");
//            }
//        } else {
        // No close robots, so search for robots within sight radius
//            tryMove(randomDirection());
//        }//TODO:

        // Here is where actions take place
        switch (currState) {
            case 0:
                // Go all the way north
                if (!go(Direction.NORTH)) {
                    currState++;
                }
                break;
            case 1:
                // Go all the way east
                if (!go(Direction.EAST)) {
                    currState++;
                }
                break;
            case 2:
                // Build array of possible enemy HQ locations
                composeEnemyHQLocations();
                currState++;
                break;
            case 3:
                // Find and go to enemy HQ
                boolean stillMoving = goToHQLocations(enemyLocationToCheck);

                if (!stillMoving) {
                    if (foundEnemyHQ) {
                        currState++;
                    } else {
                        enemyLocationToCheck++;
                    }
                }

                break;
            case 4:
                // Circle enemy HQ
                circleHQandPickUp();
                break;
        }
    }

    public boolean go(Direction dir) throws GameActionException {
        if (!rc.canSenseLocation(rc.getLocation().add(dir))) {
            return false;
        }
        nav.droneMove(dir);
        return true;
    }

    public void composeEnemyHQLocations() {
        enemyHQlocs = new ArrayList<MapLocation>();
        MapLocation topCorner = rc.getLocation();
        enemyHQlocs.add(new MapLocation(topCorner.x - hqLoc.x, topCorner.y - hqLoc.y));
        enemyHQlocs.add(new MapLocation(topCorner.x - hqLoc.x, hqLoc.y));
        enemyHQlocs.add(new MapLocation(hqLoc.x, topCorner.y - hqLoc.y));
    }

    public boolean goToHQLocations(int toCheck) throws GameActionException {

        // Go towards the location
        // Stop if you are < 25 distance squared.
        // Check if enemy HQ is there and return true if so.
        MapLocation enemyHQlocation = enemyHQlocs.get(toCheck);
        if (rc.getLocation().distanceSquaredTo(enemyHQlocation) > 20) {
            nav.goAround(enemyHQlocation);
            return true;
        } else {
            // Check if it is there
            RobotInfo hq = rc.senseRobotAtLocation(enemyHQlocation);
            if (hq.getType() == RobotType.HQ && hq.getTeam() == rc.getTeam().opponent()) {
                foundEnemyHQ = true;
                enemyHQ = enemyHQlocation;
            }
            return false;
        }
    }

    public boolean circleHQandPickUp() throws GameActionException {
        // Try to move in the direction of the enemy HQ
        // Cant so try moving left.

        // Find closest point on circle
        int closestSpot = closestCircleSpot();
        if (rc.getLocation().distanceSquaredTo(droneCircle.get(closestSpot)) == 0) {
            // We are on the circle so now go to the next spot
            int nextSpot = closestSpot+1;
            if (closestSpot == 23) {
                nextSpot = 0;
            }
            nav.droneMove(rc.getLocation().directionTo(droneCircle.get(nextSpot)));
        }
        else {
            // Go to the closest spot
            nav.droneMove(rc.getLocation().directionTo(droneCircle.get(closestSpot)));
        }

        return true;
    }

    public void initializeCircle() {

        droneCircle = new ArrayList<MapLocation>();

        int block_x = enemyHQ.x - 2;
        int block_y = enemyHQ.y + 4;
        for (int i = 0; i < 24; i++) {
            droneCircle.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0: case 1: case 2: case 3:
                    block_x++;
                    break;
                case 4:
                case 5:
                    block_y--;
                    block_x++;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    block_y--;
                    break;
                case 10:
                case 11:
                    block_y--;
                    block_x--;
                    break;
                case 12:
                case 13:
                case 14:
                case 15:
                    block_x--;
                    break;
                case 16:
                case 17:
                    block_y++;
                    block_x--;
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                    block_y++;
                    break;
                case 22:
                case 23:
                    block_y++;
                    block_x++;
                    break;
            }
        }
    }

    public int closestCircleSpot() {
        // Iterate through circle
        int currSmallest = 9999999;
        int toReturn = 0;
        for (int i = 0; i < droneCircle.size(); i++) {
            int distance = rc.getLocation().distanceSquaredTo(droneCircle.get(i));
            if (distance < currSmallest) {
                currSmallest = distance;
                toReturn = i;
            }
        }
        return toReturn;
    }
}
