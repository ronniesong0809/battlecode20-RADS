package finalbota;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {


    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        MyRobot r;
        switch(rc.getType()){
            case HQ:
                r = new HQ(rc);
                break;
            case MINER:
                r = new Miner(rc);
                break;
            case REFINERY:
                r = new Refinery(rc);
                break;
            case VAPORATOR:
                r = new Vaporator(rc);
                break;
            case DESIGN_SCHOOL:
                r = new Design(rc);
                break;
            case FULFILLMENT_CENTER:
                r = new Fullfillment(rc);
                break;
            case LANDSCAPER:
                r = new Landscaper(rc);
                break;
            case DELIVERY_DRONE:
                r = new Drone(rc);
                break;
            default:
                r = new Gun(rc);
                break;
        }

        while (true) {
            r.play();
            Clock.yield();
        }
    }
}
