package finalbota;

import battlecode.common.*;

public class Miner extends MyRobot {

    RobotController rc;
    Direction[] dirs = Direction.values();
    BugPath bugPath;
    ExploreMiner explore;
    WaterManager waterManager;
    Comm comm;
    BuildingZone buildingZone;
    Danger danger;
    boolean builder;
    boolean buildOnWall = false;
    MapLocation myLoc;

    int currentSoup = 0;
    int turnsWithoutMining = 0;

    final int MAX_ELEVATION = 2;
    final int MIN_SOUP_FOR_REFINERY = 1000;
    final int MIN_DIST_FOR_REFINERY = 50;
    final int MAX_BUILD_TURNS = 6;
    final int DESPERATE_TURNS = 30;
    final int MAX_TURNS_WITHOUT_MINING = 50;

    int tryToBuildTurns = 0;
    RobotType typeToBuild;

    Miner(RobotController rc){
        this.rc = rc;
        waterManager = new WaterManager(rc);
        comm = new Comm(rc);
        danger = new Danger();
        builder = comm.checkBuilder();
        buildingZone = new BuildingZone(rc);
        explore = new ExploreMiner(rc, comm, danger, buildingZone);
        bugPath = new BugPath(rc, comm);
    }

    void play(){

        //UPDATE STUFF
        updateBeginningTurn();

        if (Constants.DEBUG == 1) System.out.println("Bytecode post bugPath update " + Clock.getBytecodeNum());

        // TRY ESCAPING FROM WATER

        boolean flee = false;
        if (bugPath.shouldFlee && WaterManager.closestSafeCell != null){
            bugPath.moveTo(WaterManager.closestSafeCell);
            flee = true;
        }

        if (Constants.DEBUG == 1) System.out.println("Bytecode post trying to flee water " + Clock.getBytecodeNum());

        coreActions();

        //CHOOSE TARGET AND MOVE

        if (!flee){
            MapLocation target;
            if (!builder) {
                target = getTarget();
            }
            else{
                target = explore.HQloc;
            }
            bugPath.moveTo(target);
        }


        //END OF TURN STUFF

        comm.readMessages();
        comm.getEnemyHQLoc();
        if (comm.wallMes != null) buildingZone.update(comm.wallMes);
        buildingZone.run();

    }

    void updateBeginningTurn(){
        if (comm.singleMessage()) comm.readMessages();
        waterManager.update();
        if (!builder) explore.updateMiner();
        else explore.updateBuilder();
        explore.checkComm();
        bugPath.update();
        if (explore.dronesFound) bugPath.updateDrones(danger);
        updateParameters();
    }

    void coreActions(){
        //TRY TO BUILD
        tryBuildNetGun();
        tryBuilding();

        //IF I CAN STAY ON MY LOCATION, TRY BUILDING REFINERY OR TRY MINING
        Direction miningDir = getMiningDir();
        if (miningDir != null && bugPath.canMoveArray[Direction.CENTER.ordinal()]) {
            tryBuildRefinery();
            tryMine(miningDir);
        }

        if (Constants.DEBUG == 1) System.out.println("Bytecode post mining " + Clock.getBytecodeNum());

        //TRY DEPOSITING

        tryDeposit();

        if (Constants.DEBUG == 1) System.out.println("Bytecode post deposit/build " + Clock.getBytecodeNum());
    }

    void updateParameters(){
        RobotType type = BuildingManager.getNextBuilding(comm);
        if (type != null && type == typeToBuild && BuildingManager.haveSoupToSpawn(rc, type)){
            ++tryToBuildTurns;
        } else tryToBuildTurns = 0;
        typeToBuild = type;
        int s = rc.getSoupCarrying();
        if (s != currentSoup) turnsWithoutMining = 0;
        else ++turnsWithoutMining;
    }

    MapLocation getTarget(){
        MapLocation ans = getBuildingTarget();
        if (ans != null) return ans;
        ans = getBestSoupTarget();
        if (ans != null) return ans;
        if (comm.latestMiningLoc != null){
            if (explore.map[comm.latestMiningLoc.x][comm.latestMiningLoc.y] == 0) return comm.latestMiningLoc;
        }
        return explore.exploreTarget();
    }

    MapLocation getBestSoupTarget(){
        if (rc.getSoupCarrying() >= rc.getType().soupLimit) return explore.closestRefineryLoc;
        if (rc.getSoupCarrying() > 0){
            if (explore.closestSoup != null) return explore.closestSoup;
            else return explore.closestRefineryLoc;
        }
        return explore.getBestTarget();
    }

    MapLocation getBuildingTarget(){
        MapLocation ans = null;
        if (rc.getRoundNum() >= Constants.MIN_TURN_BUILD_VAPORATORS){
            if (turnsWithoutMining > MAX_TURNS_WITHOUT_MINING){
                ans = getTargetForVaporator();
                if (ans != null) return ans;
                return explore.HQloc;
            }
        } else if (comm.shouldBuildOnWall()){
            ans = getCloseBuildingTarget();
        }
        return ans;
    }

    MapLocation getTargetForVaporator(){
        if (rc.getRoundNum() >= Constants.LANDSCAPERS_TO_ENEMY_HQ) return comm.enemyHQLoc;
        return comm.HQLoc;
    }

    MapLocation getCloseBuildingTarget() {
        if (explore.HQloc == null) return null;
        RobotType r = BuildingManager.getNextBuilding(comm);
        if (r != null && rc.getTeamSoup() >= r.cost){
            if (rc.getLocation().distanceSquaredTo(explore.HQloc) <= Constants.DIST_TO_BUILD) return explore.HQloc;
        }
        return null;
    }


    boolean tryMine(Direction dir){
        try {
            if (!rc.isReady()) return false;
            if (rc.getSoupCarrying() >= rc.getType().soupLimit) return false;
            rc.mineSoup(dir);
        } catch (Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    boolean tryBuildRefinery(){
        try {
            if (!rc.isReady()) return false;
            if (rc.getTeamSoup() < RobotType.REFINERY.cost) return false;
            if (explore.soupCont <= MIN_SOUP_FOR_REFINERY) return false;
            MapLocation myLoc = rc.getLocation();
            if (explore.closestRefineryLoc.distanceSquaredTo(myLoc) <= MIN_DIST_FOR_REFINERY) return false;
            build(RobotType.REFINERY);
        } catch (Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    Direction getMiningDir(){
        try {
            MapLocation myLoc = rc.getLocation();
            for (Direction dir : dirs) {
                MapLocation newLoc = myLoc.add(dir);
                if (rc.canSenseLocation(newLoc) && rc.senseSoup(newLoc) > 0 && rc.canMineSoup(dir)) {
                    return dir;
                }
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }

    boolean tryDeposit(){
        try {
            if (rc.getSoupCarrying() == 0) return false;
            if (explore.closestRefineryLoc.distanceSquaredTo(rc.getLocation()) <= 2){
                Direction dir = rc.getLocation().directionTo(explore.closestRefineryLoc);
                if (rc.canDepositSoup(dir)) rc.depositSoup(dir, rc.getSoupCarrying());
                return true;
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
        return false;

    }

    void tryBuilding(){
        if (!rc.isReady()) return;
        if (!comm.upToDate()) return;
        if (!buildingZone.finished()) return;
        if (!comm.shouldBuildOnWall() && rc.getLocation().distanceSquaredTo(explore.HQloc) > Constants.DIST_TO_BUILD) return;
        //RobotType type = BuildingManager.getNextBuilding(comm);
        if (typeToBuild == null || typeToBuild == RobotType.NET_GUN) return;
        if (!BuildingManager.haveSoupToSpawn(rc, typeToBuild)) return;
        if (Constants.DEBUG == 1) System.out.println(typeToBuild.name());
        if (rc.getTeamSoup() <= typeToBuild.cost) return;
        if (typeToBuild == RobotType.DESIGN_SCHOOL || typeToBuild == RobotType.FULFILLMENT_CENTER){
            if (shouldNotBuild(typeToBuild)) return;
        }
        build(typeToBuild);
    }

    boolean shouldNotBuild(RobotType type){
        if (comm.buildings[type.ordinal()] >= 1) return false;
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
        for (RobotInfo r : robots){
            if (r.getType() == type) return true;
        }
        return false;
    }

    void tryBuildNetGun(){
        if (!BuildingManager.haveSoupToSpawn(rc, RobotType.NET_GUN)) return;
        if (!explore.dronesFound) return;
        if (!explore.seenLandscaperMyTeam) return;
        //if (rc.getLocation().distanceSquaredTo(explore.closestDrone) > 24) return;
        build(RobotType.NET_GUN);
    }

    RobotType buildType;

    void build(RobotType type){
        try {
            buildType = type;
            myLoc = rc.getLocation();
            buildOnWall = comm.shouldBuildOnWall();

            if (Constants.DEBUG == 2) System.out.println("Trying to build " + type.name() + " " + buildOnWall);

            BuildingSpot bestBuildingSpot = null;

            BuildingSpot s1 = new BuildingSpot(Direction.NORTH);
            BuildingSpot s2 = new BuildingSpot(Direction.NORTHEAST);
            BuildingSpot s3 = new BuildingSpot(Direction.EAST);
            BuildingSpot s4 = new BuildingSpot(Direction.SOUTHEAST);
            BuildingSpot s5 = new BuildingSpot(Direction.SOUTH);
            BuildingSpot s6 = new BuildingSpot(Direction.SOUTHWEST);
            BuildingSpot s7 = new BuildingSpot(Direction.WEST);
            BuildingSpot s8 = new BuildingSpot(Direction.NORTHWEST);

            if (s1.isBetter(bestBuildingSpot)) bestBuildingSpot = s1;
            if (s2.isBetter(bestBuildingSpot)) bestBuildingSpot = s2;
            if (s3.isBetter(bestBuildingSpot)) bestBuildingSpot = s3;
            if (s4.isBetter(bestBuildingSpot)) bestBuildingSpot = s4;
            if (s5.isBetter(bestBuildingSpot)) bestBuildingSpot = s5;
            if (s6.isBetter(bestBuildingSpot)) bestBuildingSpot = s6;
            if (s7.isBetter(bestBuildingSpot)) bestBuildingSpot = s7;
            if (s8.isBetter(bestBuildingSpot)) bestBuildingSpot = s8;

            if (bestBuildingSpot != null){
                rc.setIndicatorDot(bestBuildingSpot.loc, 255, 255, 255);
                System.out.println("Best spot " + bestBuildingSpot.score() + " " + bestBuildingSpot.zone);
            }

            if (comm.shouldBuildVaporators() && bestBuildingSpot != null && buildType == RobotType.NET_GUN){
                int vaps = comm.buildings[RobotType.VAPORATOR.ordinal()];
                int nets = comm.buildings[RobotType.NET_GUN.ordinal()];
                if (vaps < 2*nets){
                    switch(bestBuildingSpot.zone){
                        case BuildingZone.WALL:
                        case BuildingZone.OUTER_WALL:
                        case BuildingZone.HOLE: return;
                    }
                }
            }

            if (bestBuildingSpot != null && rc.canBuildRobot(buildType, bestBuildingSpot.dir)){
                if (bestBuildingSpot.score() >= 2) {
                    rc.buildRobot(buildType, bestBuildingSpot.dir);
                    comm.sendMessage(comm.BUILDING_TYPE, type.ordinal());
                } else if (bestBuildingSpot.score() >= 1){
                    if (tryToBuildTurns >= MAX_BUILD_TURNS){
                        rc.buildRobot(buildType, bestBuildingSpot.dir);
                        comm.sendMessage(comm.BUILDING_TYPE, type.ordinal());
                    }
                } else{
                    if (tryToBuildTurns >= DESPERATE_TURNS && type != RobotType.NET_GUN){
                        rc.buildRobot(buildType, bestBuildingSpot.dir);
                        comm.sendMessage(comm.BUILDING_TYPE, type.ordinal());
                    }
                }
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    class BuildingSpot{

        Direction dir;
        MapLocation loc;
        int zone;
        boolean flooded;
        boolean canBuild;
        int height;
        int effectiveHeight;
        int distToEnemyHQ = Constants.INF;

        int score = -1;

        BuildingSpot(Direction dir){
            try {
                this.dir = dir;
                loc = myLoc.add(dir);
                if (rc.canSenseLocation(loc)) {
                    canBuild = rc.canBuildRobot(buildType, dir);
                    zone = buildingZone.getZone(loc);
                    flooded = rc.senseFlooding(loc);
                    height = rc.senseElevation(loc);
                    effectiveHeight = height;
                    if (effectiveHeight > MAX_ELEVATION) effectiveHeight = MAX_ELEVATION;
                    if (loc.distanceSquaredTo(buildingZone.HQloc) <= 2) effectiveHeight = MAX_ELEVATION;
                    if (comm.enemyHQLoc != null) distToEnemyHQ = loc.distanceSquaredTo(comm.enemyHQLoc);
                }
            } catch (Throwable t){
                t.printStackTrace();
            }

        }

        int score(){
            if (score >= 0) return score;
            switch(buildType) {
                case DESIGN_SCHOOL:
                case VAPORATOR:
                    if (buildOnWall){
                        switch(zone){
                            case BuildingZone.OUTER_WALL:
                                score = 0;
                                if (buildingZone.canBuild(loc) && height >= Constants.WALL_HEIGHT) score = 2;
                                else if (height >= Constants.WALL_HEIGHT) score = 1;
                                return score;
                            case BuildingZone.BUILDING_AREA:
                            case BuildingZone.NEXT_TO_WALL:
                                score = 1;
                                return score;
                            default:
                                score = 0;
                                return score;
                        }
                    } else{
                        switch (zone) {
                            case BuildingZone.BUILDING_AREA:
                                score = 2;
                                return score;
                            case BuildingZone.NEXT_TO_WALL:
                                score = 1;
                                return score;
                            default:
                                score = 0;
                                return score;
                        }
                    }
                case FULFILLMENT_CENTER:
                    if (buildOnWall){
                        switch(zone){
                            case BuildingZone.OUTER_WALL:
                                score = 0;
                                if (buildingZone.canBuild(loc) && height >= Constants.WALL_HEIGHT) score = 2;
                                else if (height >= Constants.WALL_HEIGHT) score = 1;
                                return score;
                            case BuildingZone.BUILDING_AREA:
                            case BuildingZone.NEXT_TO_WALL:
                                score = 1;
                                return score;
                            default:
                                score = 0;
                                return score;
                        }
                    } else{
                        switch (zone) {
                            case BuildingZone.BUILDING_AREA:
                                score = 1;
                                return score;
                            case BuildingZone.NEXT_TO_WALL:
                                score = 2;
                                return score;
                            default:
                                score = 0;
                                return score;
                        }
                    }
                case NET_GUN:
                    if (buildOnWall){
                        switch(zone){
                            case BuildingZone.WALL:
                            case BuildingZone.OUTER_WALL:
                                score = 0;
                                if (height >= Constants.WALL_HEIGHT){
                                    int t = explore.minDistNetGun[dir.ordinal()];
                                    if (t == 0 || t >= 8 || (closeToEnemyHQ() && t >= 5)) {
                                        score = 2;
                                    }
                                }
                                return score;
                            case BuildingZone.NEXT_TO_WALL:
                                int t = explore.minDistNetGun[dir.ordinal()];
                                if (t == 0 || t >= 5) {
                                    score = 2;
                                }
                                return score;
                            default:
                                score = 0;
                                return score;
                        }
                    } else{
                        switch (zone) {
                            case BuildingZone.NEXT_TO_WALL:
                                int t = explore.minDistNetGun[dir.ordinal()];
                                if (t == 0 || t >= 5) {
                                    score = 2;
                                }
                                return score;
                            default:
                                score = 0;
                                return score;
                        }
                    }
                default:
                    score = 2;
                    return score;
            }
        }

        boolean closeToEnemyHQ(){
            if (comm.enemyHQLoc == null) return false;
            return comm.enemyHQLoc.distanceSquaredTo(loc) <= Constants.ATTACK_DISTANCE;
        }

        boolean isBetter(BuildingSpot s){
            if (!canBuild) return false;
            if (buildType == RobotType.NET_GUN && danger.minDist[dir.ordinal()] > 13) return false;
            if (s == null) return true;
            if (score() == 0) return false;
            if (s.score() == 0) return true;
            if (buildType == RobotType.NET_GUN){
                int d1 = explore.minDistNetGun[dir.ordinal()], d2 = explore.minDistNetGun[s.dir.ordinal()];
                if (d1 != d2){
                    if (d1 == 0) return true;
                    if (d2 == 0) return false;
                    if (d1 > d2) return true;
                    if (d2 < d1) return false;
                }
                if (danger.minDist[dir.ordinal()] < danger.minDist[s.dir.ordinal()]) return true;
                if (danger.minDist[dir.ordinal()] > danger.minDist[s.dir.ordinal()]) return false;
            }
            if (effectiveHeight > s.effectiveHeight) return true;
            if (s.effectiveHeight > effectiveHeight) return false;
            if (score() == s.score()) return height > s.height;
            return (score() > s.score());
        }

    }


}
