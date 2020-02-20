package finalbota;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class Danger {

    int minDist[];
    boolean[] safe;
    MapLocation myLoc;

    final int MAX_SAFE_DIST = 5;

    Danger(){

    }

    void init(MapLocation myLoc){
        this.myLoc = myLoc;
        minDist = new int[9];
        minDist[0] = Constants.INF;
        minDist[1] = Constants.INF;
        minDist[2] = Constants.INF;
        minDist[3] = Constants.INF;
        minDist[4] = Constants.INF;
        minDist[5] = Constants.INF;
        minDist[6] = Constants.INF;
        minDist[7] = Constants.INF;
        minDist[8] = Constants.INF;
        safe = new boolean[9];
    }

    void addDanger(MapLocation loc){
        MapLocation newLoc = myLoc.add(Direction.NORTH);
        int d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.NORTH.ordinal()] > d) minDist[Direction.NORTH.ordinal()] = d;
        newLoc = myLoc.add(Direction.NORTHWEST);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.NORTHWEST.ordinal()] > d) minDist[Direction.NORTHWEST.ordinal()] = d;
        newLoc = myLoc.add(Direction.WEST);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.WEST.ordinal()] > d) minDist[Direction.WEST.ordinal()] = d;
        newLoc = myLoc.add(Direction.SOUTHWEST);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.SOUTHWEST.ordinal()] > d) minDist[Direction.SOUTHWEST.ordinal()] = d;
        newLoc = myLoc.add(Direction.SOUTH);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.SOUTH.ordinal()] > d) minDist[Direction.SOUTH.ordinal()] = d;
        newLoc = myLoc.add(Direction.SOUTHEAST);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.SOUTHEAST.ordinal()] > d) minDist[Direction.SOUTHEAST.ordinal()] = d;
        newLoc = myLoc.add(Direction.EAST);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.EAST.ordinal()] > d) minDist[Direction.EAST.ordinal()] = d;
        newLoc = myLoc.add(Direction.NORTHEAST);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.NORTHEAST.ordinal()] > d) minDist[Direction.NORTHEAST.ordinal()] = d;
        newLoc = myLoc.add(Direction.CENTER);
        d = loc.distanceSquaredTo(newLoc);
        if (minDist[Direction.CENTER.ordinal()] > d) minDist[Direction.CENTER.ordinal()] = d;
    }

    void addSafe(MapLocation loc){
        if (loc.distanceSquaredTo(myLoc.add(Direction.NORTH)) <= MAX_SAFE_DIST) safe[Direction.NORTH.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.NORTHEAST)) <= MAX_SAFE_DIST) safe[Direction.NORTHEAST.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.EAST)) <= MAX_SAFE_DIST) safe[Direction.EAST.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.SOUTHEAST)) <= MAX_SAFE_DIST) safe[Direction.SOUTHEAST.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.SOUTH)) <= MAX_SAFE_DIST) safe[Direction.SOUTH.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.SOUTHWEST)) <= MAX_SAFE_DIST) safe[Direction.SOUTHWEST.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.WEST)) <= MAX_SAFE_DIST) safe[Direction.WEST.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.NORTHWEST)) <= MAX_SAFE_DIST) safe[Direction.NORTHWEST.ordinal()] = true;
        if (loc.distanceSquaredTo(myLoc.add(Direction.CENTER)) <= MAX_SAFE_DIST) safe[Direction.CENTER.ordinal()] = true;
    }


}
