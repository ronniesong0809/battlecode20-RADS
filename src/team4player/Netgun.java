package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Netgun extends Building {
    public Netgun(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        shootDrone();
    }
}