package finalbota;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class BuildingManager {

    static RobotType getNextBuilding(Comm comm){
        int fulfillment = comm.buildings[RobotType.FULFILLMENT_CENTER.ordinal()];
        int design = comm.buildings[RobotType.DESIGN_SCHOOL.ordinal()];
        int vaporators = comm.buildings[RobotType.VAPORATOR.ordinal()];

        if (comm.isRush()){
            if (fulfillment == 0) return RobotType.FULFILLMENT_CENTER;
            if (design == 0) return RobotType.DESIGN_SCHOOL;
            //return null;
        } else{
            if (comm.upToDate() && fulfillment > 0 && comm.buildings[RobotType.DELIVERY_DRONE.ordinal()] == 0){
                return RobotType.DELIVERY_DRONE;
            }
        }

        if (10*fulfillment <= vaporators) {
            System.out.println(RobotType.FULFILLMENT_CENTER.name());
            return RobotType.FULFILLMENT_CENTER;
        }
        if (10*design <= vaporators && vaporators > 0) {
            System.out.println(RobotType.DESIGN_SCHOOL.name());
            return RobotType.DESIGN_SCHOOL;
        }
        /*
        int extraCash = 0;
        if (comm.shouldBuildVaporators()){
            System.out.println("Should build vaps!");
            extraCash += (int)(Constants.REINVESTMENT_CONSTANT*comm.soupGeneratedByVaporators);
        }
        if (nVaporators(comm.maxSoup, extraCash) > comm.buildings[RobotType.VAPORATOR.ordinal()]) {
            if (Constants.DEBUG == 1) System.out.println(RobotType.VAPORATOR.name());
            return RobotType.VAPORATOR;
        }

        if (comm.buildings[RobotType.DESIGN_SCHOOL.ordinal()] == 0) {
            System.out.println(RobotType.DESIGN_SCHOOL.name());
            return RobotType.DESIGN_SCHOOL;
        }*/
        if (comm.shouldBuildVaporators()) return RobotType.VAPORATOR;
        return RobotType.NET_GUN;
    }

    static int nVaporators(int soup, int extra){
        int extraSoup = (soup%300) + (soup/300)*230 + extra;
        return extraSoup/RobotType.VAPORATOR.cost;
    }

    static boolean shouldBuildDrone(Comm comm, RobotController rc){
        if (!comm.upToDate()) return false;
        RobotType r = getNextBuilding(comm);
        if (!comm.isRush() || rc.getRoundNum() > Constants.MAX_RUSH_TURN) {
            int price = 0;
            if (r != null) price += r.cost;
            System.out.println("checking drone building! " + price + " " + rc.getTeamSoup());
            if (price + RobotType.DELIVERY_DRONE.cost <= rc.getTeamSoup()) {
                //if (rc.getRoundNum() < ROUND_LANDSCAPERS_ECO) return true;
                if (rc.getTeamSoup() > price + RobotType.LANDSCAPER.cost + RobotType.DELIVERY_DRONE.cost) return true;
                int landscapers = getLandscapers(comm), drones = getDrones(comm);
                return drones <= landscapers;
            }
        }
        if (r != RobotType.VAPORATOR) return false;
        int vapor = comm.buildings[RobotType.VAPORATOR.ordinal()], drones = comm.buildings[RobotType.DELIVERY_DRONE.ordinal()];
        return drones <= vapor;
    }

    static boolean shouldBuildLandscaper(Comm comm, RobotController rc){
        if (!comm.upToDate()) return false;
        //if (rc.getRoundNum() < ROUND_LANDSCAPERS_ECO) return false;
        RobotType r = getNextBuilding(comm);
        if (!comm.isRush() || rc.getRoundNum() > Constants.MAX_RUSH_TURN) {
            int price = 0;
            if (r != null) price += r.cost;
            if (price + RobotType.LANDSCAPER.cost <= rc.getTeamSoup()) {
                if (rc.getTeamSoup() > price + RobotType.LANDSCAPER.cost + RobotType.DELIVERY_DRONE.cost) return true;
                int landscapers = getLandscapers(comm), drones = getDrones(comm);
                return drones > landscapers;
            }
        }
        if (r != RobotType.VAPORATOR) return false;
        int vapor = comm.buildings[RobotType.VAPORATOR.ordinal()], landscapers = comm.buildings[RobotType.LANDSCAPER.ordinal()];
        return landscapers < vapor;
    }

    static boolean haveSoupToSpawn(RobotController rc, RobotType r){
        return rc.getTeamSoup() > r.cost;
    }

    static int getDrones(Comm comm){
        return comm.buildings[RobotType.DELIVERY_DRONE.ordinal()];
    }

    static int getLandscapers(Comm comm){
        return comm.buildings[RobotType.LANDSCAPER.ordinal()] + comm.unitsPostClutch[RobotType.LANDSCAPER.ordinal()];
    }

    static void printDebug(Comm comm){
        if (Constants.DEBUG != 1) return;
        RobotType[] types = RobotType.values();
        for (RobotType t : types){
            System.out.println(t.name() + " " + comm.buildings[t.ordinal()]);
        }
    }

}
