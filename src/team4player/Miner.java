package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit {
    static int numDesignSchool = 0;
    static int numFulfillmentCenter = 0;
    static int numRefinery = 0;
    static MapLocation blockchainRefineryDestination = null; // blockchain refinery
    static MapLocation soupDestination = null; // TODO -- pursue one soup location at a time
    static MapLocation baseRefinery = null; // When a miner cannot sense a refinery or HQ, but a refinery has been built...go to this (since HQ is blocked in by landscapers)

    int diagonalDir = -1; // the diagonal direction a miner is heading if no soup location is known
    int[] diagonalArr = {1, 3, 5, 7}; // diagonal directions to move
    ArrayList<MapLocation> oldSoupLocations = new ArrayList<MapLocation>(); // don't go here anymore

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

    //TODO -- move towards one specific soup location
    /*public boolean checkForSoup() throws GameActionException {
        MapLocation[] soup = rc.senseNearbySoup(-1);
        if (soup != null && soup.length != 0) { // we found soup! Head towards it
						if(blockchainRefineryDestination == null){
							int randomLoc = (int) (Math.random() * soup.length + 0); // random soup to avoid crowds
							soupDestination = soup[randomLoc];
							//if (!walkTowardsSoup(soupDestination) && rc.canSenseLocation(soupDestination)){
								//soupDestination = null;
							//}
						return true;
						}
        }
				return false;
    }*/

    public boolean checkForSoup() throws GameActionException {
        MapLocation[] soup = rc.senseNearbySoup(-1);
        if (soup != null && soup.length != 0) { // we found soup! Head towards it
            boolean mined = false;
            // Try to mine any soup nearby
            mined = tryMine();
            // move towards soup if we didn't mine anything. This code increases avg rounds by 200+
            if (!mined) {
                int randomLoc = (int) (Math.random() * soup.length + 0); // random soup to avoid crowds
                walkTowardsSoup(soup[randomLoc]);
            }
            return true;
        }
        return false;
    }

    public boolean changeDirection() {
        int random = (int) (Math.random() * 4); // random soup to avoid crowds
        diagonalDir = diagonalArr[random];
        return true;
    }

    public MapLocation blockchainSoup() throws GameActionException {
        MapLocation[] mapLoc = bc.getRefineryLocFromBlockchain();
        for (MapLocation newSoupLocation : mapLoc) {
            if (oldSoupLocations.contains(newSoupLocation)) {
                continue;
            } // don't add this old location, soup is gone
            return newSoupLocation;
        }
        return null;
    }

    public boolean goDiagonal() throws GameActionException {
        System.out.println("GOING DIAGONAL DIRECTION");
        if (!nav.goTo(Util.directions[diagonalDir])) {// reset diagonal direction, since we hit a wall.
            //nav.goTo(Util.randomDirection()); // to remove ourselves from hallways basically
            diagonalDir = -1; // reset diagonal direction since we hit a wall
            return false;
        }
        return true;
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        int x = 0;

        /* THE FOLLOWING IS IN ORDER OF PREFERENCE OF A MINER's BEHAVIOR*/
        // refine soup
        if (rc.getSoupCarrying() >= 70) x = 1;

            // check for nearby soup // TODO
            //else if(soupDestination != null){x=3;}

            // we aren't travelling to a soup/refinery location, look for one
        else if (blockchainRefineryDestination == null) {
            blockchainRefineryDestination = blockchainSoup();
            baseRefinery = blockchainRefineryDestination; // there is a closer refinery to make our base
        }

        //We may currently be pursuing a refinery (after before else if, or on a previous turn)
        if (blockchainRefineryDestination != null) {
            x = 2;
        }

        switch (x) {
            case 1: // building a building and refining soup
                buildABuilding();
                if (!refineSoup()) {
                    walkTowardsBuilding();
                }
                break;
            case 2: //walk towards soup refinery
                if (!walkTowardsSoup(blockchainRefineryDestination) && rc.canSenseLocation(blockchainRefineryDestination)) { // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
                    oldSoupLocations.add(blockchainRefineryDestination); // TODO -- adds any soup location...could be a performance issue
                    blockchainRefineryDestination = null;
                }
                break;
            case 3:
                //TODO -- get soup going
								/*if (!walkTowardsSoup(soupDestination) && rc.canSenseLocation(soupDestination)){
									tryMine();
								}
								else {soupDestination = null;}*/

                //walkTowardsSoup(soupDestination){
                //if(tryMine()){}
								/*for (Direction dir : Util.directions)
									if(tryMine(dir)){break;}
								else if (soupDestination != null && !walkTowardsSoup(soupDestination) || rc.canSenseLocation(soupDestination)){
									//soupDestination = null;
								}
								break;*/

            default: {
                // We are pursuing a soup location
								/*if (soupDestination != null){
								 	if (!walkTowardsSoup(soupDestination) || rc.canSenseLocation(soupDestination)){

									}
								if(tryMine()){}*/
                //buildABuilding();
                if (diagonalDir == -1) {
                    changeDirection();
                } // diagonal walking stuff
                if (!checkForSoup()) {
                    goDiagonal();
                } // no soup around...walk diagonally
                break;
            }
        }
    }

    //TODO -- have this be !nav.goTO on 3rd line?
    public boolean walkTowardsSoup(MapLocation x) throws GameActionException {
        System.out.println("Towards soup!");
        // move towards soup...if stuck, get unstuck.
        if (!nav.goAround(x) && rc.canSenseLocation(x)) { // we ran into something, and we are nearby the refinery (e.g., we bumped into the refinery)
            nav.goTo(Util.randomDirection());
            return false;
        }
        return true;
    }

    public boolean walkTowards(MapLocation x) throws GameActionException {
        System.out.println("Towards building!");
        // move towards soup...if stuck, get unstuck.
        if (!nav.goAround(x) && rc.canSenseLocation(x)) { // we ran into something, and we are nearby the refinery (e.g., we bumped into the refinery)
            nav.goTo(Util.randomDirection());
            return false;
        }
        return true;
    }

    public boolean refineSoup() throws GameActionException {
        System.out.println("TRYING TO DEPOSIT SOUP...");
        for (Direction dir : Util.directions)
            if (tryRefine(dir)) {
                System.out.println("SUCCESFULLY DEPOSITED SOUP");
                return true;
            }
        return false;
    }

    public void walkTowardsBuilding() throws GameActionException {
        MapLocation refineryLocation = findRefinery();
        if (refineryLocation != null) {
            while (true) {
                if (baseRefinery == null) {
                    baseRefinery = refineryLocation;
                }
                System.out.println("Toward to Refinery!");
                // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
                if (!walkTowards(refineryLocation) && rc.canSenseLocation(refineryLocation)) {
                    break;
                }
            }
        } else if (baseRefinery != null) {
            while (true) {
                if (!walkTowards(baseRefinery) && rc.canSenseLocation(baseRefinery)) {
                    break;
                }
            }
        } else if (baseRefinery == null) { // TODO -- a bug exists where a miner doesn't set a base refinery.. probably because of too much HQ broadcasting
            System.out.println("Toward to HQ!");
            while (true) {
                if (!walkTowards(hqLoc) && rc.canSenseLocation(hqLoc)) {
                    break;
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
     * @return true if a move was performed
     * @throws GameActionException
     */
    //NEW
    boolean tryMine() throws GameActionException {
        for (Direction dir : Util.directions) {
            if (rc.isReady() && rc.canMineSoup(dir)) {
                rc.mineSoup(dir);
                return true;
            }
        }
        return false;
    }

    //OLD
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        }
        return false;
    }

    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        }
        return false;
    }

    boolean senseNearbySoup() throws GameActionException {
        MapLocation[] localSoupLocations = rc.senseNearbySoup();
        for (MapLocation soupLoc : localSoupLocations) {
            if (soupLoc != null) {
                return true;
            }
        }
        return false;
    }
}
