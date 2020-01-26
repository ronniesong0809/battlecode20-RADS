package team4player;
import battlecode.common.*;

public class DesignSchool extends Building{
    boolean broadcastCreation = false;
    public DesignSchool(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (!broadcastCreation) {
            bc.broadcastDesignSchoolCreation(rc.getLocation());
        }
        for (Direction dir : Util.directions) {
            if (tryBuild(RobotType.LANDSCAPER, dir)) {
                System.out.println("build a landscaper");
            }
        }
    }
}
