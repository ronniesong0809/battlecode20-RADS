package team4player;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

public class HQ extends Building {
    static int numMiners = 0;
    static List<MapLocation> wallLocs = null;
    static int minWallHeight = 10;
    static boolean sentWallComplete = false;

    public HQ(RobotController rc) throws GameActionException{
        super(rc);
        bc.sendHqLoc(rc.getLocation());
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (wallLocs == null) {
            initSurroundingWalls();
        }

        if (numMiners < 5) {
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    numMiners++;
                }
            }
        }

        if (checkIfWallComplete() && !sentWallComplete) {
            bc.broadcastInitialWallComplete();
            sentWallComplete = true;
        }
    }

    public void initSurroundingWalls() {
        wallLocs = new ArrayList<MapLocation>();

        int block_x = rc.getLocation().x - 2;
        int block_y = rc.getLocation().y + 2;
        for (int i = 0; i < 16; i++) {
            wallLocs.add(new MapLocation(block_x, block_y));

            switch (i) {
                case 0: case 1: case 2: case 3:
                    block_x++; break;
                case 4: case 5: case 6: case 7:
                    block_y--; break;
                case 8: case 9: case 10: case 11:
                    block_x--; break;
                case 12: case 13: case 14: case 15:
                    block_y++; break;
            }
        }
    }

    public boolean checkIfWallComplete() throws GameActionException {
        for (MapLocation location : wallLocs) {
            if (rc.senseElevation(location) < minWallHeight) {
                return false;
            }
        }
        return true;
    }
}
