package finalbota;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class HQWall {

    RobotController rc;
    Direction[] dirs = Direction.values();


    int[] mes = new int[7];

    final int N = 7;
    // 5x5
    //int[][] visited = {{0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}};
    //all vision range
    //int[][] visited = new int[15][15];
    //7x7
    int[][] visited = {{0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0}};
    int[][] posToBit = {{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, 137, 121, 113, 109, 114, 122, 138, -1, -1, -1, -1}, {-1, -1, -1, 129, 101, 89, 81, 69, 82, 90, 102, 130, -1, -1, -1}, {-1, -1, 131, 97, 70, 61, 49, 45, 50, 62, 71, 98, 132, -1, -1}, {-1, 139, 103, 72, 57, 37, 29, 25, 30, 38, 58, 73, 104, 140, -1}, {-1, 123, 91, 63, 39, 21, 13, 9, 14, 22, 40, 64, 92, 124, -1}, {-1, 115, 83, 51, 31, 15, 5, 1, 6, 16, 32, 52, 84, 116, -1}, {-1, 110, 74, 46, 26, 10, 2, 0, 3, 11, 27, 47, 75, 111, -1}, {-1, 117, 85, 53, 33, 17, 7, 4, 8, 18, 34, 54, 86, 118, -1}, {-1, 125, 93, 65, 41, 23, 19, 12, 20, 24, 42, 66, 94, 126, -1}, {-1, 141, 105, 76, 59, 43, 35, 28, 36, 44, 60, 77, 106, 142, -1}, {-1, -1, 133, 99, 78, 67, 55, 48, 56, 68, 79, 100, 134, -1, -1}, {-1, -1, -1, 135, 107, 95, 87, 80, 88, 96, 108, 136, -1, -1, -1}, {-1, -1, -1, -1, 143, 127, 119, 112, 120, 128, 144, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};


    int MAX_SIZE = 256;
    MapLocation[] queue = new MapLocation[MAX_SIZE];
    int start = 0, end = 1;

    MapLocation myLoc;

    int myLocX, myLocY;


    HQWall(RobotController rc){
        this.rc = rc;
        myLoc = rc.getLocation();
        myLocX = myLoc.x - N;
        myLocY = myLoc.y - N;
        queue[0] = myLoc;
        visited[N][N] = 1;
        mes[0] = Comm.WALL | (myLoc.x << 16) | (myLoc.y << 10);
    }

    boolean finished(){
        return start >= end;
    }

    void run(){
        try {
            int sight = rc.getCurrentSensorRadiusSquared();
            while (start != end) {
                if (Clock.getBytecodesLeft() < 500) return;
                MapLocation loc = queue[start];
                start++;
                boolean interior = true;
                int i = 8;
                Direction dir = Direction.NORTH;
                //if (Constants.DEBUG == 1) System.out.println("Analyzing (" + loc.x + ", " + loc.y + ");");
                while (--i >= 0) {
                    MapLocation newLoc = loc.add(dir);
                    if (myLoc.distanceSquaredTo(newLoc) > sight) {
                        interior = false;
                        break;
                    }
                    if (!rc.canSenseLocation(newLoc)){
                        dir = dir.rotateLeft();
                        continue;
                    }
                    if (visited[newLoc.x - myLocX][newLoc.y - myLocY] > 1) {
                        interior = false;
                        break;
                    }
                    if (rc.senseElevation(newLoc) < Constants.MIN_DEPTH){
                        interior = false;
                        break;
                    }
                    dir = dir.rotateLeft();
                    //int x = newLoc.x - myLocX, y = newLoc.y - myLocY;
                }
                if (interior || loc.distanceSquaredTo(myLoc) == 0) {
                    //if (Constants.DEBUG == 1) System.out.println("I'm interior!");
                    int bit = posToBit[loc.x - myLocX][loc.y - myLocY];
                    //if (Constants.DEBUG == 1) System.out.println((loc.x - myLocX) + " " + (loc.y - myLocY));
                    //if (Constants.DEBUG == 1) System.out.println(bit);
                    mes[bit/32 + 1] += (1 << (bit%32));
                    MapLocation newLoc = loc.add(Direction.NORTH);
                    if (visited[newLoc.x - myLocX][newLoc.y - myLocY] == 0) {
                        if (rc.canSenseLocation(newLoc)) queue[end++] = loc.add(Direction.NORTH);
                        visited[newLoc.x - myLocX][newLoc.y - myLocY] = 1;
                    }
                    newLoc = loc.add(Direction.WEST);
                    if (visited[newLoc.x - myLocX][newLoc.y - myLocY] == 0){
                        if (rc.canSenseLocation(newLoc)) queue[end++] = loc.add(Direction.WEST);
                        visited[newLoc.x - myLocX][newLoc.y - myLocY] = 1;
                    }
                    newLoc = loc.add(Direction.SOUTH);
                    if (visited[newLoc.x - myLocX][newLoc.y - myLocY] == 0){
                        if (rc.canSenseLocation(newLoc)) queue[end++] = loc.add(Direction.SOUTH);
                        visited[newLoc.x - myLocX][newLoc.y - myLocY] = 1;
                    }
                    newLoc = loc.add(Direction.EAST);
                    if (visited[newLoc.x - myLocX][newLoc.y - myLocY] == 0){
                        if (rc.canSenseLocation(newLoc)) queue[end++] = loc.add(Direction.EAST);
                        visited[newLoc.x - myLocX][newLoc.y - myLocY] = 1;
                    }
                }
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

}
