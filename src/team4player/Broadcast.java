package team4player;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Transaction;

import java.util.ArrayList;

public class Broadcast {
    static final int hqTeamSecret = 11;
    static final int teamSecret = 4444444;
    static final int teamSecret2 = 63;
    static final String[] messageType = {"HQ loc", "design school created", "soup location"};
    private static RobotController rc;

    public Broadcast(RobotController r) {
        rc = r;
    }

    //FIXME Optimized version for HQ is below
    public static int sendHqLocToBlockchain(MapLocation loc) throws GameActionException {
        int packedMessage = 0;
        int[] message = new int[7];
        packedMessage = (packedMessage << 6) + loc.x;
        packedMessage = (packedMessage << 6) + loc.y;
        packedMessage = (packedMessage << 6) + hqTeamSecret;
        System.out.println("SENDING: " + packedMessage);
        message[0] = packedMessage;

        if (rc.canSubmitTransaction(message, 4)) {
            rc.submitTransaction(message, 4);
            return message[0];
        }
        return 0;
    }

    public static MapLocation getHqLocFromBlockchain() throws GameActionException {
        System.out.println("BLOCKCHAIN! (HQ)");
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                int secret = mess[0] & 0b111111;
                if (secret == hqTeamSecret) {
                    int originaly = (mess[0] >> 6) & 0b111111;
                    int originalx = (mess[0] >> 12) & 0b111111;
                    return new MapLocation(originalx, originaly);
                }
            }
        }
        return null;
    }
    public static int sendRefineryLocToBlockchain(MapLocation loc) throws GameActionException {
        int packedMessage = 0;
        int[] message = new int[7];
        packedMessage = (packedMessage << 6) + loc.x;
        packedMessage = (packedMessage << 6) + loc.y;
        packedMessage = (packedMessage << 6) + teamSecret2;
        //System.out.println("SENDING: " + packedMessage);
        message[0] = packedMessage;

        if (rc.canSubmitTransaction(message, 4)) {
            rc.submitTransaction(message, 4);
            return message[0];
        }
        return 0;
    }

    public static MapLocation[] getRefineryLocFromBlockchain() throws GameActionException {
        MapLocation[] msgs = new MapLocation[7];
        int j = 0; // we need to add in the messages we find into MapLocation [] msgs in proper index.
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                int secret = mess[0] & 0b111111;
                int originaly = (mess[0] >> 6) & 0b111111;
                int originalx = (mess[0] >> 12) & 0b111111;
                if (secret == teamSecret2) {
                    msgs[j] = new MapLocation(originalx, originaly);
                    ++j;
                }
            }
        }
        return msgs;
    }
    // This is used...but in a weird way (To prevent fulfillment centers from being created too many times
    public boolean readDesignSchoolCreation() throws GameActionException {
        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] mess = tx.getMessage();
            if (mess[0] == teamSecret && mess[1] == 220022) {
                return true;
            }
        }
        return false;
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


}
