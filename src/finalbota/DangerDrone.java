package finalbota;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class DangerDrone {

    RobotController rc;
    int[][] dangerMap;
    Direction[] dirPath =  new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.NORTH, Direction.NORTH, Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.EAST, Direction.EAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTH, Direction.SOUTH, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.WEST, Direction.WEST, Direction.NORTHWEST, Direction.NORTH, Direction.NORTH, Direction.NORTH, Direction.NORTH, Direction.EAST, Direction.EAST, Direction.EAST, Direction.EAST, Direction.SOUTH, Direction.SOUTH, Direction.SOUTH, Direction.SOUTH, Direction.WEST, Direction.WEST, Direction.WEST, Direction.NORTH, Direction.NORTH, Direction.NORTH, Direction.EAST, Direction.EAST, Direction.SOUTH, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.CENTER};
    int contDanger = -1, contRemove = -1;
    MapLocation locDanger, locRemove;
    int[][] map;

    boolean[] cantMove;
    MapLocation myLoc;
    int[] netGunCont;

    boolean shouldUpdateDanger;


    DangerDrone(RobotController rc, boolean shouldUpdateDanger){
        this.rc = rc;
        this.shouldUpdateDanger = shouldUpdateDanger;
        map = new int[rc.getMapWidth()][rc.getMapHeight()];
        if (shouldUpdateDanger) dangerMap = new int[rc.getMapWidth()][rc.getMapHeight()];
    }

    void addDanger(MapLocation loc){
        if (!shouldUpdateDanger) return;
        if (Constants.DEBUG == 1) System.out.println("Starting danger at " + Clock.getBytecodeNum());
        contDanger = dirPath.length;
        locDanger = new MapLocation(loc.x, loc.y);
        while (--contDanger >= 0) {
            locDanger = locDanger.add(dirPath[contDanger]);
            if (rc.onTheMap(locDanger)){
                ++dangerMap[locDanger.x][locDanger.y];
                //if (Constants.DEBUG == 1) rc.setIndicatorDot(newLoc, 0, 0, 255);
            }
            if (Clock.getBytecodesLeft() <= Constants.SAFETY_BYTECODE_MESSAGES) return;
        }
        if (Constants.DEBUG == 1) System.out.println("Ending danger at " + Clock.getBytecodeNum());

    }

    void removeDanger(MapLocation loc){
        if (!shouldUpdateDanger) return;
        contRemove = dirPath.length;
        locRemove = new MapLocation(loc.x, loc.y);
        while (--contRemove >= 0) {
            locRemove = locRemove.add(dirPath[contRemove]);
            if (rc.onTheMap(locRemove)){
                --dangerMap[locRemove.x][locRemove.y];
                //if (Constants.DEBUG == 1) rc.setIndicatorDot(newLoc, 0, 0, 255);
            }
            if (Clock.getBytecodesLeft() <= Constants.SAFETY_BYTECODE_MESSAGES) return;
        }
    }

    void complete(){
        if (!shouldUpdateDanger) return;
        while (--contDanger >= 0) {
            locDanger = locDanger.add(dirPath[contDanger]);
            if (rc.onTheMap(locDanger)){
                ++dangerMap[locDanger.x][locDanger.y];
                //if (Constants.DEBUG == 1) rc.setIndicatorDot(newLoc, 0, 0, 255);
            }
            if (Clock.getBytecodesLeft() <= Constants.SAFETY_BYTECODE_MESSAGES) return;
        }
        while (--contRemove >= 0) {
            locRemove = locRemove.add(dirPath[contRemove]);
            if (rc.onTheMap(locRemove)){
                --dangerMap[locRemove.x][locRemove.y];
                //if (Constants.DEBUG == 1) rc.setIndicatorDot(newLoc, 0, 0, 255);
            }
            if (Clock.getBytecodesLeft() <= Constants.SAFETY_BYTECODE_MESSAGES) return;
        }
    }

    void initVisibleDanger(){
        myLoc = rc.getLocation();
        cantMove = new boolean[9];
        netGunCont = new int[9];
    }

    void addVisibleDanger(MapLocation loc, int cooldown){
        int maxDist = getMaxDist(cooldown);
        boolean alreadyAccounted = map[loc.x][loc.y] > 0;
        MapLocation newLoc = myLoc.add(Direction.NORTH);
        int d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.NORTH.ordinal()];
            if (d <= maxDist) cantMove[Direction.NORTH.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.NORTHWEST);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.NORTHWEST.ordinal()];
            if (d <= maxDist) cantMove[Direction.NORTHWEST.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.WEST);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.WEST.ordinal()];
            if (d <= maxDist) cantMove[Direction.WEST.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.SOUTHWEST);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.SOUTHWEST.ordinal()];
            if (d <= maxDist) cantMove[Direction.SOUTHWEST.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.SOUTH);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.SOUTH.ordinal()];
            if (d <= maxDist) cantMove[Direction.SOUTH.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.SOUTHEAST);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.SOUTHEAST.ordinal()];
            if (d <= maxDist) cantMove[Direction.SOUTHEAST.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.EAST);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.EAST.ordinal()];
            if (d <= maxDist) cantMove[Direction.EAST.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.NORTHEAST);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.NORTHEAST.ordinal()];
            if (d <= maxDist) cantMove[Direction.NORTHEAST.ordinal()] = true;
        }
        newLoc = myLoc.add(Direction.CENTER);
        d = newLoc.distanceSquaredTo(loc);
        if (d <= 13){
            if (alreadyAccounted) --netGunCont[Direction.CENTER.ordinal()];
            if (d <= maxDist) cantMove[Direction.CENTER.ordinal()] = true;
        }
        /*
        newLoc = myLoc.add(Direction.NORTHWEST);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.NORTHWEST.ordinal()] = true;
        newLoc = myLoc.add(Direction.WEST);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.WEST.ordinal()] = true;
        newLoc = myLoc.add(Direction.SOUTHWEST);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.SOUTHWEST.ordinal()] = true;
        newLoc = myLoc.add(Direction.SOUTH);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.SOUTH.ordinal()] = true;
        newLoc = myLoc.add(Direction.SOUTHEAST);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.SOUTHEAST.ordinal()] = true;
        newLoc = myLoc.add(Direction.EAST);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.EAST.ordinal()] = true;
        newLoc = myLoc.add(Direction.NORTHEAST);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.NORTHEAST.ordinal()] = true;
        newLoc = myLoc.add(Direction.CENTER);
        if (newLoc.distanceSquaredTo(loc) <= 13) cantMove[Direction.CENTER.ordinal()] = true;*/
    }

    int getMaxDist(int cd){
        switch (cd){
            case 3:
            case 4:
                return 5;
            case 5:
            case 6:
                return 1;
            case 7:
            case 8:
            case 9:
            case 10:
                return 0;
            case 0:
            case 1:
            case 2:
            default:
                return 13;
        }
    }


}
