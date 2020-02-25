package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class DesignSchool extends Building {
    static int numLandscapers = 0;
    static int numRounds = 0;
    boolean broadcastCreation = false;

    public DesignSchool(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        numRounds++;

        if (numLandscapers < 15) {
            if (tryBuild(RobotType.LANDSCAPER, Util.randomDirection())) {
                System.out.println("build a landscaper");
                numLandscapers++;
            }
        }
    }
}
