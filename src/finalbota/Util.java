package finalbota;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Util {

    static boolean stuck(MapLocation loc, RobotController rc, BuildingZone buildingZone){
        if (!buildingZone.finished()) return false;
        int zone = buildingZone.getZone(loc);
        switch (zone) {
            case BuildingZone.WALL:
            case BuildingZone.OUTER_WALL:
                return false;
            case BuildingZone.BUILDING_AREA:
            case BuildingZone.NEXT_TO_WALL:
                return true;
        }
        try{
            int elev = rc.senseElevation(loc);
            if (elev < Constants.WALL_HEIGHT - GameConstants.MAX_DIRT_DIFFERENCE) return true;
        } catch (Throwable t){
            t.printStackTrace();
        }
        return false;
    }


}
