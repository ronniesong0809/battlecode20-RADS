package team4enemy;

import battlecode.common.*;

public class Unit extends Robot {

    MapLocation hqLoc;
    Navigation nav;

    public Unit(RobotController rc) {
        super(rc);
        nav = new Navigation(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        findHQ();
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
                hqLoc = Broadcast.getHqLocFromBlockchain();
            }
        }
    }

    MapLocation findRefinery() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam()) {
                return robot.location;
            }
        }
        return null;
    }

    boolean nearbyRobot(RobotType target) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots) {
            if (r.getType() == target) {
                return true;
            }
        }
        return false;
    }
}
