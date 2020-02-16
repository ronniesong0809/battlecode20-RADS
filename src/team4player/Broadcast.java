package team4player;
import battlecode.common.*;
import java.util.ArrayList;

public class Broadcast {
    private static RobotController rc;
    static final int teamSecret = 4444444;
    static final int teamSecret2 = 63;
    static final String[] messageType = {"HQ loc", "design school created", "soup location"};

    public Broadcast(RobotController r) {
        rc = r;
    }

    public static int sendHqLocToBlockchain(MapLocation loc) throws GameActionException {
        int packedMessage = 0;
        int[] message = new int[7];
        packedMessage = (packedMessage << 6) + loc.x;
        packedMessage = (packedMessage << 6) + loc.y;
        packedMessage = (packedMessage << 6) + teamSecret;
				System.out.println("SENDING: " + packedMessage);
        message[0] = packedMessage;

        if (rc.canSubmitTransaction(message, 4)){
            rc.submitTransaction(message,4);
						return message[0];
        }
				return 0;
    }

    public static MapLocation getHqLocFromBlockchain() throws GameActionException {
        System.out.println("BLOCKCHAIN! (HQ)");
				//MapLocation [] msgs = new MapLocation[7];
				int j = 0; // keeps track of where we insert into msgs.
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
								int secret = mess[0] & 0b111111;
								int originaly = (mess[0] >> 6) & 0b111111;
								int originalx = (mess[0] >> 12) & 0b111111;
                if(secret == teamSecret){
										System.out.println(secret);
										System.out.println(originaly);
										System.out.println(originalx);
                    System.out.println("found the HQ!  " + secret + " " + originalx + " " + originaly);
                    return new MapLocation(originalx, originaly);
                }
            }
        }
        //return msgs;
				return null;
    }

    /*public static void sendHqLocToBlockchain(MapLocation loc) throws GameActionException {
		//	return;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 101101101;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
        }
    }

    public static MapLocation getHqLocFromBlockchain() throws GameActionException {
		//	return null;
        System.out.println("BLOCKCHAIN!");
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
                if(mess[0] == teamSecret && mess[1]==101101101){
                    System.out.println("found the HQ!");
                    return new MapLocation(mess[2], mess[3]);
                }
            }
        }
        return null;
    }*/

    /*public static void sendRefineryLocToBlockchain(MapLocation loc) throws GameActionException {
        int[] message = new int[7];
        message[0] = teamSecret2;
        message[1] = 101101101;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
        }
    }

    public static MapLocation getRefineryLocFromBlockchain() throws GameActionException {
        System.out.println("BLOCKCHAIN!");
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
								System.out.println(mess[0]);
								System.out.println(mess[2]);
								System.out.println(mess[3]);
                if(mess[0] == teamSecret2 && mess[1]==101101101){
                    System.out.println("found the REFINERY!");
                    return new MapLocation(mess[2], mess[3]);
                }
            }
        }
        return null;
    }*/

    public static int sendRefineryLocToBlockchain(MapLocation loc) throws GameActionException {
        int packedMessage = 0;
        int[] message = new int[7];
        packedMessage = (packedMessage << 6) + loc.x;
        //System.out.println(Integer.toString(packedMessage, 2));
        packedMessage = (packedMessage << 6) + loc.y;
        //System.out.println(Integer.toString(packedMessage, 2));
        packedMessage = (packedMessage << 6) + teamSecret2;
        //System.out.println(Integer.toString(packedMessage, 2));
				System.out.println("SENDING: " + packedMessage);
        message[0] = packedMessage;

        if (rc.canSubmitTransaction(message, 4)){
            rc.submitTransaction(message,4);
						return message[0];
        }
				return 0;
    }

		/*TODO
			This function does not capture an edge case where more than one refinery location
			exists on the blockchain, it only grabs the first one it finds. This is okay in most cases,
			but should be changed in the future to return all locations matching the teamSecret2.
		*/
    //public static MapLocation getRefineryLocFromBlockchain() throws GameActionException {
    public static MapLocation [] getRefineryLocFromBlockchain() throws GameActionException {
        System.out.println("BLOCKCHAIN! (refinery)");
				MapLocation [] msgs = new MapLocation[7];
				int j = 0; // keeps track of where we insert into msgs.
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
								int secret = mess[0] & 0b111111;
								int originaly = (mess[0] >> 6) & 0b111111;
								int originalx = (mess[0] >> 12) & 0b111111;
								System.out.println(secret);
								System.out.println(originaly);
								System.out.println(originalx);
                if(secret == teamSecret2){
                    System.out.println("found a refinery!  " + secret + " " + originalx + " " + originaly);
                    //return new MapLocation(originalx, originaly);
										msgs[j] = new MapLocation(originalx, originaly);
										++j;
                }
            }
        }
        return msgs;
				//return null;
    }

    public void broadcastFulfillmentCenterCreation() throws GameActionException {
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 330033;
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
        }
    }

    public boolean readFCCreation() throws GameActionException {
        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] mess = tx.getMessage();
            if (mess[0] == teamSecret && mess[1] == 330033) {
                return true;
            }
        }
        return false;
    }

    public void broadcastDesignSchoolCreation() throws GameActionException {

        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 220022;
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
        }
    }

    public boolean readDesignSchoolCreation() throws GameActionException {
        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] mess = tx.getMessage();
            if (mess[0] == teamSecret && mess[1] == 220022) {
                return true;
            }
        }
        return false;
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

    public void broadcastInitialWallComplete(int height)  throws GameActionException {
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 101000101;
        message[2] = height;
        if (rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message,3);
        }
    }

    public int readInitialWallComplete() throws GameActionException {
        System.out.println("BLOCKCHAIN!");
        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx: rc.getBlock(i)){
                int[] mess = tx.getMessage();
                if(mess[0] == teamSecret && mess[1]==101000101){
                    System.out.println("Initial Wall Complete");
                    return mess[2];
                }
            }
        }
        return 0;
    }
}
