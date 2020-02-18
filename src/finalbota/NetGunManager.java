package finalbota;


import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class NetGunManager {

    RobotController rc;
    MapLocation myLoc;

    NetGunManager(RobotController rc){
        this.rc = rc;
        myLoc = rc.getLocation();
    }

    void tryShoot(){
        if (!rc.isReady()) return;
        ShootingTarget s = null;
        int sight = rc.getCurrentSensorRadiusSquared();
        if (sight > GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED) sight = GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED;
        RobotInfo[] rArray = rc.senseNearbyRobots(sight, rc.getTeam().opponent());
        for (RobotInfo r : rArray){
            if (rc.canShootUnit(r.getID())){
                ShootingTarget t = new ShootingTarget(r);
                if (t.isBetterThan(s)) s = t;
            }
        }
        if (s != null) s.shoot();
    }

    class ShootingTarget{
        int dist;
        int id;

        ShootingTarget(RobotInfo r){
            dist = r.location.distanceSquaredTo(myLoc);
            id = r.getID();
        }

        boolean isBetterThan(ShootingTarget s){
            if (s == null) return true;
            return dist < s.dist;
        }

        void shoot(){
            try{
                rc.shootUnit(id);
            } catch (Throwable t){
                t.printStackTrace();
            }
        }

    }

}
