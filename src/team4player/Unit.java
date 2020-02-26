package team4player;

import battlecode.common.*;
import java.util.ArrayList;

public class Unit extends Robot {

    MapLocation hqLoc;
    Navigation nav;
    ArrayList<MapLocation> enemyHQlocs = new ArrayList<MapLocation>(3);

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
        if(rc.getRoundNum()==3)
            findEnemyHQ();
    }
    public void findEnemyHQ() throws GameActionException {

        if (hqLoc != null) {
            int hq_x = hqLoc.x;
            int hq_y = hqLoc.y;
            int map_x = rc.getMapWidth()-1;
            int map_y = rc.getMapHeight()-1;
            // 3 possible enemy location
            enemyHQlocs.add(new MapLocation(map_x-hq_x,hq_y));
            enemyHQlocs.add(new MapLocation(map_x-hq_x,map_y-hq_y));
            enemyHQlocs.add(new MapLocation(hq_x,map_y-hq_y));
            System.out.println(enemyHQlocs);
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

    public int hqLocx() {
        return hqLoc.x;
    }

    public int hqLocy() {
        return hqLoc.y;
    }
}
