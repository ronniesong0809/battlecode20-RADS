package team4player;
import battlecode.common.*;

public class FulfillmentCenter extends Building{
    public FulfillmentCenter(RobotController rc){
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        for (Direction dir : Util.directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }//TODO:
}
