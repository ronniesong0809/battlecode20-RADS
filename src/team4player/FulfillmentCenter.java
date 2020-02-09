package team4player;
import battlecode.common.*;

public class FulfillmentCenter extends Building{
    public int numDrones = 0;
    static int numRounds = 0;
    public FulfillmentCenter(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        numRounds++;
        if (numRounds % 20 == 0) {
            bc.broadcastFulfillmentCenterCreation();
        }

        if (numDrones < 1) {
            if (tryBuild(RobotType.DELIVERY_DRONE, Util.randomDirection())) {
                numDrones++;
            }
        }
    }//TODO:
}
