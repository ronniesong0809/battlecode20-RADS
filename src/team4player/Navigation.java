package team4player;
import battlecode.common.*;

public class Navigation {
    RobotController rc;

    public Navigation(RobotController r){
        rc = r;
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        }
        return false;
    }

    boolean tryMove() throws GameActionException {
        for (Direction dir : Util.directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    boolean goTo(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft().rotateRight(), dir.rotateLeft().rotateRight(), dir.rotateRight().rotateLeft()};
        for (Direction d : toTry)
            if (tryMove(d))
                return true;
        return false;
    }

    boolean goAround(MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        Direction[] toTry =
                {
                    dir,
                    dir.rotateLeft(),
                    dir.rotateLeft().rotateLeft(),
                    dir.rotateRight(),
                    dir.rotateRight().rotateRight()
                };

        for (Direction d : toTry) {
            if (tryMove(d)) {
                return true;
            }
        }
        return false;
    }

    boolean droneMove(Direction dir) throws GameActionException {
        Direction[] toTry =
                {
                        dir,
                        dir.rotateLeft(),
                        dir.rotateLeft().rotateLeft(),
                        dir.rotateLeft().rotateLeft().rotateLeft(),
                        dir.rotateRight(),
                        dir.rotateRight().rotateRight()
                };

        for (Direction d : toTry) {
            if (rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }

    boolean circleMove(Direction dir) throws GameActionException {
        Direction[] toTry =
                {
                        dir,
                        dir.rotateLeft(),
                        dir.rotateLeft().rotateLeft(),
                        dir.rotateLeft().rotateLeft().rotateLeft(),
                        dir.rotateRight(),
                        dir.rotateRight().rotateRight()
                };

        for (Direction d : toTry) {
            if (rc.canMove(dir)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }

    boolean goAround(Direction dir) throws GameActionException {
        Direction[] toTry =
                {
                        dir,
                        dir.rotateLeft(),
                        dir.rotateLeft().rotateLeft(),
                        dir.rotateRight(),
                        dir.rotateRight().rotateRight()
                };

        for (Direction d : toTry) {
            if (tryMove(d)) {
                return true;
            }
        }
        return false;
    }



    boolean goTo(MapLocation dir) throws GameActionException {
        return goTo(rc.getLocation().directionTo(dir));
    }
}
