package finalbota;

import battlecode.common.*;

public class Landscaper extends MyRobot {

    RobotController rc;
    Comm comm;
    ExploreLandscaper exploreLandscaper;
    BugPath bugPath;
    WaterManager waterManager;
    BuildingZone buildingZone;
    Danger danger;
    MapLocation myLoc;

    Direction[] dirs = Direction.values();

    AdjacentSpot bestSpotDig, bestSpotDeposit;
    int sight;
    boolean spareDirt;
    boolean full;
    boolean alert = false;
    int myDirt;
    MapLocation target;

    Landscaper(RobotController rc){
        this.rc = rc;
        comm = new Comm(rc);
        danger = new Danger();
        waterManager = new WaterManager(rc);
        buildingZone = new BuildingZone(rc);
        exploreLandscaper = new ExploreLandscaper(rc, comm, danger, buildingZone);
        bugPath = new BugPath(rc, comm);
    }

    void play(){

        //UPDATE STUFF
        updateBeginningTurn();

        //TRY FLEEING FROM WATER

        boolean flee = false;
        if (bugPath.shouldFlee && WaterManager.closestSafeCell != null){
            bugPath.moveTo(WaterManager.closestSafeCell);
            flee = true;
        }
        if (Constants.DEBUG == 1) System.out.println("Bytecode post trying to flee water " + Clock.getBytecodeNum());

        //CHECK URGENCT MOVES: flooded cells, hurt buildings, enemy buildings, etc.

        target = null;
        checkUrgentMoves();
        if (target != null) bugPath.moveTo(target);

        //TRY DEPOSITING ON ENEMY BUILDING

        tryBury();

        //TRY DIGGING AND DEPOSITING (GENERAL)

        tryDigAndDeposit();


        //IF NOT FLEE GO TO TARGET
        if (!flee && target == null){
            target = getTarget();
            bugPath.moveTo(target);
        }

        //END OF TURN STUFF

        if (comm.wallMes != null) buildingZone.update(comm.wallMes);
        buildingZone.run();
        comm.readMessages();
    }

    void debugDigDeposit(){
        if (Constants.DEBUG == 1){
            if (bestSpotDig != null){
                System.out.println("Best Spot Dig " + bestSpotDig.scoreDig());
                rc.setIndicatorDot(bestSpotDig.loc, 255, 0, 0);
            }
            if (bestSpotDeposit != null){
                System.out.println("Best Spot deposit " + bestSpotDeposit.scoreDeposit());
                rc.setIndicatorDot(bestSpotDeposit.loc, 0, 255, 0);
            }
        }
    }

    void updateBeginningTurn(){
        if (comm.singleMessage()) comm.readMessages();
        waterManager.update();
        exploreLandscaper.update();
        exploreLandscaper.checkComm();
        bugPath.update();
        if (exploreLandscaper.dronesFound) bugPath.updateDrones(danger);
        if (Constants.DEBUG == 1) System.out.println("Bytecode post bugPath update " + Clock.getBytecodeNum());
    }

    MapLocation getTarget(){
        if (!rc.isReady()) return null;
        if (exploreLandscaper.closestEnemyBuilding != null) return exploreLandscaper.closestEnemyBuilding;
        if ((comm.HQLoc == null || !comm.wallFinished || rc.getRoundNum() <= Constants.MIN_TURN_GO_TO_ENEMY) && exploreLandscaper.closestWallToBuild != null) return exploreLandscaper.closestWallToBuild;
        MapLocation enemyHQ = comm.getEnemyHQLoc();
        if (enemyHQ != null) return enemyHQ;
        MapLocation loc = getBestGuess();
        return loc;
    }

    void checkUrgentMoves() {
        try {
            if (!rc.isReady()) return;
            target = null;
            buildingHurtMove();
            closeEnemyBuildingMove();
            urgentFixMove();

            if (target != null) return;
            if (exploreLandscaper.closestWallToBuild == null) return;
            if (buildingZone.isWall(rc.getLocation())) return;
            bugPath.moveTo(exploreLandscaper.closestWallToBuild);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    boolean buildingHurtMove(){
        try {
            if (exploreLandscaper.buildingHurt == null) return false;
            if (rc.getDirtCarrying() >= RobotType.LANDSCAPER.dirtLimit && exploreLandscaper.closestEnemyBuilding != null) return false;
            int d = rc.getLocation().distanceSquaredTo(exploreLandscaper.buildingHurt);
            if (d <= 2) {
                if (rc.isReady()) {
                    Direction dir = rc.getLocation().directionTo(exploreLandscaper.buildingHurt);
                    if (rc.canDigDirt(dir)) {
                        rc.digDirt(dir);
                    }
                    forceDeposit();
                }
                target = rc.getLocation();
                return true;
            }
            if (target != null) return false;
            if (!comm.isRush() && buildingZone.finished() && buildingZone.isWall(rc.getLocation())) return false;
            target = exploreLandscaper.buildingHurt;
            return true;
        } catch(Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    boolean closeEnemyBuildingMove(){
        try {
            if (exploreLandscaper.closestEnemyBuilding == null) return false;
            int d = rc.getLocation().distanceSquaredTo(exploreLandscaper.closestEnemyBuilding);
            if (d <= 2) {
                if (rc.isReady()) {
                    Direction dir = rc.getLocation().directionTo(exploreLandscaper.closestEnemyBuilding);
                    if (rc.canDepositDirt(dir)) {
                        rc.depositDirt(dir);
                    }
                }
                target = rc.getLocation();
                return true;
            }
            if (target != null) return false;
            if (!comm.isRush() && buildingZone.finished() && buildingZone.isWall(rc.getLocation())) return false;
            if (buildingZone.finished() && !buildingZone.isCritical(exploreLandscaper.closestEnemyBuilding)) return false;
            target = exploreLandscaper.closestEnemyBuilding;
            return true;
        } catch(Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    boolean urgentFixMove(){
        try {
            if (exploreLandscaper.urgentFix == null) return false;
            if (target != null) return false;
            int d = rc.getLocation().distanceSquaredTo(exploreLandscaper.urgentFix);
            if (d <= 2) {
                target = rc.getLocation();
                return true;
            }
            if (!buildingZone.finished()) return false;
            if (buildingZone.isWall(rc.getLocation())) return false;
            target = exploreLandscaper.urgentFix;
            return true;
        } catch(Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    MapLocation getBestGuess(){
        MapLocation ans = null;
        int bestDist = 0;
        MapLocation myLoc = rc.getLocation();
        MapLocation hor = comm.getHorizontal(), ver = comm.getVertical(), rot = comm.getRotational();
        if (hor != null){
            int t = myLoc.distanceSquaredTo(hor);
            if (ans == null || t < bestDist){
                ans = hor;
                bestDist = t;
            }
        }
        if (ver != null){
            int t = myLoc.distanceSquaredTo(ver);
            if (ans == null || t < bestDist){
                ans = ver;
                bestDist = t;
            }
        }
        if (rot != null){
            int t = myLoc.distanceSquaredTo(rot);
            if (ans == null || t < bestDist){
                ans = rot;
                bestDist = t;
            }
        }
        return ans;
    }

    void tryBury(){
        try {
            if (!rc.isReady()) return;
            if (exploreLandscaper.closestEnemyBuilding == null) return;
            if (exploreLandscaper.closestEnemyBuilding.distanceSquaredTo(rc.getLocation()) <= 2){
                Direction dir = rc.getLocation().directionTo(exploreLandscaper.closestEnemyBuilding);
                if (rc.canDepositDirt(dir)) rc.depositDirt(dir);
                return;
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    void forceDeposit(){
        try{
            if (!rc.isReady()) return;
            int minHeight = 0;
            Direction bestDir = null;
            for (Direction dir : dirs){
                MapLocation loc = rc.getLocation().add(dir);
                if (!rc.canDepositDirt(dir)) continue;
                if (!rc.canSenseLocation(loc)) continue;
                RobotInfo r = rc.senseRobotAtLocation(loc);
                if (r != null && r.getTeam() == rc.getTeam() && r.type.isBuilding()) continue;
                int e = rc.senseElevation(loc);
                if (bestDir == null || e < minHeight){
                    bestDir = dir;
                    minHeight = e;
                }
            }
            if (bestDir != null){
                rc.depositDirt(bestDir);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    boolean shouldBuildInnerWall(){
        if (rc.getRoundNum() > Constants.INNER_WALL_TURN) return false;
        if (comm.wallFinished) return false;
        return true;
    }

    void tryDigAndDeposit(){
        try {
            if (!rc.isReady()) return;
            if (!buildingZone.finished()) return;
            if (!bugPath.canMoveArray[Direction.CENTER.ordinal()]) return;
            bestSpotDig = null; bestSpotDeposit = null;
            myLoc = rc.getLocation();
            sight = rc.getCurrentSensorRadiusSquared();
            spareDirt = rc.getDirtCarrying() > 1;
            full = rc.getDirtCarrying() >= RobotType.LANDSCAPER.dirtLimit;
            alert = false;
            myDirt = rc.getDirtCarrying();


            AdjacentSpot s1 = new AdjacentSpot(Direction.NORTH);
            AdjacentSpot s2 = new AdjacentSpot(Direction.NORTHEAST);
            AdjacentSpot s3 = new AdjacentSpot(Direction.EAST);
            AdjacentSpot s4 = new AdjacentSpot(Direction.SOUTHEAST);
            AdjacentSpot s5 = new AdjacentSpot(Direction.SOUTH);
            AdjacentSpot s6 = new AdjacentSpot(Direction.SOUTHWEST);
            AdjacentSpot s7 = new AdjacentSpot(Direction.WEST);
            AdjacentSpot s8 = new AdjacentSpot(Direction.NORTHWEST);
            AdjacentSpot s0 = new AdjacentSpot(Direction.CENTER);

            if (s0.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s0;
            if (s1.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s1;
            if (s2.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s2;
            if (s3.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s3;
            if (s4.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s4;
            if (s5.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s5;
            if (s6.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s6;
            if (s7.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s7;
            if (s8.isBetterDepositTargetThan(bestSpotDeposit)) bestSpotDeposit = s8;

            if (rc.getDirtCarrying() > 0){
                if (bestSpotDeposit != null){
                    if (bestSpotDeposit.scoreDeposit() <= WALL_LOW) {
                        rc.depositDirt(bestSpotDeposit.dir);
                        return;
                    }
                }
            }

            if (!full) {
                if (s8.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s8;
                if (s7.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s7;
                if (s6.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s6;
                if (s5.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s5;
                if (s4.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s4;
                if (s3.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s3;
                if (s2.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s2;
                if (s1.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s1;
                if (s0.isBetterDiggingTargetThan(bestSpotDig)) bestSpotDig = s0;


                if (bestSpotDig != null){
                    if (rc.getDirtCarrying() > 0){
                        if (exploreLandscaper.urgentFix == null && bestSpotDig.scoreDig() >= HOLE){
                            rc.digDirt(bestSpotDig.dir);
                            return;
                        }
                    } else{
                        rc.digDirt(bestSpotDig.dir);
                    }
                }
            }

        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    static final int FLOODED_INTERIOR = 0;
    static final int FLOODED_NEXT_TO_WALL = 1;
    static final int FLOODED_WALL = 2;
    static final int FLOODED_OUTER_WALL = 3;
    //static final int FLOODED_HOLE = 4;
    static final int WALL_LOW = 5;
    static final int BUILDING_AREA = 6;
    static final int NEXT_TO_WALL = 7;
    static final int HOLE = 8;
    static final int WALL_HIGH = 10;
    static final int WALL_SUPER_HIGH = 9;


    class AdjacentSpot {

        Direction dir;
        boolean canDig, canDeposit;
        int zone;
        MapLocation loc;
        int elevation;
        boolean flooded;
        boolean waterAdj;

        int scoreDig = -1, scoreDeposit = -1;

        //TODO: what if can't sense?
        AdjacentSpot(Direction dir){
            try {
                this.dir = dir;
                loc = myLoc.add(dir);
                if (rc.canSenseLocation(loc)){
                    canDig = rc.canDigDirt(dir);
                    if (exploreLandscaper.cantDig[dir.ordinal()]) canDig = false;
                    canDeposit = rc.canDepositDirt(dir);
                    zone = buildingZone.getZone(loc);
                    elevation = rc.senseElevation(loc);
                    flooded = rc.senseFlooding(loc);
                    RobotInfo r = rc.senseRobotAtLocation(loc);
                    if (r != null && r.getType().isBuilding()) elevation += r.getDirtCarrying();
                    if (sight >= 8){
                        //if (Constants.DEBUG == 1) System.out.println("Before compute Adj: " + Clock.getBytecodeNum());
                        computeWaterAdj();
                        //if (Constants.DEBUG == 1) System.out.println("After compute Adj: " + Clock.getBytecodeNum());
                    }

                }
            } catch(Throwable t){
                t.printStackTrace();
            }
        }

        boolean flood(int e){
            if (flooded || waterAdj && e < WaterManager.waterLevel + waterManager.safetyWall()) return true;
            return false;
        }

        int computeScore(int e, int d){
            if (e < Constants.MIN_DEPTH && zone != BuildingZone.BUILDING_AREA && zone != BuildingZone.NEXT_TO_WALL && zone != BuildingZone.WALL){
                return HOLE;
            }
            switch(zone){
                case BuildingZone.BUILDING_AREA:
                    if (flood(e)) return FLOODED_INTERIOR;
                    return BUILDING_AREA;
                case BuildingZone.NEXT_TO_WALL:
                    if (flood(e)) return FLOODED_NEXT_TO_WALL;
                    return NEXT_TO_WALL;
                case BuildingZone.HOLE:
                    /*if (myDirt > d) {
                        if (flood(e)) return FLOODED_HOLE;
                        return HOLE;
                    }*/
                    return HOLE;
                case BuildingZone.OUTER_WALL:
                    if (e < Constants.WALL_HEIGHT){
                        if (flood(e)) return FLOODED_OUTER_WALL;
                        return WALL_LOW;
                    }
                    else if (e > Constants.WALL_HEIGHT + Constants.MAX_DIFF_HEIGHT) return WALL_SUPER_HIGH;
                    return WALL_HIGH;
                case BuildingZone.WALL:
                default:
                    if (e < Constants.WALL_HEIGHT){
                        if (flood(e)) return FLOODED_WALL;
                        return WALL_LOW;
                    }
                    else if (e > Constants.WALL_HEIGHT + Constants.MAX_DIFF_HEIGHT) return WALL_SUPER_HIGH;
                    return WALL_HIGH;
            }
        }



        int scoreDig(){
            if (scoreDig < 0) scoreDig = computeScore(elevation-1, 0);
            return scoreDig;
        }

        int scoreDeposit(){
            if (scoreDeposit < 0) scoreDeposit = computeScore(elevation, 1);
            return scoreDeposit;
        }

        void computeWaterAdj(){
            try {
                MapLocation newLoc = loc.add(Direction.NORTH);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.NORTHEAST);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.EAST);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.SOUTHEAST);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.SOUTH);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.SOUTHWEST);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.WEST);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }
                newLoc = loc.add(Direction.NORTHWEST);
                if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) {
                    waterAdj = true;
                    return;
                }

            } catch(Throwable t){
                t.printStackTrace();
            }
        }



        boolean isBetterDiggingTargetThan(AdjacentSpot d){
            if (!canDig) return false;
            if (d == null) return true;
            return scoreDig() > d.scoreDig();
        }

        boolean isBetterDepositTargetThan(AdjacentSpot d){
            if (!canDeposit) return false;
            if (d == null) return true;
            return scoreDeposit() < d.scoreDeposit();
        }


    }


}
