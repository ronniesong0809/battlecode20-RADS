package team4player;
import battlecode.common.*;

public class Miner  extends Unit{
    public Miner(RobotController rc) {
        super(rc);
    }

    public void runTurn() throws GameActionException {
        updateUnitCounts();
        updateSoupLocation();
        checkIfSoupGone();


        for (Direction dir : Util.directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : Util.directions)
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if (!soupLocation.contains(soupLoc)) {
                    broadcastSoupLocation(soupLoc);
                }
            }
        if (numDesignSchool < 3) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection())) {
                System.out.println("build a Design School");
            }
        }
        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            System.out.println("at soup limit");
//            Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
//            if(goTo(dirToHQ)){
//            if (hqLoc != null) {
            if (goTo(hqLoc)) {
                System.out.println("Toward to HQ!");
            }
//            }
        } else if (soupLocation.size() > 0) {
            goTo(soupLocation.get(0));
        } else if (goTo(randomDirection())) {
            System.out.println("I moved!");
        }
    }

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
        } else return false;
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
        } else return false;
    }
}
