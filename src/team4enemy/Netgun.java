package team4enemy;

import battlecode.common.*;

public class Netgun extends Building {
    public Netgun(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        shootDrone();
    }
}