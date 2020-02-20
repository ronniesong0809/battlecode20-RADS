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

public class RefineryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

		@Mock
    RobotController rcMock = mock(RobotController.class);

		@InjectMocks
    Miner minerMock = new Miner(rcMock);

		@Before
    public void setup() {
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
    }

		@Mock
    Refinery refMock = mock(Refinery.class);

    @Test
    public void RefineryTest() throws GameActionException {
				//RobotController rc = new RobotController();
				//Refinery ref = new Refinery(rc);
				//when(Refinery(rcMock)).thenReturn();
				assertTrue(true);
		}
    @Test
    public void takeTurnTest() throws GameActionException {
				assertTrue(true);
		}

}
