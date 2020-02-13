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
    @Mock
    Robot robotMock = mock(Robot.class);

    @Mock
    RobotController rcMock = mock(RobotController.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws GameActionException{
        if(rcMock.senseFlooding(new MapLocation(3,3))) {
            when(robotMock.tryBuild(RobotType.MINER, new MapLocation(5, 5))).thenReturn(false);
        }else{
            when(robotMock.tryBuild(RobotType.MINER, new MapLocation(5, 5))).thenReturn(true);
        }
    }

    @Test
    public void tryBuild() throws GameActionException{
        boolean result;
        if (rcMock.senseFlooding(new MapLocation(3,3)))
            result = robotMock.tryBuild(RobotType.MINER, new MapLocation(5, 5));
        else
            result = false;

        assertEquals(result, false);
    }

    @Test
    public void tryBuildTest2() throws GameActionException{
        boolean result;
        if (!rcMock.senseFlooding(new MapLocation(3,3)))
            result = robotMock.tryBuild(RobotType.MINER, new MapLocation(5, 5));
        else
            result = false;

        assertEquals(result, true);
    }
}
