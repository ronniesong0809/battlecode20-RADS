package team4player;
import battlecode.common.*;
import java.lang.Math;
import java.util.ArrayList;

public strictfp class RobotPlayer {
    static RobotController rc;

    static int turnCount;
    static MapLocation hqLoc;
    static int numMiners = 0;
    static int numDesignSchool = 0;
    static int numRefinery = 0;
    static int numLandscaper = 0;
    static ArrayList<MapLocation> soupLocation = new ArrayList<MapLocation>();

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static void run(RobotController rc) throws GameActionException {
        Robot me = null;

        switch (rc.getType()) {
            case HQ:
                me = new Building(rc);
                break;
            case MINER:
                me = new Miner(rc);
                break;
            case REFINERY:
                me = new Building(rc);
                break;
            case VAPORATOR:
                me = new Building(rc);
                break;
            case DESIGN_SCHOOL:
                me = new Building(rc);
                break;
            case FULFILLMENT_CENTER:
                me = new Building(rc);
                break;
            case LANDSCAPER:
                me = new Unit(rc);
                break;
            case DELIVERY_DRONE:
                me = new Unit(rc);
                break;
            case NET_GUN:
                me = new Building(rc);
                break;
        }

        while (true) {
            try {
                me.takeTurn();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }



    static MapLocation findRefinery() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam()) {
                return robot.location;
            }
        }
        return null;
    }



    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
        if (numRefinery < 3) {
            for (Direction dir : Util.directions)
                if (tryBuild(RobotType.REFINERY, dir)) {
                    System.out.println("build a refinery");
                    System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
                    numRefinery++;
                }
        }
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {
        if (!broadcastedCreation) {
            broadcastDesignSchoolCreation(rc.getLocation());
        }
        for (Direction dir : Util.directions) {
            if (tryBuild(RobotType.LANDSCAPER, dir)) {
                System.out.println("build a landscaper");
            }
        }
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : Util.directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {
        if (rc.getDirtCarrying() == 0) {
            tryDig();
        }
        MapLocation bestPlaceToBuildWall = null;
        if (hqLoc != null) {
            int lowestElevation = 9999999;
            for (Direction dir : Util.directions) {
                MapLocation tileToCheck = hqLoc.add(dir);
                if (rc.getLocation().distanceSquaredTo(tileToCheck) < 4 && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))) {
                    if (rc.senseElevation(tileToCheck) < lowestElevation) {
                        lowestElevation = rc.senseElevation(tileToCheck);
                        bestPlaceToBuildWall = tileToCheck;
                    }
                }
            }
        }
        if (Math.random() < 0.4) {
            if (bestPlaceToBuildWall != null) {
                rc.depositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall));
                System.out.println("building a wall");
            }
        }
        if (hqLoc != null) {
            goTo(hqLoc);
        } else {
            tryMove(randomDirection());
        }
    }

    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
//            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    static void checkIfSoupGone() throws GameActionException {
        if (soupLocation.size() > 0) {
            MapLocation targetSoupLoc = soupLocation.get(0);
            if (rc.canSenseLocation(targetSoupLoc) && rc.senseSoup(targetSoupLoc) == 0) {
                soupLocation.remove(0);
            }
        }
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return Util.directions[(int) (Math.random() * Util.directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
//    static RobotType randomSpawnedByMiner() {
//        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
//    }

    static boolean nearbyRobot(RobotType target) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots) {
            if (r.getType() == target) {
                return true;
            }
        }
        return false;
    }

    static boolean tryDig() throws GameActionException {
        Direction dir = randomDirection();
        if (rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }




    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : Util.directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    static boolean goTo(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft().rotateRight(), dir.rotateLeft().rotateRight(), dir.rotateRight().rotateLeft()};
        for (Direction d : toTry)
            if (tryMove(d))
                return true;
        return false;
    }

    static boolean goTo(MapLocation dir) throws GameActionException {
        return goTo(rc.getLocation().directionTo(dir));
    }
}