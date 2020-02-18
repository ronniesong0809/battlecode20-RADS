package finalbota;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class WaterManager {

    RobotController rc;

    WaterManager(RobotController rc){
        this.rc = rc;
    }

    static final int[] wHeight = new int[]{0, 256, 464, 677, 931, 1210, 1413, 1546, 1640, 1713, 1771, 1819, 1861, 1897, 1929, 1957, 1983, 2007, 2028, 2048, 2067, 2084, 2100, 2115, 2129, 2143, 2155, 2168, 2179, 2190, 2201, 2211, 2220, 2230, 2239, 2247, 2256, 2264, 2271, 2279, 2286, 2293, 2300, 2307, 2313, 2319, 2325, 2331, 2337, 2343, 2348};
    static int waterLevel = 0;
    static int waterLevelPlus = 0;
    static int waterLevelWall = 0;
    static MapLocation closestSafeCell = null;
    static int height;
    static final int MIN_SAFE_TURNS = 7;

    void update(){
        int r = rc.getRoundNum();
        while (waterLevel+1 < wHeight.length && r >= wHeight[waterLevel+1]) waterLevel++;
        while (waterLevelWall+1 < wHeight.length && r+ Constants.SAFETY_WALL_TURNS >= wHeight[waterLevelWall+1]) waterLevelWall++;
        while (waterLevelPlus+1 < wHeight.length && r+MIN_SAFE_TURNS >= wHeight[waterLevelPlus+1]){
            waterLevelPlus++;
            if (height <= waterLevelPlus){
                height = 0;
                closestSafeCell = null;
            }
        }
    }

    int safetyWall(){
        return Constants.MIN_SAFETY_WALL;
    }

}
