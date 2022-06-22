package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.certification.RobotControl;
import thkoeln.coco.ad.core.TestHelper;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.domainprimitives.path.PathModel;
import thkoeln.coco.ad.robot.domain.MovingService;
import thkoeln.coco.ad.robot.domain.PathFindingService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PathFindingAndMovingTests {
    @Autowired
    private RobotControl robotControl;
    @Autowired
    private PlanetChecking planetChecking;
    @Autowired
    private PathFindingService pathFindingService;
    @Autowired
    private MovingService movingService;

    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        UUID originId = robotControl.resetAll();
    }

    @Test
    public void pathIsLegal() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robotId );
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();
        UUID p3Id = UUID.randomUUID();
        UUID p4Id = UUID.randomUUID();
        planetChecking.neighboursDetected( homeId, null, null, null, p1Id );
        planetChecking.neighboursDetected( p1Id, null, homeId, null, p2Id );
        planetChecking.neighboursDetected( p2Id, p3Id, p1Id, null, null );
        planetChecking.neighboursDetected( p3Id, p4Id, null, p2Id, null );
        planetChecking.neighboursDetected( p4Id, null, null, p3Id, null);

        // when
        PathModel path = pathFindingService.calculatePath( robotId, p4Id);
        for (Coordinate coordinate : path.getSteps()){
            System.out.println(String.format("x: %d <> y: %d", coordinate.getX(), coordinate.getY()));
        }

        // then
        assertTrue(path.isLegal());
    }

    @Test
    public void pathIsAlsoLegalInOppositeDirection() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robotId );
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();
        UUID p3Id = UUID.randomUUID();
        UUID p4Id = UUID.randomUUID();
        planetChecking.neighboursDetected( homeId, null, null, null, p1Id );
        planetChecking.neighboursDetected( p1Id, null, homeId, null, p2Id );
        planetChecking.neighboursDetected( p2Id, p3Id, p1Id, null, null );
        planetChecking.neighboursDetected( p3Id, p4Id, null, p2Id, null );
        planetChecking.neighboursDetected( p4Id, null, null, p3Id, null);
        robotControl.executeOrder( "[west," + robotId + "]" );
        robotControl.executeOrder( "[west," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );

        // when
        PathModel path = pathFindingService.calculatePath( robotId, homeId);
        for (Coordinate coordinate : path.getSteps()){
            System.out.println(String.format("x: %d <> y: %d", coordinate.getX(), coordinate.getY()));
        }

        // then
        assertTrue(path.isLegal());
    }

    @Test
    public void moveOnFoundPath() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robotId );
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();
        UUID p3Id = UUID.randomUUID();
        UUID p4Id = UUID.randomUUID();
        planetChecking.neighboursDetected( homeId, null, null, null, p1Id );
        planetChecking.neighboursDetected( p1Id, null, homeId, null, p2Id );
        planetChecking.neighboursDetected( p2Id, p3Id, p1Id, null, null );
        planetChecking.neighboursDetected( p3Id, p4Id, null, p2Id, null );
        planetChecking.neighboursDetected( p4Id, null, null, p3Id, null);

        System.out.println(homeId + "\n" + p1Id + "\n" + p2Id + "\n" + p3Id + "\n" + p4Id);

        // when
        PathModel path = pathFindingService.calculatePath( robotId, p4Id );
        movingService.executeMoveOnPath( path );
        UUID actualRobotPositionAfterMove = robotControl.getRobotPlanet( robotId );

        // then
        assertEquals( p4Id, actualRobotPositionAfterMove );
    }
}
