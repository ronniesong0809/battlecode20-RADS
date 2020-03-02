package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit {

		// Decides a switch statement for the buildABuilding function
		public int decideX(){
				int ret = -99;
				//if (!senseBuilding(RobotType.VAPORATOR)) {ret=-1;}
				MapLocation[] soup = rc.senseNearbySoup(-1);
				if(soup != null && soup.length != 0 && !senseBuilding(RobotType.REFINERY)){ret=0;}
				//if (!senseBuilding(RobotType.REFINERY)) {ret=0;}
				else if (numDesignSchool < 1){ret=1;}
				//else if (numFulfillmentCenter < 1){ret=2;}
				else if (numNetGuns < 1){ret=3;}
				return ret;
		}

		// These are variables used to indicate what buildings to build
		static int numDesignSchool = 0;
    static int numFulfillmentCenter = 0;
    static int numNetGuns=0;

		// These are destinations used for mining and travelling.
    static MapLocation blockchainRefineryDestination = null; // blockchain refinery
    MapLocation baseRefinery = hqLoc; // When a miner cannot sense a refinery or HQ, one exists...go to this
		static Direction lastSuccessfulMine = null; // Direction where a miner had success mining last time..try this FIRST
		static MapLocation returnToSoupLocation = null; // we can return to a location when we refine at a far off refinery or just want to keep track of one soup instead of calling the soup find function repeatedly

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

    /*public boolean buildABuilding() throws GameActionException {
    	// Build design school if miner hasn't made one, none are nearby, and we are by HQ  --- all to control production of DSs
		if (numDesignSchool < 1 && bc.readDesignSchoolCreation()) {
			numDesignSchool++;
		}
		if ((numFulfillmentCenter < 1 && bc.readFCCreation()) || // Another robot created a center
				(numFulfillmentCenter < 1 && !senseBuilding(RobotType.FULFILLMENT_CENTER) && !bc.readFCCreation() && tryBuild(RobotType.FULFILLMENT_CENTER, hqLoc))) // This robot created a center
		{
				numFulfillmentCenter++;
		}
		MapLocation[] soup = rc.senseNearbySoup(-1); // build refineries only close to soup
		if (!senseBuilding(RobotType.REFINERY) && soup != null && soup.length != 0) tryBuild(RobotType.REFINERY, hqLoc);
			// This was Alex's version
		//else if (numDesignSchool < 2 && !senseBuilding(RobotType.DESIGN_SCHOOL) && !bc.readDesignSchoolCreation() && tryBuild(RobotType.DESIGN_SCHOOL, hqLoc)) {
			// I think this version should be put forth if I can work out the bugs
			else if (numDesignSchool < 1 && !senseBuilding(RobotType.DESIGN_SCHOOL) && !bc.readDesignSchoolCreation() && tryBuild(RobotType.DESIGN_SCHOOL, hqLoc)) {
				bc.broadcastDesignSchoolCreation();
			numDesignSchool++;
			System.out.println("built a Design School");
		}
		else if (!senseBuilding(RobotType.VAPORATOR)) {
			tryBuild(RobotType.VAPORATOR, hqLoc);
		}else if(numNetGuns<1 && !senseBuilding(RobotType.NET_GUN)&& !bc.readNGCreation()&& tryBuild(RobotType.NET_GUN,hqLoc)){
			numNetGuns++;
		}
		return true;
	}*/


    public boolean buildABuilding() throws GameActionException {
			int x = decideX();
				switch (x){ // case statements fall through if one building fails.
					case -1: // Build a vaporator //TODO see if this actually is helpful...
		 					//if (!senseBuilding(RobotType.VAPORATOR)) { // TODO -- add this to below condition?
						if(tryBuild(RobotType.VAPORATOR, hqLoc)){break;}
					case 0: // Build a refinery (only close to soup)
						tryBuild(RobotType.REFINERY, hqLoc); break;
					case 1: // No Design Schools yet?
						if (bc.readDesignSchoolCreation()) {
							numDesignSchool++;
						} else if (numDesignSchool < 1 && !senseBuilding(RobotType.DESIGN_SCHOOL) && !bc.readDesignSchoolCreation() && tryBuild(RobotType.DESIGN_SCHOOL, hqLoc)) {
								bc.broadcastDesignSchoolCreation();
								numDesignSchool++;
								System.out.println("built a Design School");
								break;
						}
					/*case 2: // No fulfillment centers yet?
						if (bc.readFCCreation()) {
							numFulfillmentCenter++;
						} else if (!senseBuilding(RobotType.FULFILLMENT_CENTER) && !bc.readFCCreation() && tryBuild(RobotType.FULFILLMENT_CENTER, hqLoc)) {
							numFulfillmentCenter++;
							break;
						}*/
					case 3: // No Netguns yet?
						if(numNetGuns<1 && !senseBuilding(RobotType.NET_GUN)
								&& !bc.readNGCreation()&& tryBuild(RobotType.NET_GUN,hqLoc)){
								numNetGuns++;
								break;
						}
					default:break;
				}
		return true;
	}

		public boolean checkForSoup(MapLocation [] soup) throws GameActionException {
			boolean mined = false;
			// Try to mine any soup nearby
			mined = tryMine();
			// move towards soup if we didn't mine anything. This code increases avg rounds by 200+
			if (!mined) {
				int randomLoc = (int) (Math.random() * soup.length + 0); // random soup to avoid crowds
				walkTowards(soup[randomLoc]);
				returnToSoupLocation = soup[randomLoc];
				//soupDestination = soup[randomLoc];// TODO -- remove todo once this works.
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
			MapLocation[] mapLoc = Broadcast.getRefineryLocFromBlockchain();
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
			//System.out.println(returnToSoupLocation);
			int x = 0;
			MapLocation[] soup = null;
			if(baseRefinery == null) {
				findHQ();
				baseRefinery = hqLoc;
			}
			/* THE FOLLOWING IS IN ORDER OF PREFERENCE OF A MINER's BEHAVIOR*/
			// Return to a previous mining spot
			if (returnToSoupLocation != null){
					x = -1;
			}

			// refine soup
			else if (rc.getSoupCarrying() >= 70){
				x = 1;
			}

			// find nearby soup
			else if(true){
				soup = rc.senseNearbySoup(-1);
				if (soup != null && soup.length != 0) {x=3;}// we found soup! Head towards it
			}

			// Look for soup on the blockchain
			// we aren't travelling to a soup/refinery location, look for one
			else{
				MapLocation blockchainRefineryDestination = blockchainSoup();
				if (blockchainRefineryDestination != null) {baseRefinery = blockchainRefineryDestination;} // there is a closer refinery to make our base
				x = 2;
			}

			//We may currently be pursuing a refinery (after before else if, or on a previous turn)
			//if (blockchainRefineryDestination != null && x==0){x = 2;}

			switch (x) {
				case -1: // return to a previous soup area after refining soup.
					System.out.println("RETURNING TO PREVIOUS LOCATION!!");
					while(!rc.canSenseLocation(returnToSoupLocation)){
						walkTowards(returnToSoupLocation); // just walking one more times towards it
					}
					returnToSoupLocation = null;
					/*
					if(rc.canSenseLocation(returnToSoupLocation)){
						walkTowards(returnToSoupLocation); // just walking one more times towards it
						returnToSoupLocation = null;
					} else {
						walkTowards(returnToSoupLocation);
					}*/
					break;
				case 1: // building a building and refining soup
					buildABuilding();
					/*if (rc.canSenseLocation(baseRefinery)){ // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
					//if (!walkTowards(baseRefinery) && rc.canSenseLocation(baseRefinery)){ // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
					if (!refineSoup()) {
						walkTowards(baseRefinery);
					}
					}*/
					/*MapLocation refineryLocation = findRefinery();
					if(refineryLocation != null){
						if (!refineSoup()) {
							walkTowards(refineryLocation);
						}
						//while(true){
						if(rc.canSenseLocation(refineryLocation)){
							walkTowards(refineryLocation);
						}
						if (!refineSoup()){break;}
						//if(rc.canSenseLocation(refineryLocation)){break;}
						//}
					}
					else {
						//while(true){
							walkTowards(baseRefinery);
							if (!refineSoup()){break;}
							//if(rc.canSenseLocation(baseRefinery)){break;}
						//}
					}*/
					if (returnToSoupLocation == null){ // to avoid resetting it again, this condition is necessary.
						returnToSoupLocation = rc.getLocation();
					}

					while (!refineSoup()) {
						MapLocation refineryLocation = findRefinery();
						if (refineryLocation != null){baseRefinery = refineryLocation;}
						walkTowards(baseRefinery);
						//walkTowardsBuilding();
					}
					 /*else {

					 }*/
					/*while(!refineSoup()) {
						walkTowards(baseRefinery);
						//walkTowardsBuilding();
					}*/
					/*if (!rc.canSenseLocation(baseRefinery)){ // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
						walkTowards(baseRefinery);
					}else{

					if (!walkTowards(baseRefinery) || rc.canSenseLocation(baseRefinery)){ // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
						if (!refineSoup()) {
							walkTowards(baseRefinery);
						}
					}
					}*/
					/*if (!refineSoup()) {
						walkTowardsBuilding();
					}*/


					break;
				case 2: //walk towards soup refinery
					if (!walkTowards(blockchainRefineryDestination) && rc.canSenseLocation(blockchainRefineryDestination)){ // we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
						oldSoupLocations.add(blockchainRefineryDestination); // TODO -- adds any soup location...could be a performance issue
						blockchainRefineryDestination = null;
					}
					break;
				case 3:
					//buildABuilding();
					if (diagonalDir == -1) { changeDirection();} // diagonal walking stuff
					if(!checkForSoup(soup)){goDiagonal();} // no soup around...walk diagonally
					break;

				default: {
									 if (diagonalDir == -1) { changeDirection();} // diagonal walking stuff
									 goDiagonal();
									 // We are pursuing a soup location
									 /*if (soupDestination != null){
										 if (!walkTowardsSoup(soupDestination) || rc.canSenseLocation(soupDestination)){

										 }
										 if(tryMine()){}*/
									 break;
									 }
				}
			}

			public boolean walkTowards(MapLocation x) throws GameActionException {
				System.out.println("Towards something!!");
				// move towards soup...if stuck, get unstuck.
				if (!nav.goAround(x) && rc.canSenseLocation(x)) { // we ran into something, and we are nearby the refinery (e.g., we bumped into the refinery)
					nav.goTo(Util.randomDirection());
					return false;
				}
				return true;
			}

			public boolean refineSoup() throws GameActionException {
				System.out.println("TRYING TO DEPOSIT SOUP...");
				System.out.println("Ret Soup Location: " + returnToSoupLocation);
				for (Direction dir : Util.directions)
					if (tryRefine(dir)) {
						System.out.println("SUCCESFULLY DEPOSITED SOUP");
						return true;
					}
				return false;
			}

			public boolean walkTowardsBuilding() throws GameActionException {
				MapLocation refineryLocation = findRefinery();
				if (refineryLocation != null) {
					while (true) {
						if (baseRefinery == null) { baseRefinery = refineryLocation;}
						System.out.println("Toward to Refinery!");
						// we ran into something, and we are nearby the refinery (i.e., we bumped into the refinery)
						if (!walkTowards(refineryLocation) && rc.canSenseLocation(refineryLocation)){break;}
					}
				} else if (baseRefinery != null) {
					while (true){
						if (!walkTowards(baseRefinery) && rc.canSenseLocation(baseRefinery)){break;}
					}
				} else if(baseRefinery == null) {
					System.out.println("Toward to HQ!");
					if (!walkTowards(hqLoc) && rc.canSenseLocation(hqLoc)){}
				}
				return true;
			}

			public Direction getSuccessfulMine(){
				return lastSuccessfulMine;
			}

			boolean tryMine() throws GameActionException {
				Direction successful = getSuccessfulMine();
				if (successful != null){
					if (rc.isReady() && rc.canMineSoup(successful)) {
						rc.mineSoup(successful);
						return true;
					}
				}

				for (Direction dir : Util.directions) {
					if (rc.isReady() && rc.canMineSoup(dir)) {
						rc.mineSoup(dir);
						lastSuccessfulMine = dir;
						return true;
					}
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
				if (localSoupLocations != null){ return true;}
				return false;
			}

		}

