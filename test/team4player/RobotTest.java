package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RobotTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    RobotController rcMock = mock(RobotController.class);
    @InjectMocks
    Robot robotMock = new Robot(rcMock);

    @Test
    public void tryBuildMapLocation() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canBuildRobot(RobotType.MINER, Direction.NORTH)).thenReturn(true);

        boolean result = robotMock.tryBuild(RobotType.MINER, new MapLocation(50, 50));
        assertEquals(result, true);
    }

    @Test
    public void tryBuildMapLocation2() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canBuildRobot(RobotType.MINER, Direction.NORTH)).thenReturn(true);

        boolean result = robotMock.tryBuild(RobotType.MINER, new MapLocation(5, 5));
        assertEquals(result, false);
    }

    @Test
    public void tryBuildDirectionTest() throws GameActionException {
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canBuildRobot(RobotType.MINER, Direction.NORTH)).thenReturn(true);

        boolean result = robotMock.tryBuild(RobotType.MINER, Direction.NORTH);
        assertEquals(result, true);
    }

    @Test
    public void tryBuildDirectionTest2() throws GameActionException {
        when(rcMock.isReady()).thenReturn(false);
        when(rcMock.canBuildRobot(RobotType.MINER, Direction.NORTH)).thenReturn(true);

        boolean result = robotMock.tryBuild(RobotType.MINER, Direction.NORTH);
        assertEquals(result, false);
    }
}
