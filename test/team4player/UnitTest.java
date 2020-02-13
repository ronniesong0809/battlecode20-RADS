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
    public void setup() {
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
    }

    @Test
    public void findHQ() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(5, 5))});

        unitMock.findHQ();
        verify(rcMock).senseNearbyRobots();
        verify(rcMock).getTeam();
    }

    @Test
    public void findRefineryTest() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.REFINERY, 0, false, 0, 0, 0, new MapLocation(10, 10))});

        MapLocation result = unitMock.findRefinery();
        assertEquals(result, new MapLocation(10, 10));
    }

    @Test
    public void nearbyRobotTest() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(5, 5))});

        boolean result = unitMock.nearbyRobot(RobotType.HQ);
        assertEquals(result, true);
    }
}