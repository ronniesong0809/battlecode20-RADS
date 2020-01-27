package team4player;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner extends Unit{
    static int numDesignSchool = 0;
    static int numRefinery = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();

    public Miner(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        //super.takeTurn();
        MapLocation[] soup = rc.senseNearbySoup(-1); // we want a loop until we find the soup, not iterate through above cases (waste of time/instructions)
        if (soup != null && soup.length != 0) { // we found soup! Head towards it
            int randomLoc = (int) Math.random() * soup.length + 0; // random soup location we are moving towards
            Direction mineDir = walkTowardsSoup(soup, randomLoc); // until you get near there
            // Refine soup if we are full.
            if (rc.getSoupCarrying() >= 70) {
                refineSoup();
            } else {
                if (mineDir == null) {
                    return;
                }
                boolean stillMining = true;
                while (stillMining) {
                    stillMining = false; // mines all directions
                    if (tryMine(mineDir) == false) {
                        for (Direction dir : Util.directions)
                            if (tryMine(dir)) {
                                System.out.println("MINING SOUP");
                                mineDir = dir;
                                stillMining = true;
                            }
                    }
                }
            }
        } else {
            System.out.println("GOING RANDOM DIRECTION");
            nav.goTo(Util.randomDirection());
        }
        /*
        numDesignSchool += bc.updateUnitCounts();
        //TODO -- broadcasting, checking if broadcast is stale
         //bc.updateUnitCounts();
         //bc.updateSoupLocations(soupLocations);
         //checkIfSoupGone();

        nearbySoup();

        for (Direction dir : Util.directions)
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if (!soupLocations.contains(soupLoc)) {
                    bc.broadcastSoupLocation(soupLoc);
                }
            }
        for (Direction dir : Util.directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());

        // Sense design schools around to see if this miner should build one.
//        numDesignSchool = 0;
        RobotInfo [] nearbyRobots = rc.senseNearbyRobots();
//        for (RobotInfo r : nearbyRobots) {
//            if (r.type == RobotType.DESIGN_SCHOOL) {
//                numDesignSchool++;
//            }
//        }
        if (numDesignSchool < 3) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, Util.randomDirection())) {
                System.out.println("build a Design School");
            }
        }


        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            MapLocation nearestRefinery = null;
            for (RobotInfo r : nearbyRobots) {
                if (r.type == RobotType.REFINERY) {
                    nearestRefinery = new MapLocation(r.getLocation().x, r.getLocation().y);
                }
            }

            if (nearestRefinery == null) {
                if (tryBuild(RobotType.REFINERY, Util.randomDirection())) {
                    System.out.println("Built Refinery");
                }
            }

            System.out.println("at soup limit");
            MapLocation toGo = null;
            if (nearestRefinery != null) {
                toGo = nearestRefinery;
            }
            else {
                toGo = hqLoc;
            }
            if (nav.goTo(toGo)) {
                System.out.println("Toward to HQ!");
            }
            nav.goTo(Util.randomDirection());
        } else if (soupLocations.size() > 0) {
            nav.goTo(soupLocations.get(0));
        } else {
            if (nav.goTo(Util.randomDirection())) {
                System.out.println("I moved!");
            }
        }*/
    }

    public Direction walkTowardsSoup(MapLocation [] soup, int randomLoc) throws GameActionException {
        //NOTE: do NOT use a while loop. Miners will go to old soup locations that are dried up if you do.
        while(true) {
            System.out.println("Towards soup!");
            for (Direction dir : Util.directions) {
                if (rc.canMineSoup(dir)) {
                    return dir;
                }
            }
            if (!nav.goTo(soup[randomLoc])) {
                nav.goTo(Util.randomDirection());
            } // This gets miners unstuck if they are being blocked
        }
        //return null;
    }

    public void refineSoup() throws GameActionException {
        MapLocation refineryLocation = findRefinery();
        while (rc.getSoupCarrying() >= 70) {
            System.out.println("at soup limit");
            if (refineryLocation == null) { for (Direction dir : Util.directions) { if (tryBuild(RobotType.REFINERY, dir)) { break; } } }
            refineryLocation = findRefinery(); // need to call again, since we just built one
            if (refineryLocation != null) {
                while(true){
                    System.out.println("Toward to Refinery!");
                    if (nav.goTo(refineryLocation) == false) { break; }
                }
            }
            else{
                /*while(true){
                    System.out.println("Toward to HQ!");
                    if(nav.goTo(hqLoc) == false){ break;}
                }*/
            }
            System.out.println("TRYING TO DEPOSIT SOUP...");
            for (Direction dir : Util.directions)
                if(tryRefine(dir)){ System.out.println("SUCCESFULLY DEPOSITED SOUP"); break;}
            if (refineryLocation == null) nav.goTo(Util.randomDirection());
        }
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
     //RobotType randomSpawnedByMiner() {
     //    return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
     //}

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        }
        return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        }
        return false;
    }

    void checkIfSoupGone() throws GameActionException {
        if (soupLocations.size() > 0) {
            MapLocation targetSoupLoc = soupLocations.get(0);
            if (rc.canSenseLocation(targetSoupLoc) && rc.senseSoup(targetSoupLoc) == 0) {
                soupLocations.remove(0);
            }
        }
    }

    MapLocation nearbySoup() throws GameActionException {
        MapLocation[] souplocations = rc.senseNearbySoup();
        for (MapLocation souploc : souplocations) {
            MapLocation soupLoc = rc.getLocation();
        }
        return null;
    }
}
