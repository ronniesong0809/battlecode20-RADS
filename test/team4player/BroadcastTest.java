package team4player;

import battlecode.common.*;
import finalbota.HQ;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BroadcastTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    RobotController rcMock = mock(RobotController.class);
    @InjectMocks
    Broadcast broadMock = new Broadcast(rcMock);

    @Before
    public void setup() {
        when(rcMock.getTeam()).thenReturn(Team.A);
    }

    @Test
    public void sendHqLocToBlockchainTest() throws GameActionException {
        broadMock.sendHqLocToBlockchain(new MapLocation(1,1));
    }

    @Test
    public void getHqLocFromBlockchainTest() throws GameActionException {
        broadMock.getHqLocFromBlockchain();
    }

    @Test
    public void sendRefineryLocToBlockchainTest() throws GameActionException {
        int packedMessage = 0;
        int[] message = new int[7];
        packedMessage = (packedMessage << 6) + 1; // x
        packedMessage = (packedMessage << 6) + 1; // y
        packedMessage = (packedMessage << 6) + 63; //teamsecret2
        message[0] = packedMessage;
        when(rcMock.canSubmitTransaction(message, 4)).thenReturn(true);
        //rcMock.submitTransaction(message, 4);
        broadMock.sendRefineryLocToBlockchain(new MapLocation(1,1));
    }

    //public static MapLocation getRefineryLocFromBlockchain() throws GameActionException {
    @Test
    public void getRefineryLocFromBlockchainTest() throws GameActionException {
        broadMock.getRefineryLocFromBlockchain();
    }
}
