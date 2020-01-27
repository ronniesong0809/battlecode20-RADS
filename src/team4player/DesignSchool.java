package team4player;
import battlecode.common.*;

public class DesignSchool extends Building{
    static int numLandscapers = 0;
    boolean broadcastCreation = false;
    public DesignSchool(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (!broadcastCreation) {
            bc.broadcastDesignSchoolCreation(rc.getLocation());
        }
        if (numLandscapers < 2) {
            if (tryBuild(RobotType.LANDSCAPER, Util.randomDirection())) {
                System.out.println("build a landscaper");
                numLandscapers++;
            }
        }
    }
}
