package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DesignSchoolTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    RobotController rcMock = mock(RobotController.class);

    @Mock
    Util utilMock = mock(Util.class);

    @InjectMocks
    DesignSchool dsMock = new DesignSchool(rcMock);

    @Test
    public void takeTurnTest() throws GameActionException {
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canBuildRobot(RobotType.LANDSCAPER, Direction.NORTH)).thenReturn(true);
        for(int i =0; i< 15;i++)
            dsMock.takeTurn();
    }
}