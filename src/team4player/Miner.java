package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit {
    static int numDesignSchool = 0;
    static int numFulfillmentCenter = 0;
    static int numRefinery = 0;
		static MapLocation destination = null;

    int diagonalDir = -1; // the diagonal direction a miner is heading if no soup location is known
    int[] diagonalArr = {1, 3, 5, 7}; // diagonal directions to move
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>(); // go and take all soup
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
        /*if (numFulfillmentCenter < 1 && bc.readFCCreation()) {
            numFulfillmentCenter++;
        }*/
        MapLocation[] soup = rc.senseNearbySoup(-1); // build refineries only close to soup
        if (!senseBuilding(RobotType.REFINERY) && soup != null && soup.length != 0) tryBuild(RobotType.REFINERY, hqLoc);
        else if (numDesignSchool < 1 && !senseBuilding(RobotType.DESIGN_SCHOOL) && !bc.readDesignSchoolCreation() && tryBuild(RobotType.DESIGN_SCHOOL, hqLoc)) {
            numDesignSchool++;
            System.out.println("built a Design School");
        } /*else if (numFulfillmentCenter < 1 && !senseBuilding(RobotType.FULFILLMENT_CENTER) && !bc.readFCCreation() && tryBuild(RobotType.FULFILLMENT_CENTER, hqLoc)) {
            numFulfillmentCenter++;
        }*/
    }

    public boolean checkForSoup() throws GameActionException {
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
							walkTowardsSoup(soup[randomLoc]);
						}
						return true;
        }
				return false;
				/*else {
            System.out.println("GOING DIAGONAL DIRECTION");
           if (!nav.goTo(Util.directions[diagonalDir])) {// reset diagonal direction, since we hit a wall.
                //nav.goTo(Util.randomDirection()); // to remove ourselves from hallways basically
                diagonalDir = -1; // reset diagonal direction since we hit a wall
            }
				}*/
    }

		public boolean changeDirection(){
				int random = (int) (Math.random() * 4); // random soup to avoid crowds
				diagonalDir = diagonalArr[random];
				return true;
		}

		public int blockchainSoup() throws GameActionException{
				MapLocation [] mapLoc = bc.getRefineryLocFromBlockchain();
				//if (newSoupLocation != null) {
				for(MapLocation newSoupLocation : mapLoc){
						if (oldSoupLocations.contains(newSoupLocation)){return 0;} // don't add this old location, soup is gone
						else if (!soupLocations.contains(newSoupLocation)){//don't add the location twice.
							soupLocations.add(newSoupLocation);
							return 2;
						}
				}
				return 0;
		}
		//TODO -- use this
		public boolean goDiagonal() throws GameActionException{
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
        //if (checkForSoup()){goDiagonal(); return;}
				if (destination == null) { System.out.println("Destination is null");}
				else {System.out.println("Destination is NOT null: " + destination.x + " " + destination.y);}
        if (diagonalDir == -1) { changeDirection();} // diagonal walking stuff

        if (rc.getSoupCarrying() >= 70) x = 1; // refine soup
				//else if (senseNearbySoup()){ x = 0;} //mine soup
				else if (destination == null){ // we aren't travelling to a soup location, look for one
						if(!senseNearbySoup()){
							x = blockchainSoup(); // case 2, or case default
						}
				}
				else if (destination != null){
						x = 3;
				}

        switch (x) {
            case 1: // building a building and refining soup
        				buildABuilding();
                refineSoup();
                break;
						case 2: // walking
								destination = soupLocations.get(0);
								walkTowardsSoup(destination);
								break;
						case 3: //walk towards soup
								walkTowardsSoup(destination);
								if (rc.canSenseLocation(destination)){ // we are close enough to the refinery...mine soup next round.
									//if (!senseNearbySoup()){ ;} //mine soup
									oldSoupLocations.add(destination);
									soupLocations.remove(destination);
									destination = null;
								}
            default: {
								if (senseNearbySoup()){checkForSoup();}
								else {goDiagonal();}
                //if (!checkForSoup()){goDiagonal();}
                break;
            }
        }
    }

   // public void walkTowardsSoup(MapLocation[] soup, int randomLoc) throws GameActionException {
    public void walkTowardsSoup(MapLocation x) throws GameActionException {
        System.out.println("Towards soup!");
        // move towards soup...if stuck, get unstuck.
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
										/*if (rc.canSenseLocation(destination)) {
												oldSoupLocations.add(
												break;
										}*/
                    if (!nav.goTo(refineryLocation)) {
                        nav.goTo(Util.randomDirection()); // must randomly move so as to avoid getting stuck.
                        break;
                    }
                }
            } else {
                while (true) {
                    System.out.println("Toward to HQ!");
                    if (!nav.goTo(hqLoc)) {
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

    boolean senseNearbySoup() throws GameActionException {
        MapLocation[] localSoupLocations = rc.senseNearbySoup();
        for (MapLocation soupLoc : localSoupLocations) {
						if (soupLoc != null){ return true;}
        }
        return false;
    }

}
