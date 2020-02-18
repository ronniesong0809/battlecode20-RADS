package finalbota;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.HashSet;

public class BugPath {

    RobotController rc;
    Comm comm;

    BugPath(RobotController rc, Comm comm){
        this.rc = rc;
        this.comm = comm;
    }

    Boolean rotateRight = null; //if I should rotate right or left
    Boolean rotateRightAux = null;
    MapLocation lastObstacleFound = null; //latest obstacle I've found in my way
    int minDistToTarget = Constants.INF; //minimum distance I've been to the enemy while going around an obstacle
    MapLocation minLocationToTarget = null;
    MapLocation prevTarget = null; //previous target
    Direction[] dirs = Direction.values();
    boolean shouldFlee = false;
    boolean surroundedByWater = false;
    int turnsFleeing = 0;
    HashSet<Integer> states = new HashSet<>();

    MapLocation myLoc;
    boolean[] canMoveArray;
    boolean[] flooded;
    int round;

    int turnsMovingToObstacle = 0;
    final int MAX_TURNS_MOVING_TO_OBSTACLE = 2;

    final int MIN_DIST_RESET = 3;
    int[] minDists;
    boolean[] safe;

    void update(){
        if (!rc.isReady()) return;
        myLoc = rc.getLocation();
        round = rc.getRoundNum();
        updateArray();
        checkFleeStatus();
        //if (Constants.DEBUG == 1) debugMovement();
    }

    void updateDrones(Danger danger){
        if (!rc.isReady()) return;
        this.minDists = danger.minDist;
        this.safe = danger.safe;
        checkArrayWithDrones();
    }

    void debugMovement(){
        try{
            for (Direction dir : dirs){
                MapLocation newLoc = myLoc.add(dir);
                if (rc.canSenseLocation(newLoc) && canMoveArray[dir.ordinal()]) rc.setIndicatorDot(newLoc, 0, 0, 255);
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    void moveTo(MapLocation target){
        //No target? ==> bye!
        if (!rc.isReady()) return;
        if (target == null) target = rc.getLocation();
        if (Constants.DEBUG == 1) rc.setIndicatorLine(rc.getLocation(), target, 255, 255, 255);
        //if (target == null) return;


        //different target? ==> previous data does not help!
        if (prevTarget == null){
            if (Constants.DEBUG_BUGPATH == 1) System.out.println("Previous target is null! reset!");
            resetPathfinding();
            rotateRight = null;
            rotateRightAux = null;
        }


        else {
            int distTargets = target.distanceSquaredTo(prevTarget);
            if (distTargets > 0) {
                if (Constants.DEBUG_BUGPATH == 1) System.out.println("Different target!! Reset!");
                if (distTargets >= MIN_DIST_RESET){
                    rotateRight = null;
                    rotateRightAux = null;
                    resetPathfinding();
                }
                else{
                    if (Constants.DEBUG_BUGPATH == 1) System.out.println("Different target!! Soft Reset!");
                    softReset(target);
                }
            }
        }

        //Update data
        prevTarget = target;

        checkState();

        myLoc = rc.getLocation();


        //If I'm at a minimum distance to the target, I'm free!
        int d = myLoc.distanceSquaredTo(target);
        if (d == 0){
            if (canMoveArray[Direction.CENTER.ordinal()]) return;
            moveSafe();
            return;
        }
        if (d < minDistToTarget){
            if (Constants.DEBUG_BUGPATH == 1) System.out.println("resetting on d < mindist");
            resetPathfinding();
            minDistToTarget = d;
            minLocationToTarget = myLoc;
        }

        //If there's an obstacle I try to go around it [until I'm free] instead of going to the target directly
        Direction dir = myLoc.directionTo(target);
        if (lastObstacleFound == null){
            if (tryGreedyMove()){
                if (Constants.DEBUG_BUGPATH == 1) System.out.println("No obstacle and could move greedily :)");
                resetPathfinding();
                return;
            }
        }
        else{
            dir = myLoc.directionTo(lastObstacleFound);
            rc.setIndicatorDot(lastObstacleFound, 0, 0, 0);
        }

        try {

            if (canMoveArray[dir.ordinal()]){
                myMove(dir);
                if (lastObstacleFound != null) {
                    if (Constants.DEBUG_BUGPATH == 1) System.out.println("Could move to obstacle?!");
                    ++turnsMovingToObstacle;
                    lastObstacleFound = rc.getLocation().add(dir);
                    if (turnsMovingToObstacle >= MAX_TURNS_MOVING_TO_OBSTACLE){
                        if (Constants.DEBUG_BUGPATH == 1) System.out.println("obstacle reset!!");
                        resetPathfinding();
                    } else if (!rc.onTheMap(lastObstacleFound)){
                        if (Constants.DEBUG_BUGPATH == 1) System.out.println("obstacle reset!! - out of the map");
                        resetPathfinding();
                    }
                }
                return;
            } else turnsMovingToObstacle = 0;

            checkRotate(dir);

            if (Constants.DEBUG_BUGPATH == 1) System.out.println(rotateRight + " " + dir.name());

            //I rotate clockwise or counterclockwise (depends on 'rotateRight'). If I try to go out of the map I change the orientation
            //Note that we have to try at most 16 times since we can switch orientation in the middle of the loop. (It can be done more efficiently)
            int i = 16;
            while (i-- >= 0) {
                MapLocation newLoc = myLoc.add(dir);
                if (canMoveArray[dir.ordinal()]) {
                    myMove(dir);
                    return;
                }
                if (!rc.onTheMap(newLoc)) rotateRight = !rotateRight;
                    //If I could not go in that direction and it was not outside of the map, then this is the latest obstacle found
                else lastObstacleFound = newLoc;
                if (rotateRight) dir = dir.rotateRight();
                else dir = dir.rotateLeft();
            }

            if (canMoveArray[dir.ordinal()]){
                myMove(dir);
                return;
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    void myMove(Direction dir){
        try {
            if (dir == Direction.CENTER) return;
            rc.move(dir);
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    void updateArray(){
        canMoveArray = new boolean[9];
        flooded = new boolean[9];
        surroundedByWater = false;
        try {
            boolean foundFlooding = false;
            boolean blind = rc.getCurrentSensorRadiusSquared() < 2;
            boolean canMove = false;
            for (Direction dir : dirs) {
                if (blind){
                    if (dir == Direction.CENTER || rc.canMove(dir)) {
                        canMoveArray[dir.ordinal()] = true;
                        canMove = true;
                    }
                }
                else {
                    MapLocation newLoc = myLoc.add(dir);
                    if (!rc.canSenseLocation(newLoc)) continue;
                    if (isFlooded(newLoc)) {
                        flooded[dir.ordinal()] = true;
                        foundFlooding = true;
                    }
                    if ((dir == Direction.CENTER || rc.canMove(dir)) && !rc.senseFlooding(newLoc) && !flooded[dir.ordinal()]){
                        canMoveArray[dir.ordinal()] = true;
                        canMove = true;
                    }
                }
            }
            if (!canMove && !blind){
                for (Direction dir : dirs){
                    MapLocation newLoc = myLoc.add(dir);
                    if ((dir == Direction.CENTER || rc.canMove(dir)) && !rc.senseFlooding(newLoc)) canMoveArray[dir.ordinal()] = true;
                }
            }
            if (foundFlooding) checkFlee();
        } catch (Throwable t){
            t.printStackTrace();
        }

    }

    void checkArrayWithDrones(){
        if (minDists == null){
            //if (Constants.DEBUG == 1) System.out.println("THIS SHOULDNT HAPPEN");
            return;
        }
        int maxMinDist = 0;
        int i = 9;
        while (--i >= 0){
            if (!canMoveArray[i]) continue;
            int dist = minDists[i];
            if (safe[i]) dist = Constants.INF;
            if (dist > maxMinDist) maxMinDist = minDists[i];
        }

        int minDistFlee = Constants.MIN_DIST_FLEE;
        if (attacking()) minDistFlee = Constants.MIN_DIST_FLEE_ATTACKING;

        if (maxMinDist >= minDistFlee) maxMinDist = minDistFlee;
        //if (Constants.DEBUG == 1) System.out.println("MAXMIN DIST " + maxMinDist);
        i = 9;
        while (--i >= 0){
            if (canMoveArray[i] && !safe[i] && minDists[i] < maxMinDist){
                canMoveArray[i] = false;
                //if (Constants.DEBUG == 1) System.out.println("Cant move!! " + dirs[i].name());
            }
        }
    }

    boolean attacking(){
        if (comm.enemyHQLoc == null) return false;
        return comm.enemyHQLoc.distanceSquaredTo(rc.getLocation()) <= Constants.ATTACK_DISTANCE;
    }

    void moveSafe(){
        resetPathfinding();
        int i = 9;
        while (--i >= 0){
            if (canMoveArray[i]){
                myMove(dirs[i]);
                return;
            }
        }
    }

    void checkFlee(){
        try {
            if (!shouldFlee) {
                if (rc.senseElevation(myLoc) <= WaterManager.waterLevelPlus){
                    shouldFlee = true;
                    turnsFleeing = 0;
                }
            }
        } catch(Throwable t){
            t.printStackTrace();
        }
    }

    void checkFleeStatus(){
        try {
            if (shouldFlee) {
                if (rc.senseElevation(myLoc) > WaterManager.waterLevelPlus){
                    if (turnsFleeing >= WaterManager.MIN_SAFE_TURNS){
                        //if (surroundedByWater && turnsFleeing < WaterManager.MIN_SAFE_TURNS) turnsFleeing = WaterManager.MIN_SAFE_TURNS-1;
                        shouldFlee = false;
                        turnsFleeing = 0;
                    } else turnsFleeing++;
                }
            }
        } catch(Throwable t){
            t.printStackTrace();
        }
    }

    boolean isFlooded (MapLocation loc){
        try {
            if (rc.senseElevation(loc) > WaterManager.waterLevelPlus) return false;
            MapLocation newLoc = loc.add(Direction.NORTH);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.NORTHEAST);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.EAST);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.SOUTHEAST);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.SOUTH);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.SOUTHWEST);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.WEST);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
            newLoc = loc.add(Direction.NORTHWEST);
            if (rc.canSenseLocation(newLoc) && rc.senseFlooding(newLoc)) return true;
        } catch (Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    boolean tryGreedyMove(){
        try {
            if (rotateRightAux != null) return false;
            MapLocation myLoc = rc.getLocation();
            Direction dir = myLoc.directionTo(prevTarget);
            if (canMoveArray[dir.ordinal()]) {
                myMove(dir);
                return true;
            }
            int dist = myLoc.distanceSquaredTo(prevTarget);
            int dist1 = Constants.INF, dist2 = Constants.INF;
            Direction dir1 = dir.rotateRight();
            MapLocation newLoc = myLoc.add(dir1);
            if (canMoveArray[dir1.ordinal()]) dist1 = newLoc.distanceSquaredTo(prevTarget);
            Direction dir2 = dir.rotateLeft();
            newLoc = myLoc.add(dir2);
            if (canMoveArray[dir2.ordinal()]) dist2 = newLoc.distanceSquaredTo(prevTarget);
            if (dist1 < dist && dist1 < dist2) {
                rotateRightAux = true;
                myMove(dir1);
                return true;
            }
            if (dist2 < dist && dist2 < dist1) {
                rotateRightAux = false;
                myMove(dir2);
                return true;
            }
        } catch(Throwable t){
            t.printStackTrace();
        }
        return false;
    }

    //TODO: check remaining cases
    //TODO: move obstacle if can move to obstacle lol
    void checkRotate(Direction dir){
        if (rotateRight != null) return;
        /*if (rotateRightAux != null){
            rotateRight = rotateRightAux;
            return;
        }*/
        Direction dirLeft = dir;
        Direction dirRight = dir;
        int i = 8;
        while (--i >= 0) {
            if (!canMoveArray[dirLeft.ordinal()]) dirLeft = dirLeft.rotateLeft();
            else break;
        }
        i = 8;
        while (--i >= 0){
            if (!canMoveArray[dirRight.ordinal()]) dirRight = dirRight.rotateRight();
            else break;
        }
        int distLeft = myLoc.add(dirLeft).distanceSquaredTo(prevTarget), distRight = myLoc.add(dirRight).distanceSquaredTo(prevTarget);
        if (distRight < distLeft) rotateRight = true;
        else rotateRight = false;
    }

    //clear some of the previous data
    void resetPathfinding(){
        if (Constants.DEBUG_BUGPATH == 1) System.out.println("reset!");
        lastObstacleFound = null;
        minDistToTarget = Constants.INF;
        states = new HashSet<>();
        turnsMovingToObstacle = 0;
    }

    void softReset(MapLocation target){
        if (Constants.DEBUG_BUGPATH == 1) System.out.println("soft reset!");
        if (minLocationToTarget != null) minDistToTarget = minLocationToTarget.distanceSquaredTo(target);
        else resetPathfinding();
    }

    void checkState(){
        if (lastObstacleFound == null) return;
        int state = (myLoc.x << 8) | (myLoc.y << 2) | (lastObstacleFound.x << 20) | (lastObstacleFound.y << 14);
        if (rotateRight != null) {
            if (rotateRight == true) state = state | 1;
            if (rotateRight == false) state = state | 2;
        }
        if (states.contains(state)){
            if (Constants.DEBUG == 1) System.out.println("REPEATED STATE! RESET");
            resetPathfinding();
        }
        states.add(state);
    }

}
