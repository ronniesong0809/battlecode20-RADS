package team4player;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner extends Unit{
    static int numDesignSchool = 0;
    static int numRefinery = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();

    public Miner(RobotController rc) {
        super(rc);
    }

		// Sense a building passes in
		public boolean senseBuilding(RobotType type){
			RobotInfo [] nearbyRobots = rc.senseNearbyRobots();
					for (RobotInfo r : nearbyRobots) {
							if (r.type == type) {
								return true;
							}
					}
					return false;
		}

		public void buildDesignSchoolOrRefinery() throws GameActionException{
        // Build design school
        if (numDesignSchool < 1 && !senseBuilding(RobotType.DESIGN_SCHOOL)) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, Util.randomDirection())) {
                numDesignSchool++;
                System.out.println("build a Design School");
            }
        }
				else if(!senseBuilding(RobotType.REFINERY)) tryBuild(RobotType.REFINERY);
		}

		public void checkForSoup() throws GameActionException{
				MapLocation[] soup = rc.senseNearbySoup(-1);
				if (soup != null && soup.length != 0) { // we found soup! Head towards it
						int randomLoc = (int) Math.random() * soup.length + 0; // random soup to avoid crowds
						walkTowardsSoup(soup, randomLoc);
						for (Direction dir : Util.directions){
								tryMine(dir);
						}
				} else {
					System.out.println("GOING RANDOM DIRECTION"); // we can be stuck
					nav.goTo(Util.randomDirection());
				}
		}

    public void takeTurn() throws GameActionException {
        super.takeTurn();
				int x = 0;
				if (rc.getSoupCarrying() >= 70) x=1;
				switch(x){
					case 1: refineSoup();
									break;
					default: {checkForSoup();break;}
				}
        /*
        numDesignSchool += bc.updateUnitCounts();
        //TODO -- broadcasting, checking if broadcast is stale
         bc.updateUnitCounts();
         bc.updateSoupLocations(soupLocations);
				*/
    }

    public void walkTowardsSoup(MapLocation [] soup, int randomLoc) throws GameActionException {
        //NOTE: do NOT use a while loop. Miners will go to old soup locations that are dried up if you do.
					System.out.println("Towards soup!");
					for (Direction dir : Util.directions) {
							if (rc.canMineSoup(dir)) {return;}
					}
					// move towards soup...if stuck, get unstuck.
					if (!nav.goTo(soup[randomLoc])) {nav.goTo(Util.randomDirection());}
    }

    public void refineSoup() throws GameActionException {
					buildDesignSchoolOrRefinery();
					MapLocation refineryLocation = findRefinery();
					while (rc.getSoupCarrying() >= 70) {
							System.out.println("at soup limit");
							if (refineryLocation != null) {
									while(true){
											System.out.println("Toward to Refinery!");
											if (nav.goTo(refineryLocation) == false) { break; }
									}
							}
							else{
									while(true){
											System.out.println("Toward to HQ!");
											if(nav.goTo(hqLoc) == false){ break;}
									}
							}
							System.out.println("TRYING TO DEPOSIT SOUP...");
							for (Direction dir : Util.directions)
									if(tryRefine(dir)){ System.out.println("SUCCESFULLY DEPOSITED SOUP"); break;}
							nav.goTo(Util.randomDirection());
					}
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
     //RobotType randomSpawnedByMiner() {
     //    return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
     //}

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

    void checkIfSoupGone() throws GameActionException {
        if (soupLocations.size() > 0) {
            MapLocation targetSoupLoc = soupLocations.get(0);
            if (rc.canSenseLocation(targetSoupLoc) && rc.senseSoup(targetSoupLoc) == 0) {
                soupLocations.remove(0);
            }
        }
    }

    MapLocation nearbySoup() throws GameActionException {
        MapLocation[] souplocations = rc.senseNearbySoup();
        for (MapLocation souploc : souplocations) {
            MapLocation soupLoc = rc.getLocation();
        }
        return null;
    }
}
