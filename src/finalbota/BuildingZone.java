package finalbota;

import battlecode.common.*;

public class BuildingZone {

    static final int BUILDING_AREA = 1;
    static final int NEXT_TO_WALL = 2;
    static final int WALL = 3;
    static final int OUTER_WALL = 4;
    static final int HOLE = 5;

    int[][] map;
    int[] message = null;

    int wallArrayCont = 0;
    MapLocation[] wallArray;

    int row = 0;
    final int h,w;

    final int[] X = new int[]{0,-1,0,0,1,-1,-1,1,1,-2,0,0,2,-2,-2,-1,-1,1,1,2,2,-2,-2,2,2,-3,0,0,3,-3,-3,-1,-1,1,1,3,3,-3,-3,-2,-2,2,2,3,3,-4,0,0,4,-4,-4,-1,-1,1,1,4,4,-3,-3,3,3,-4,-4,-2,-2,2,2,4,4,-5,-4,-4,-3,-3,0,0,3,3,4,4,5,-5,-5,-1,-1,1,1,5,5,-5,-5,-2,-2,2,2,5,5,-4,-4,4,4,-5,-5,-3,-3,3,3,5,5,-6,0,0,6,-6,-6,-1,-1,1,1,6,6,-6,-6,-2,-2,2,2,6,6,-5,-5,-4,-4,4,4,5,5,-6,-6,-3,-3,3,3,6,6};
    final int[] Y = new int[]{0,0,-1,1,0,-1,1,-1,1,0,-2,2,0,-1,1,-2,2,-2,2,-1,1,-2,2,-2,2,0,-3,3,0,-1,1,-3,3,-3,3,-1,1,-2,2,-3,3,-3,3,-2,2,0,-4,4,0,-1,1,-4,4,-4,4,-1,1,-3,3,-3,3,-2,2,-4,4,-4,4,-2,2,0,-3,3,-4,4,-5,5,-4,4,-3,3,0,-1,1,-5,5,-5,5,-1,1,-2,2,-5,5,-5,5,-2,2,-4,4,-4,4,-3,3,-5,5,-5,5,-3,3,0,-6,6,0,-1,1,-6,6,-6,6,-1,1,-2,2,-6,6,-6,6,-2,2,-4,4,-5,5,-5,5,-4,4,-3,3,-6,6,-6,6,-3,3};

    MapLocation HQloc = null;

    MapLocation randomWallLoc = null;

    RobotController rc;

    int cont = X.length, wallCont = X.length;
    boolean hq;
    //boolean shouldComputeWall = false;

    BuildingZone(RobotController rc){
        this.rc = rc;
        w = rc.getMapWidth();
        h = rc.getMapHeight();
        map = new int[w][0];
        hq = rc.getType() == RobotType.HQ;
        if (hq) wallArray = new MapLocation[X.length];
    }

    void update(int[] message){
        this.message = message;
        HQloc = new MapLocation((message[0] >>>16)&63, (message[0] >>>10)&63);
        //if (Constants.DEBUG == 1) System.out.println(message[0] + " " + message[1] + " " + message[2] + " " + message[3] + " " + message[4] + " " + message[5]);
    }

    void run(){
        if (finished()){
            if (hq) checkWall();
            return;
        }
        while (row < map.length){
            if (Clock.getBytecodesLeft() <= 300) return;
            map[row] = new int[h];
            ++row;
        }
        if (message == null) return;
        while (cont >= 0){
            if (Clock.getBytecodesLeft() <= 300) return;
            int bit = message[cont/32 + 1]&(1 << (cont%32));
            if (bit != 0){
                int x = HQloc.x + X[cont], y = HQloc.y + Y[cont];
                map[x][y] = BUILDING_AREA;
            }
            cont--;
        }
        //if (!shouldComputeWall) return;
        while (wallCont >= 0){
            if (Clock.getBytecodesLeft() <= 700) return;
            System.out.print(Clock.getBytecodeNum());
            int bit = message[wallCont/32 + 1]&(1 << (wallCont%32));
            if (bit != 0){
                MapLocation loc = new MapLocation(HQloc.x + X[wallCont], HQloc.y + Y[wallCont]);
                MapLocation newLoc = loc.add(Direction.NORTH);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.NORTHEAST);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.EAST);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.SOUTHEAST);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.SOUTH);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.SOUTHWEST);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.WEST);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                newLoc = loc.add(Direction.NORTHWEST);
                if (rc.onTheMap(newLoc)){
                    switch(map[newLoc.x][newLoc.y]){
                        case 0:
                            map[newLoc.x][newLoc.y] = WALL;
                            if (randomWallLoc == null) randomWallLoc = newLoc;
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            if (hq) wallArray[wallArrayCont++] = newLoc;
                            break;
                        case WALL:
                            map[loc.x][loc.y] = NEXT_TO_WALL;
                            break;
                    }
                }
                if (rc.getType() == RobotType.HQ && map[loc.x][loc.y] == NEXT_TO_WALL) rc.setIndicatorDot(loc, 255, 255, 0);
                /*int x = HQloc.x + X[wallCont], y = HQloc.y + Y[wallCont];
                int i = 9;
                while (--i >= 0){
                    int newX = x + X[i], newY = y + Y[i];
                    if (0 > newX) continue;
                    if (0 > newY) continue;
                    if (newX >= w) continue;
                    if (newY >= h) continue;
                    if (map[newX][newY] == 0){
                        map[newX][newY] = WALL;
                        if (randomWallLoc == null) randomWallLoc = new MapLocation(newX, newY);
                        map[x][y] = NEXT_TO_WALL;
                        //if (Constants.DEBUG == 1) rc.setIndicatorDot(new MapLocation(newX, newY), 0, 255, 0);
                    }
                }*/
            }
            wallCont--;
            System.out.print(Clock.getBytecodeNum());
        }
        if (hq && wallCont < 0) checkWall();
    }

    void checkWall(){
        try {
            if (wallArrayCont <= 0) return;
            System.out.println("Checking wall "+ wallArrayCont);
            while (wallArrayCont > 0) {
                if (Clock.getBytecodesLeft() <= 500) return;
                MapLocation loc = wallArray[wallArrayCont - 1];
                if (!rc.canSenseLocation(loc)) return;
                int e = rc.senseElevation(loc);
                if (e < Constants.WALL_HEIGHT) return;
                --wallArrayCont;
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }



    boolean finished(){
        return wallCont < 0;
    }

    boolean finishedHQ(){
        return wallCont < 0 && wallArrayCont <= 0;
    }

    void debugPrint(){
        if (Constants.DEBUG != 1) return;
        System.out.println("Debug printing zone!");
        int i = X.length;
        while (--i >= 0){
            MapLocation loc = new MapLocation(HQloc.x + X[i], HQloc.y + Y[i]);
            if (loc.x < 0) continue;
            if (loc.x >= map.length) continue;
            if (loc.y < 0) continue;
            if (loc.y >= h) continue;
            if (map[loc.x][loc.y] == 1) rc.setIndicatorDot(loc, 0, 0, 255);
        }
    }

    boolean isWall(MapLocation loc){
        int zone = map[loc.x][loc.y];
        switch(zone){
            case WALL:
            case OUTER_WALL:
                return true;
            case BUILDING_AREA:
            case NEXT_TO_WALL:
            case HOLE:
                return false;
            default:
                if((loc.x - HQloc.x + 64 )%2 == 1 || (loc.y - HQloc.y + 64)%2 == 1){
                    map[loc.x][loc.y] = OUTER_WALL;
                    return true;
                }
                map[loc.x][loc.y] = HOLE;
                return false;
        }
    }

    int getZone(MapLocation loc){
        int zone = map[loc.x][loc.y];
        switch(zone){
            case WALL:
                return WALL;
            case OUTER_WALL:
                return OUTER_WALL;
            case BUILDING_AREA:
                return BUILDING_AREA;
            case NEXT_TO_WALL:
                return NEXT_TO_WALL;
            case HOLE:
                return HOLE;
            default:
                if((loc.x - HQloc.x + 64 )%2 == 1 || (loc.y - HQloc.y + 64)%2 == 1){
                    map[loc.x][loc.y] = OUTER_WALL;
                    return OUTER_WALL;
                }
                map[loc.x][loc.y] = HOLE;
                return HOLE;
        }
    }

    boolean canBuild(MapLocation loc){
        return (loc.x - HQloc.x + 64 )%2 == 1 && (loc.y - HQloc.y + 64)%2 == 1;
    }

    boolean isCritical(MapLocation loc){
        switch(getZone(loc)){
            case WALL:
                return true;
            case OUTER_WALL:
                return false;
            case BUILDING_AREA:
                return true;
            case NEXT_TO_WALL:
                return true;
            case HOLE:
                return false;
            default:
                return false;
        }
    }

    //TODO
    boolean isSafe(MapLocation loc){
        return false;
    }

}
