package team4player;
import battlecode.common.*;

public class HQ extends Building {
    static int numMiners = 0;
    public HQ(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if(turnCount == 1){
            bc.sendHqLoc(rc.getLocation());
        }

        if (numMiners < 10) {
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    numMiners++;
                }
            }
        }
    }
}
