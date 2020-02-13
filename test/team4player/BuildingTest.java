package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

public class BuildingTest {
    @Mock
    RobotController rcMock = mock(RobotController.class);

    @InjectMocks
    Building buildingMock = new Building(rcMock);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup(){
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, Team.B)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.DELIVERY_DRONE, 0, false, 0, 0, 0, new MapLocation(5, 5))});
        when(rcMock.canShootUnit(12)).thenReturn(true);
    }

    @Test
    public void shootDroneTest() throws GameActionException {
        buildingMock.shootDrone();
        verify(rcMock).canShootUnit(12);
    }
}
