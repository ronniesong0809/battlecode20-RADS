package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

public class Drone extends Unit {
    static int currState = 0;
    static List<MapLocation> droneCircle = null;
    static int enemyLocationToCheck = 0;
    static boolean foundEnemyHQ = false;
    static MapLocation enemyHQ = null;
    static boolean initialized = false;
    static boolean haveCow = false;

    public Drone(RobotController rc) {
        super(rc);
    }
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (!initialized && enemyHQ != null) {
            initializeCircle();
            initialized = true;
        }

        // Here is where actions take place
        switch (currState) {
            case 0:
                // Build array of possible enemy HQ locations
                findHQ();
                findEnemyHQ();
                if (enemyHQlocs != null) {
                    currState++;
                }
                break;
            case 1:
                // Find and go to enemy HQ
                if (!haveCow && isCowAround()) {
                    //Go find cow and pick it up
                    currState = 3;
                    break;
                }
                boolean stillMoving = goToHQLocations(enemyLocationToCheck);
                if (!stillMoving) {
                    if (foundEnemyHQ) {
                        currState++;
                    } else {
                        enemyLocationToCheck++;
                    }
                }
                break;
            case 2:
                // Drop cow
                break;
            case 3:
                // Find cow and pick it up.
                pickUpCow();

                // if we have cow
                if (haveCow) {
                    currState = 1;
                }
                break;
            default:
                break;
        }
    }

    public boolean isCowAround() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared());
        for (RobotInfo e : robots) {
            if (e.type == RobotType.COW) {
                //Found cow
                return true;
            }
        }
        return false;
    }

    public boolean pickUpCow() throws GameActionException {
    //find cow
        MapLocation cowloc=null;
        int cowid=-1;
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared());
        for (RobotInfo e : robots) {
            if (e.type == RobotType.COW) {
                cowid=e.getID();
                cowloc = new MapLocation(e.getLocation().x, e.getLocation().y);
                break;
            }
        }

        if(cowid>=0) {
            if (rc.getLocation().distanceSquaredTo(cowloc) <= 2) {
                //pick up cow
                if (rc.canPickUpUnit(cowid)) {
                    rc.pickUpUnit(cowid);
                    haveCow = true;
                    return true;
                }
            } else {
                nav.goAround(cowloc);
                return false;
            }
        }
        return false;

    }
    public boolean go(Direction dir) throws GameActionException {
        if (!rc.canSenseLocation(rc.getLocation().add(dir))) {
            return false;
        }
        nav.droneMove(dir);
        return true;
    }


    public boolean goToHQLocations(int toCheck) throws GameActionException {
        // Go towards the location
        // Stop if you are < 25 distance squared.
        // Check if enemy HQ is there and return true if so.
        MapLocation enemyHQlocation = enemyHQlocs.get(toCheck);
        if (rc.getLocation().distanceSquaredTo(enemyHQlocation) > 15) {
            nav.droneMove(rc.getLocation().directionTo(enemyHQlocation));
            return true;
        } else {
            // Check if it is there
            RobotInfo hq = rc.senseRobotAtLocation(enemyHQlocation);
            if (hq != null && hq.getType() == RobotType.HQ && hq.getTeam() == rc.getTeam().opponent()) {
                foundEnemyHQ = true;
                enemyHQ = enemyHQlocation;
                System.out.println("Found enemy");
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
            int nextSpot = closestSpot + 1;
            if (closestSpot == 23) {
                nextSpot = 0;
            }
            nav.droneMove(rc.getLocation().directionTo(droneCircle.get(nextSpot)));
        } else {
            // Go to the closest spot
            nav.droneMove(rc.getLocation().directionTo(droneCircle.get(closestSpot)));
        }

        return true;
    }

    //public ArrayList<MapLocation> createDroneCircle(){
    public boolean createDroneCircle(){
        droneCircle = new ArrayList<MapLocation>();
        return true;
        //return new ArrayList<MapLocation>();
    }
    public int returnXY(int xy){
        if (xy == 2){
            return enemyHQ.x -2;
        } else {
            return enemyHQ.y + 4;
        }
    }

    public void initializeCircle() {
        createDroneCircle();
        int block_x = returnXY(2);
        int block_y = returnXY(4);
        for (int i = 0; i < 24; i++) {
            droneCircle.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3:
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
                default:
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


