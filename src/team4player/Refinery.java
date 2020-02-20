package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Refinery extends Building {
    public Refinery(RobotController rc) throws GameActionException {
        super(rc);
        bc.sendRefineryLocToBlockchain(rc.getLocation()); // just post once when created
        //System.out.println(bc.sendRefineryLocToBlockchain(rc.getLocation()));
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        //System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }
}
