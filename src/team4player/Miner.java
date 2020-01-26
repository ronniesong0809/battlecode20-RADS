package team4player;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner  extends Unit{
    int numDesignSchool = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();

    public Miner(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        numDesignSchool += bc.updateUnitCounts();
        bc.updateUnitCounts();
        bc.updateSoupLocations(soupLocations);
        checkIfSoupGone();
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

        if (numDesignSchool < 3) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, Util.randomDirection())) {
                System.out.println("build a Design School");
            }
        }

        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            System.out.println("at soup limit");
            if (nav.goTo(hqLoc)) {
                System.out.println("Toward to HQ!");
            }
            nav.goTo(Util.randomDirection());
        } else if (soupLocations.size() > 0) {
            nav.goTo(soupLocations.get(0));
        } else {
            if (nav.goTo(Util.randomDirection())) {
                System.out.println("I moved!");
            }
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
