package team4player;
import battlecode.common.*;

public class Unit extends Robot{

    MapLocation hqLoc;

    public Unit(RobotController rc) {
        super(rc);
    }
    public void takenTurn()throws GameActionException{
        super.takeTurn();
    }

    public void findHQ() throws GameActionException {
        if (hqLoc == null) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == rc.getTeam()) {
                    hqLoc = robot.location;
                }
            }
            if (hqLoc == null) {
                hqLoc = bc.getHqLocFromBlockchain();
            }
        }
    }
}
