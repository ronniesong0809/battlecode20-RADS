package team4player;
import battlecode.common.*;

public class DesignSchool extends Building{
    static int numLandscapers = 0;
    boolean broadcastCreation = false;
    static int numRounds = 0;
    public DesignSchool(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        numRounds++;

        if (numRounds % 20 == 0) {
            bc.broadcastDesignSchoolCreation();
        }

        if (numLandscapers < 8) {
            if (tryBuild(RobotType.LANDSCAPER, Util.randomDirection())) {
                System.out.println("build a landscaper");
                numLandscapers++;
            }
        }
    }
}
