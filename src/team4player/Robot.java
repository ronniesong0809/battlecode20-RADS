package team4player;
import battlecode.common.*;

import java.util.NavigableMap;

public class Robot {
    RobotController rc;
    Broadcast bc;
    Navigation nav;
    int turnCount = 0;

    public Robot(RobotController r) {
        this.rc = r;
        bc = new Broadcast(rc);
        nav = new Navigation(rc);
    }

    public void takeTurn() throws GameActionException{
        turnCount += 1;
        System.out.println("I'm a robot");
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }
}
