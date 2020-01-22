package team4player;
import battlecode.common.*;
import java.lang.Math;
import java.util.ArrayList;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

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
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                findHQ();
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void findHQ() throws GameActionException{
        if (hqLoc == null) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot: robots) {
                if (robot.type == RobotType.HQ && robot.team == rc.getTeam()) {
                    hqLoc = robot.location;
                }
            }
            if(hqLoc == null) {
                getHqLocFromBlockchain();
            }
        }
    }

    static MapLocation findRefinery() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot: robots) {
            if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam()) {
                return robot.location;
            }
        }
        return null;
    }

    static void runHQ() throws GameActionException {
        if(turnCount == 1){
            sendHqLoc(rc.getLocation());
        }
        // limit miners to 10
        if (numMiners < 10) {
            for (Direction dir : directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    numMiners++;
                }
            }
        }
    }

    static void runMiner() throws GameActionException {
        updateUnitCounts();
        updateSoupLocation();
        checkIfSoupGone();

        for (Direction dir : directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : directions)
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if(!soupLocation.contains(soupLoc)){
                    broadcastSoupLocation(soupLoc);
                }
            }
        if(numDesignSchool < 3) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection())) {
                System.out.println("build a Design School");
            }
        }
        if(rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            System.out.println("at soup limit");
//            Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
//            if(goTo(dirToHQ)){
//            if (hqLoc != null) {
            if (goTo(hqLoc)) {
                System.out.println("Toward to HQ!");
            }
//            }
        } else if (soupLocation.size() > 0){
            goTo(soupLocation.get(0));
        } else if (goTo(randomDirection())){
            System.out.println("I moved!");
        }
    }

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
        if (numRefinery < 3) {
            for (Direction dir : directions)
                if (tryBuild(RobotType.REFINERY, dir)){
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
        for (Direction dir : directions) {
            if (tryBuild(RobotType.LANDSCAPER, dir)) {
                System.out.println("build a landscaper");
            }
        }
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {
        if(rc.getDirtCarrying() == 0) {
            tryDig();
        }
        MapLocation bestPlaceToBuildWall = null;
        if(hqLoc!=null){
            int lowestElevation = 9999999;
            for (Direction dir : directions){
                MapLocation tileToCheck = hqLoc.add(dir);
                if(rc.getLocation().distanceSquaredTo(tileToCheck) < 4 && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))){
                    if(rc.senseElevation(tileToCheck)<lowestElevation){
                        lowestElevation = rc.senseElevation(tileToCheck);
                        bestPlaceToBuildWall = tileToCheck;
                    }
                }
            }
        }
        if (Math.random() < 0.4 ){
            if(bestPlaceToBuildWall!=null){
                rc.depositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall));
                System.out.println("building a wall");
            }
        }
        if(hqLoc !=null){
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
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    static void checkIfSoupGone() throws GameActionException {
        if(soupLocation.size() > 0){
            MapLocation targetSoupLoc = soupLocation.get(0);
            if(rc.canSenseLocation(targetSoupLoc) && rc.senseSoup(targetSoupLoc) == 0){
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
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean nearbyRobot(RobotType target) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo r : robots){
            if(r.getType() == target) {
                return true;
            }
        }
        return false;
    }

    static boolean tryDig() throws GameActionException {
        Direction dir = randomDirection();
        if(rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
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

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
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

    static final int teamSecret = 4444444;
    static final String[] messageType = {"HQ loc", "design school created", "soup location"};

    public static void sendHqLoc(MapLocation loc) throws GameActionException{
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 0;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
        }
    }

    public static void getHqLocFromBlockchain() throws GameActionException {
        System.out.println("BLOCKCHAIN!");
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
                if(mess[0] == teamSecret && mess[1]==0){
                    System.out.println("found the HQ!");
                    hqLoc = new MapLocation(mess[2], mess[3]);
                }
            }
        }
    }

    public static boolean broadcastedCreation = false;

    public static void broadcastDesignSchoolCreation(MapLocation loc) throws GameActionException{
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 1;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
            broadcastedCreation = true;
        }
    }

    public static void updateUnitCounts() throws GameActionException {
        for(Transaction tx: rc.getBlock(rc.getRoundNum()-1)){
            int[] mess = tx.getMessage();
            if(mess[0] == teamSecret && mess[1]==1){
                System.out.println("found the HQ!");
                numDesignSchool += 1;
            }
        }
    }

    public static void broadcastSoupLocation(MapLocation loc) throws GameActionException {
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 2;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
            System.out.println("found soup at " + loc);
        }
    }

    public static void updateSoupLocation() throws GameActionException {
        for(Transaction tx: rc.getBlock(rc.getRoundNum()-1)){
            int[] mess = tx.getMessage();
            if(mess[0] == teamSecret && mess[1]==2){
                System.out.println("heard new soup!");
                soupLocation.add(new MapLocation(mess[2], mess[3]));
            }
        }
    }

}
