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
import static org.mockito.Mockito.*;


public class UnitTest {
    @Mock
    RobotController rcMock = mock(RobotController.class);

    @InjectMocks
    Unit unitMock = new Unit(rcMock);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{});
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
    }

    @Test
    public void findHQ() throws GameActionException {
        unitMock.findHQ();
        verify(rcMock).senseNearbyRobots();
        verify(rcMock, times(0)).getTeam();
    }

    @Test
    public void findRefineryTest() throws GameActionException {
        MapLocation result = unitMock.findRefinery();
        assertEquals(result, null);
    }

    @Test
    public void nearbyRobotTest() throws GameActionException {
        boolean result = unitMock.nearbyRobot(RobotType.HQ);
        assertEquals(result, false);
    }
}