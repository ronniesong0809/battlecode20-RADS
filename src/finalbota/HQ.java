package finalbota;

import battlecode.common.*;

public class HQ extends MyRobot {

    RobotController rc;

    HQWall hqWall;
    BuildingZone buildingZone;
    NetGunManager netGunManager;

    int myX, myY;
    MapLocation myLoc;
    Comm comm;
    int miners = 0;
    int maxSoup = 0;
    int localMaxSoup = 0;
    int MIN_MAX_SOUP = 1000;
    Direction dirToSoup = null;
    boolean rush;
    DroneChecker droneChecker;

    final int MINERS_BEFORE_FULFILLMENT = 5;
    final int BUILDER_SOUP = 1080;

    int[] X = new int[]{0,-1,0,0,1,-1,-1,1,1,-2,0,0,2,-2,-2,-1,-1,1,1,2,2,-2,-2,2,2,-3,0,0,3,-3,-3,-1,-1,1,1,3,3,-3,-3,-2,-2,2,2,3,3,-4,0,0,4,-4,-4,-1,-1,1,1,4,4,-3,-3,3,3,-4,-4,-2,-2,2,2,4,4,-5,-4,-4,-3,-3,0,0,3,3,4,4,5,-5,-5,-1,-1,1,1,5,5,-5,-5,-2,-2,2,2,5,5,-4,-4,4,4,-5,-5,-3,-3,3,3,5,5,-6,0,0,6,-6,-6,-1,-1,1,1,6,6,-6,-6,-2,-2,2,2,6,6,-5,-5,-4,-4,4,4,5,5,-6,-6,-3,-3,3,3,6,6};
    int[] Y = new int[]{0,0,-1,1,0,-1,1,-1,1,0,-2,2,0,-1,1,-2,2,-2,2,-1,1,-2,2,-2,2,0,-3,3,0,-1,1,-3,3,-3,3,-1,1,-2,2,-3,3,-3,3,-2,2,0,-4,4,0,-1,1,-4,4,-4,4,-1,1,-3,3,-3,3,-2,2,-4,4,-4,4,-2,2,0,-3,3,-4,4,-5,5,-4,4,-3,3,0,-1,1,-5,5,-5,5,-1,1,-2,2,-5,5,-5,5,-2,2,-4,4,-4,4,-3,3,-5,5,-5,5,-3,3,0,-6,6,0,-1,1,-6,6,-6,6,-1,1,-2,2,-6,6,-6,6,-2,2,-4,4,-5,5,-5,5,-4,4,-3,3,-6,6,-6,6,-3,3};

    HQ(RobotController rc){
        this.rc = rc;
        comm = new Comm(rc);
        myLoc = rc.getLocation();
        myX = myLoc.x; myY = myLoc.y;
        buildingZone = new BuildingZone(rc);
        hqWall = new HQWall(rc);
        netGunManager = new NetGunManager(rc);
        //if (Constants.DEBUG == 1) System.out.println("I'm at (" + rc.getLocation().x + ", " + rc.getLocation().y + ")");
    }

    void play(){
        if (comm.singleMessage()) comm.readMessages();
        if (comm.maxSoup > maxSoup) maxSoup = comm.maxSoup;
        checkRush();
        if (comm.isRush()) System.out.println("RUSHHH LOL");
        else System.out.println("NOT RUSH");
        System.out.println(comm.maxSoup);
        netGunManager.tryShoot();
        if (rush){
            if (droneChecker == null) droneChecker = new DroneChecker(rc, comm);
            droneChecker.checkForNetGuns();
        }


        getDirToSoup();
        if (shouldBuildMiner()) buildMiner(false);
        if (shouldBuildBuilder()){
            buildMiner(true);
        }

        RobotType t = BuildingManager.getNextBuilding(comm);
        if (t != null) System.out.println(t.name());
        else System.out.println("No building! " + comm.maxSoup);

        comm.readMessages();

        hqWall.run();
        if (comm.wallMes != null) buildingZone.update(comm.wallMes);
        if (hqWall.finished()) comm.sendWall(hqWall.mes);
        buildingZone.run();
        if (buildingZone.finishedHQ()){
            comm.sendWallFinished();
        }


    }

    boolean shouldBuildMiner(){
        if (!rc.isReady()) return false;
        if (miners <= 3) return true;
        //if (miners >= Constants.MAX_MINERS) return false;
        if (rush) return false;
        if ((comm.buildings[RobotType.FULFILLMENT_CENTER.ordinal()] == 0 || comm.buildings[RobotType.DELIVERY_DRONE.ordinal()] == 0) && miners >= MINERS_BEFORE_FULFILLMENT) return false;
        if (miners <= getMinerNumber()) return true;
        if (miners >= Constants.MAX_MINERS) return false;
        if (comm.wallFinished || comm.buildings[RobotType.LANDSCAPER.ordinal()] >= 2) return false;
        int soup = MIN_MAX_SOUP;
        if (maxSoup > soup) soup = maxSoup;
        if (comm.maxSoup > maxSoup) soup = comm.maxSoup;
        if (miners <= (soup - 2*miners*rc.getRoundNum()) / Constants.SOUP_PER_MINER) return true;
        return false;
    }

    int getMinerNumber(){
        int vaps = comm.buildings[RobotType.VAPORATOR.ordinal()];
        if (vaps >= 10) vaps = 10 + ((vaps-10)*2)/3;
        return vaps;
    }

    void buildMiner(boolean send){
        try {
            if (rc.getTeamSoup() < RobotType.MINER.cost) return;
            Direction dir = dirToSoup;
            if (dir == Direction.CENTER || dir == null) dir = Direction.NORTH;
            for (int i = 0; i < 8; ++i){
                if (rc.canBuildRobot(RobotType.MINER, dir)){
                    rc.buildRobot(RobotType.MINER, dir);
                    ++miners;
                    if (send) comm.sendMessage(comm.BUILDER_TYPE, 0);
                    return;
                }
                dir = dir.rotateLeft();
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    //TODO improve this crap
    void getDirToSoup(){
        try {
            int soup = 0;
            dirToSoup = null;
            for (int i = 0; i < X.length; ++i) {
                MapLocation loc = new MapLocation(myX + X[i], myY + Y[i]);
                if (myLoc.distanceSquaredTo(loc) > rc.getCurrentSensorRadiusSquared()) break;
                if (!rc.canSenseLocation(loc)) continue;
                if (rc.senseSoup(loc) > 0 && !rc.senseFlooding(loc)){
                    soup += rc.senseSoup(loc);
                    if (dirToSoup == null) dirToSoup = myLoc.directionTo(loc);
                }
            }
            if (soup > maxSoup) maxSoup = soup;
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    boolean shouldBuildBuilder(){
        RobotType r = BuildingManager.getNextBuilding(comm);
        if (rush && r != RobotType.DESIGN_SCHOOL && r != RobotType.FULFILLMENT_CENTER) return false;
        if (r == null) return false;
        if (minerNearby()) return false;
        if (rc.getTeamSoup() < r.cost + RobotType.MINER.cost + Constants.SAFETY_SOUP) return false;
        return true;
    }

    boolean minerNearby(){
        if (!buildingZone.finished()) return false;
        RobotInfo[] r = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
        for (RobotInfo ri : r){
            if (ri.getType() == RobotType.MINER){
                if (buildingZone.getZone(ri.location) != BuildingZone.HOLE) return true;
            }
        }
        return false;
    }

    void checkRush(){
        rush = false;
        if (isRush()){
            rush = true;
            comm.sendRush();
        }
        else comm.sendRushEnd();
    }

    boolean isRush(){
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam().opponent());
        for (RobotInfo r : enemies){
            switch (r.type){
                case MINER:
                case LANDSCAPER:
                case NET_GUN:
                case DESIGN_SCHOOL:
                    //System.out.println("Found one!!");
                    if (rc.getRoundNum() < Constants.MAX_TURN_RUSH){
                        return true;
                    }
                    if (buildingZone.finished()) {
                        //System.out.println("Got here!! " + buildingZone.getZone(r.location));
                        if (buildingZone.isCritical(r.location)) return true;
                    }
                default:
                    break;
            }
        }
        return false;
    }

}
