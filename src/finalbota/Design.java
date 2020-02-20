package finalbota;

import battlecode.common.*;

public class Design extends MyRobot {

    RobotController rc;
    Comm comm;
    MapLocation myLoc;
    BuildingZone buildingZone;
    boolean needDrone;
    boolean enemyNetGunNearby;
    Direction[] dirs = Direction.values();
    RushManager rushManager;

    Design(RobotController rc){
        this.rc = rc;
        comm = new Comm(rc);
        myLoc = rc.getLocation();
        rushManager = new RushManager(rc, comm);
        buildingZone = new BuildingZone(rc);
    }

    void play(){
        if (comm.singleMessage()) comm.readMessages();
        if (shouldBuildLandscaper()) {
            build(RobotType.LANDSCAPER, false);
        }
        BuildingManager.printDebug(comm);
        comm.readMessages();
        if (comm.wallMes != null) buildingZone.update(comm.wallMes);
        if (!buildingZone.finished()) buildingZone.run();
    }

    boolean shouldBuildLandscaper(){
        if (!comm.upToDate()) return false;
        if (!BuildingManager.haveSoupToSpawn(rc, RobotType.LANDSCAPER)) return false;
        if (rushManager.rushBuild() == RobotType.LANDSCAPER) return true;
        /*checkUnits();
        if (comm.isRush() && comm.buildings[RobotType.LANDSCAPER.ordinal()] <= 2){
            if (enemyNetGunNearby && comm.buildings[RobotType.LANDSCAPER.ordinal()] == 0) return true;
            if (needDrone && comm.buildings[RobotType.FULFILLMENT_CENTER.ordinal()] > 0 && comm.buildings[RobotType.LANDSCAPER.ordinal()] > 0) return false;
            build(RobotType.LANDSCAPER, true);
            if (needDrone) return false;
            return true;
        }*/
        return BuildingManager.shouldBuildLandscaper(comm, rc);
    }

    void build (RobotType r, boolean adj){
        try{
            if (Constants.DEBUG == 1) System.out.println("Trying to build landscaper!");
            if (!buildingZone.finished()) return;
            LandscaperBuildingSpot bestSpot = null;
            for (Direction dir : dirs){
                LandscaperBuildingSpot spot = new LandscaperBuildingSpot(dir);
                if (spot.isBetterThan(bestSpot)) bestSpot = spot;
            }
            if (bestSpot == null) return;
            if (adj && bestSpot.distToHQ > 2) return;
            rc.buildRobot(r, bestSpot.dir);
            comm.sendMessage(Comm.BUILDING_TYPE, r.ordinal());
        } catch(Throwable t){
            t.printStackTrace();
        }
    }

    /*boolean visibleLandscaper(){
        RobotInfo[] visibleRobots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam().opponent());
        for (RobotInfo r : visibleRobots){
            if (r.getType() == RobotType.LANDSCAPER) return true;
        }
        return false;
    }*/

    class LandscaperBuildingSpot{
        Direction dir;
        boolean canBuild;
        MapLocation loc;
        int zone;
        int distToHQ = 100;

        int buildingScore = -1;

        int score(){
            if (buildingScore >= 0) return buildingScore;
            switch(zone){
                case BuildingZone.WALL:
                case BuildingZone.OUTER_WALL:
                    buildingScore = 1;
                    return buildingScore;
                case BuildingZone.NEXT_TO_WALL:
                    buildingScore = 2;
                    return buildingScore;
                case BuildingZone.BUILDING_AREA:
                    buildingScore = 3;
                    return buildingScore;
                default:
                    buildingScore = 0;
                    return buildingScore;
            }
        }

        LandscaperBuildingSpot(Direction dir){
            this.dir = dir;
            loc = myLoc.add(dir);
            if (buildingZone.HQloc != null) distToHQ = loc.distanceSquaredTo(buildingZone.HQloc);
            if (rc.canSenseLocation(loc)){
                canBuild = rc.canBuildRobot(RobotType.LANDSCAPER, dir);
                zone = buildingZone.getZone(loc);
            }
        }

        boolean isBetterThan(LandscaperBuildingSpot s){
            if (!canBuild) return false;
            if (s == null) return true;
            if (score() == s.score()) return distToHQ < s.distToHQ;
            return score() > s.score();
        }

    }

    void checkUnits(){
        enemyNetGunNearby = false;
        needDrone = false;
        int match = 0;
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots){
            if (r.team != rc.getTeam()) {
                switch (r.type) {
                    case LANDSCAPER:
                    case MINER:
                        ++match;
                        break;
                    case NET_GUN:
                        enemyNetGunNearby = true;
                        break;
                }
            } else{
                switch (r.type) {
                    case DELIVERY_DRONE:
                        if (!r.isCurrentlyHoldingUnit()){
                            --match;
                            break;
                        }
                    default:
                        break;
                }
            }
        }
        needDrone = match > 0;
    }

}
