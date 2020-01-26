package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Refinery extends Building{
    public Refinery(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
        for (Direction dir : Util.directions) {
            if (tryBuild(RobotType.REFINERY, dir)) {
                System.out.println("build a refinery");
                System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
            }

        }
        //FIXME:
    }
}
