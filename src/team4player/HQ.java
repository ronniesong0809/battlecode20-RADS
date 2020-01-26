package team4player;
import battlecode.common.*;

public class HQ extends Building {
    static int numMiners = 0;

    public HQ(RobotController rc) throws GameActionException{
        super(rc);
        bc.sendHqLoc(rc.getLocation());
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (numMiners < 5) {
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    numMiners++;
                }
            }
        }
    }
}
