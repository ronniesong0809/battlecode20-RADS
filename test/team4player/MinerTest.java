package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class MinerTest {
    @Mock
    RobotController rcMock = mock(RobotController.class);

    @Mock
    Navigation navMock = mock(Navigation.class);

    @InjectMocks
    Miner minerMock = new Miner(rcMock);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
    }

    @Test
    public void senseBuildingTest() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(5, 5))});

        minerMock.senseBuilding(RobotType.HQ);
        verify(rcMock).senseNearbyRobots();
    }

    @Test
    public void checkForSoupTest() throws GameActionException {
        when(rcMock.senseNearbySoup(-1)).thenReturn(new MapLocation[]{new MapLocation(1,1)});
        when(rcMock.canMineSoup(Direction.CENTER)).thenReturn(true);
        when(navMock.goAround(new MapLocation(1,1))).thenReturn(false);

        minerMock.checkForSoup();
    }

    @Test
    public void walkTowardsSoup() throws GameActionException {
        when(rcMock.canMineSoup(Direction.CENTER)).thenReturn(true);
        when(navMock.goAround(new MapLocation(1,1))).thenReturn(false);

        minerMock.walkTowardsSoup(new MapLocation(1,1));
    }
		//New
    /*@Test
    public void refineSoup() throws GameActionException {
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canMineSoup(Direction.CENTER)).thenReturn(true);

				//TODO -- Ask Ronnie for help.
        //boolean result = minerMock.tryMine(Direction.CENTER);
        boolean result = minerMock.tryMine();
        //assertTrue(result);
    }*/

		//Old
    @Test
    public void refineSoup() throws GameActionException {
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canMineSoup(Direction.CENTER)).thenReturn(true);

        boolean result = minerMock.tryMine(Direction.CENTER);
        assertTrue(result);
    }

    @Test
    public void tryRefine() throws GameActionException {
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canDepositSoup(Direction.CENTER)).thenReturn(true);
        when(rcMock.getSoupCarrying()).thenReturn(1);

        boolean result = minerMock.tryRefine(Direction.CENTER);
        assertTrue(result);
    }
}
