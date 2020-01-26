package team4player;
import battlecode.common.*;
import java.util.ArrayList;

public class Broadcast {
    private static RobotController rc;
    static final int teamSecret = 4444444;
    static final String[] messageType = {"HQ loc", "design school created", "soup location"};

    public Broadcast(RobotController r) {
        rc = r;
    }

    public static void sendHqLoc(MapLocation loc) throws GameActionException {
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 0;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
        }
    }

    public static MapLocation getHqLocFromBlockchain() throws GameActionException {
        System.out.println("BLOCKCHAIN!");
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
                if(mess[0] == teamSecret && mess[1]==0){
                    System.out.println("found the HQ!");
                    return new MapLocation(mess[2], mess[3]);
                }
            }
        }
        return null;
    }

    public boolean broadcastCreation = false;

    public void broadcastDesignSchoolCreation(MapLocation loc) throws GameActionException {
        if(broadcastCreation) return;

        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 1;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
            broadcastCreation = true;
        }
    }

    public int updateUnitCounts() throws GameActionException {
        int count = 0;
        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] mess = tx.getMessage();
            if (mess[0] == teamSecret && mess[1] == 1) {
                System.out.println("unit count!");
                count += 1;
            }
        }
        return count;
    }

    public void broadcastSoupLocation(MapLocation loc) throws GameActionException {
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 2;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
            System.out.println("found soup at " + loc);
        }
    }

    public void updateSoupLocations(ArrayList<MapLocation> soupLocations) throws GameActionException {
        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] mess = tx.getMessage();
            if (mess[0] == teamSecret && mess[1] == 2) {
                System.out.println("heard new soup!");
                soupLocations.add(new MapLocation(mess[2], mess[3]));
            }
        }
    }

//    void tryBlockchain() throws GameActionException {
//        if (turnCount < 3) {
//            int[] message = new int[7];
//            for (int i = 0; i < 7; i++) {
//                message[i] = 123;
//            }
//            if (rc.canSubmitTransaction(message, 10))
//                rc.submitTransaction(message, 10);
//        }
//        // System.out.println(rc.getRoundMessages(turnCount-1));
//    }


}
