package team4player;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UnitTest {
    @Mock
    Unit unitMock = mock(Unit.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws GameActionException {
        when(unitMock.findRefinery()).thenReturn(new MapLocation(5, 5));
        when(unitMock.nearbyRobot(RobotType.MINER)).thenReturn(true);
    }

    @Test
    public void findRefineryTest() throws GameActionException {
        MapLocation result = unitMock.findRefinery();
        assertEquals(result, new MapLocation(5, 5));
    }

    @Test
    public void nearbyRobotTest() throws GameActionException {
        boolean result = unitMock.nearbyRobot(RobotType.MINER);
        assertEquals(result, true);
    }
}