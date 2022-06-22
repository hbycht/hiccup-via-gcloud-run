package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.certification.RobotControl;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.planet.presentation.PlanetModel;
import thkoeln.coco.ad.robot.domain.Robot;
import thkoeln.coco.ad.robot.presentation.RobotModel;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ModelTests {
    @Autowired
    private RobotControl robotControl;
    @Autowired
    private PlanetChecking planetChecking;

    @Test
    public void planetModelShouldReturnModelOfPlanet(){
        // given
        UUID id = UUID.randomUUID();
        Planet planet = new Planet( id, Coordinate.fromXY(3, -4 ) );
        PlanetModel planetModel = PlanetModel.fromPlanet( planet );

        // when
        String returnedId = planetModel.getId();
        Integer resourceAmount = planetModel.getResourceAmount();
        Coordinate position = planetModel.getCoordinate();
        String type = planetModel.getType();

        // then
        assertEquals( returnedId, id.toString() );
        assertEquals( resourceAmount, 0 );
        assertEquals( position, Coordinate.fromXY( 3, -4 ) );
        assertEquals( type, "unknown" );
    }

    @Test
    public void robotModelShouldReturnModelOfRobot(){
        // given
        UUID id = UUID.randomUUID();
        UUID planetId = UUID.randomUUID();
        Planet planet = new Planet( id, Coordinate.fromXY(3, -4 ) );
        Robot robot = new Robot( id, planet );
        RobotModel robotModel = RobotModel.fromRobot( robot );

        // when
        String returnedId = robotModel.getId();
        Integer resourceAmount = robotModel.getCargoAmount();
        Coordinate position = robotModel.getCurrentPosition();

        // then
        assertEquals( returnedId, id.toString() );
        assertEquals( resourceAmount, 0 );
        assertEquals( position, Coordinate.fromXY( 3, -4 ) );
    }
}
