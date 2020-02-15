package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit {
    static int numDesignSchool = 0;
    static int numFulfillmentCenter = 0;
    static int numRefinery = 0;
    int diagonalDir = -1; // the diagonal direction a miner is heading if no soup location is known
    int[] diagonalArr = {1, 3, 5, 7}; // diagonal directions to move
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();

    public Miner(RobotController rc) {
        super(rc);
    }

    // Sense a building passes in
    public boolean senseBuilding(RobotType type) {
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
        for (RobotInfo r : nearbyRobots) {
            if (r.type == type) {
                return true;
            }
        }
        return false;
    }

    public void buildABuilding() throws GameActionException {
        // Build design school if miner hasn't made one, none are nearby, and we are by HQ  --- all to control production of DSs
        if (numDesignSchool < 1 && bc.readDesignSchoolCreation()) {
            numDesignSchool++;
        }
        if (numFulfillmentCenter < 1 && bc.readFCCreation()) {
            numFulfillmentCenter++;
        }
        MapLocation[] soup = rc.senseNearbySoup(-1); // build refineries only close to soup
        if (!senseBuilding(RobotType.REFINERY) && soup != null && soup.length != 0) tryBuild(RobotType.REFINERY, hqLoc);
        else if (numDesignSchool < 1 && !senseBuilding(RobotType.DESIGN_SCHOOL) && !bc.readDesignSchoolCreation() && tryBuild(RobotType.DESIGN_SCHOOL, hqLoc)) {
            numDesignSchool++;
            System.out.println("built a Design School");
        } else if (numFulfillmentCenter < 1 && !senseBuilding(RobotType.FULFILLMENT_CENTER) && !bc.readFCCreation() && tryBuild(RobotType.FULFILLMENT_CENTER, hqLoc)) {
            numFulfillmentCenter++;
        }
    }

    public void checkForSoup() throws GameActionException {
        MapLocation[] soup = rc.senseNearbySoup(-1);
        if (soup != null && soup.length != 0) { // we found soup! Head towards it
					  boolean mined = false;
						// Try to mine any soup nearby
            for (Direction dir : Util.directions) {
                if (tryMine(dir)) {
                    mined = true;
                }
            }
						// move towards soup if we didn't mine anything. This code increases avg rounds by 200+
						if (!mined) {
							int randomLoc = (int) (Math.random() * soup.length + 0); // random soup to avoid crowds
							walkTowardsSoup(soup, randomLoc);
						}
        } else {
            System.out.println("GOING DIAGONAL DIRECTION");
           if (!nav.goTo(Util.directions[diagonalDir])) {// reset diagonal direction, since we hit a wall.
                nav.goTo(Util.randomDirection()); // to remove ourselves from hallways basically
                diagonalDir = -1; // reset diagonal direction since we hit a wall
            }
				}
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if (diagonalDir == -1) {
            int random = (int) (Math.random() * 4); // random soup to avoid crowds
            diagonalDir = diagonalArr[random];
        }
				int x = 0;
        if (rc.getSoupCarrying() >= 70) x = 1;
        switch (x) {
            case 1:
        				buildABuilding();
                refineSoup();
                break;
            default: {
                checkForSoup();
                break;
            }
        }
    }

    public void walkTowardsSoup(MapLocation[] soup, int randomLoc) throws GameActionException {
        System.out.println("Towards soup!");
        // move towards soup...if stuck, get unstuck.
        MapLocation x = soup[randomLoc];
        if (!nav.goAround(x)) {
            nav.goTo(Util.randomDirection());
        }
    }

    public void refineSoup() throws GameActionException {
        MapLocation refineryLocation = findRefinery();
        if (rc.getSoupCarrying() >= 70) {
            System.out.println("TRYING TO DEPOSIT SOUP...");
            for (Direction dir : Util.directions)
                if (tryRefine(dir)) {
                    System.out.println("SUCCESFULLY DEPOSITED SOUP");
                    return;
                }
            if (refineryLocation != null) {
                while (true) {
                    System.out.println("Toward to Refinery!");
                    if (!nav.goTo(refineryLocation)) {
                        nav.goTo(Util.randomDirection()); // must randomly move so as to avoid getting stuck. TODO -- add N,E,W, S exclusively?
                        break;
                    }
                }
            } else {
                while (true) {
                    System.out.println("Toward to HQ!");
                    if (nav.goTo(hqLoc) == false) {
                        nav.goTo(Util.randomDirection());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        }
        return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        }
        return false;
    }
}
