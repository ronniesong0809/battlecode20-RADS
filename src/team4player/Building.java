package team4player;

import battlecode.common.*;

public class Building extends Robot {
    public Building(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
    }

    public void shootDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        RobotInfo[] enemiesInRange = rc.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, enemy);
        for (RobotInfo e : enemiesInRange) {
            if (e.type == RobotType.DELIVERY_DRONE) {
                if (e.getHeldUnitID() > 0) {
                    if (rc.canShootUnit(e.ID)) {
                        rc.shootUnit(e.ID);
                        System.out.println("I shoot'ed enemy! " + e.type);
                    }
                }
            }
        }
    }
}
