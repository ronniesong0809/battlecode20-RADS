package team4player;

import battlecode.common.*;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HQTest {

    @Mock
    RobotController rcMock = mock(RobotController.class);
    @Mock
    Broadcast bc = mock(Broadcast.class);
    @InjectMocks
    HQ hqMock = new HQ(rcMock);

    @Test
    public void takeTurnTest() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, Team.B)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.DELIVERY_DRONE, 0, false, 0, 0, 0, new MapLocation(5, 5))});
        when(rcMock.canShootUnit(12)).thenReturn(true);
        hqMock.takeTurn();
    }

    @Test
    public void initSurroundingWallsTest() {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        hqMock.initSurroundingWalls();
    }

    @Test
    public void testCheckIfWallCompleteTest() throws Exception {
        initSurroundingWallsTest();

        boolean result = hqMock.checkIfWallComplete();
        assertFalse(result);
    }
}