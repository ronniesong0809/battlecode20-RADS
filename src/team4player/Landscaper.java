package team4player;
import battlecode.common.*;

public class Landscaper extends Unit{
    public Landscaper(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

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
        if (Math.random() < 0.8) {
            if (bestPlaceToBuildWall != null) {
                rc.depositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall));
                System.out.println("building a wall");
            }
        }
        if (hqLoc != null) {
            nav.goTo(hqLoc);
        } else {
            nav.goTo(Util.randomDirection());
        }
    }

    boolean tryDig() throws GameActionException {
        Direction dir;
        if(hqLoc == null){
            dir = Util.randomDirection();
        } else {
            dir = hqLoc.directionTo(rc.getLocation());
        }
        if (rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }
}
