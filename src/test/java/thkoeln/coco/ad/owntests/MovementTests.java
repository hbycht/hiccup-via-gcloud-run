package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.certification.RobotControl;
import thkoeln.coco.ad.certification.RobotControlException;
import thkoeln.coco.ad.core.TestHelper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovementTests {
    @Autowired
    private RobotControl robotControl;
    @Autowired
    private PlanetChecking planetChecking;

    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        UUID originId = robotControl.resetAll();
    }

    @Test
    public void shouldReturnPositionWestIfBotMovesSouthNorthWest() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robotId );
        UUID northId = UUID.randomUUID();
        UUID eastId = UUID.randomUUID();
        UUID southId = UUID.randomUUID();
        UUID westId = UUID.randomUUID();
        System.out.println("home: " + homeId +
                "\nnorth: " + northId +
                "\neast: " + eastId +
                "\nsouth: " + southId +
                "\nwest: " + westId);
        planetChecking.neighboursDetected( homeId, northId, eastId, southId, westId );
        planetChecking.neighboursDetected( northId, null, null, homeId, null );
        planetChecking.neighboursDetected( eastId, null, null, null, homeId );
        planetChecking.neighboursDetected( southId, homeId, null, null, null );
        planetChecking.neighboursDetected( westId, null, homeId, null, null );

        // when
        robotControl.executeOrder( "[south," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );
        robotControl.executeOrder( "[west," + robotId + "]" );

        // then
        assertEquals( westId, robotControl.getRobotPlanet( robotId ) );
    }

    @Test
    public void planetTypeShouldChangeAfterVisiting() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robotId );
        UUID northId = UUID.randomUUID();
        UUID eastId = UUID.randomUUID();
        UUID southId = UUID.randomUUID();
        UUID westId = UUID.randomUUID();
        planetChecking.neighboursDetected( homeId, northId, eastId, southId, westId );

        // when
        robotControl.executeOrder( "[south," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );

        // then
        assertEquals( northId, robotControl.getRobotPlanet( robotId ) );
        assertEquals( "space shipyard", robotControl.getPlanetType( homeId ) );
        assertEquals( "regular", robotControl.getPlanetType( northId ) );
        assertEquals( "unknown", robotControl.getPlanetType( eastId ) );
        assertEquals( "regular", robotControl.getPlanetType( southId ) );
        assertEquals( "unknown", robotControl.getPlanetType( westId ) );
    }

    @Test
    public void planetShouldAutowireKnownPlanets() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robotId );
        UUID northId = UUID.randomUUID();
        UUID eastId = UUID.randomUUID();
        UUID southId = UUID.randomUUID();
        UUID westId = UUID.randomUUID();
        System.out.println("home: " + homeId +
                "\nnorth: " + northId +
                "\neast: " + eastId +
                "\nsouth: " + southId +
                "\nwest: " + westId);
        planetChecking.neighboursDetected( homeId, northId, eastId, southId, westId );

        // when
        robotControl.executeOrder( "[south," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );
        robotControl.executeOrder( "[north," + robotId + "]" );

        // then
        assertEquals( northId, robotControl.getRobotPlanet( robotId ) );
    }

    @Test
    public void planetShouldAutowireKnownPlanetsEvenAroundCorner() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID robot2 = testHelper.performCreate( robotControl );
        UUID homeId = robotControl.getRobotPlanet( robot1 );
        UUID northId = UUID.randomUUID();
        UUID eastId = UUID.randomUUID();
        UUID southId = UUID.randomUUID();
        UUID westId = UUID.randomUUID();
        UUID extraId = UUID.randomUUID();
        System.out.println("home: " + homeId +
                "\nnorth: " + northId +
                "\neast: " + eastId +
                "\nsouth: " + southId +
                "\nwest: " + westId);
        planetChecking.neighboursDetected( homeId, northId, eastId, southId, westId );
        planetChecking.neighboursDetected( northId, null, null, homeId, extraId );

        // when
        robotControl.executeOrder( "[west," + robot1 + "]" );
        robotControl.executeOrder( "[north," + robot1 + "]" );
        robotControl.executeOrder( "[north," + robot2 + "]" );
        robotControl.executeOrder( "[west," + robot2 + "]" );

        // then
        assertEquals( robotControl.getRobotPlanet( robot1 ), robotControl.getRobotPlanet( robot2 ) );
    }

    @Test
    public void robotExploresPlanets() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID p0 = robotControl.getRobotPlanet( robot1 );
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        UUID p4 = UUID.randomUUID();
        UUID p5 = UUID.randomUUID();
        UUID p6 = UUID.randomUUID();
        UUID p7 = UUID.randomUUID();
        System.out.println("p0: " + p0 +
                "\np1: " + p1 +
                "\np2: " + p2 +
                "\np3: " + p3 +
                "\np4: " + p4 +
                "\np5: " + p5 +
                "\np6: " + p6 +
                "\np7: " + p7);
        planetChecking.neighboursDetected( p0, p1, p2, p3, p4 );
        planetChecking.neighboursDetected( p1, p5, p6, p0, p7 );

        // when
        robotControl.executeOrder( "[explore," + robot1 + "]" );

        // then
        assertNotEquals( p0, robotControl.getRobotPlanet( robot1 ) );
    }

    @Test
    public void robotExploresTheOneUnknownPlanet() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID p0 = robotControl.getRobotPlanet( robot1 );
        UUID p1 = UUID.randomUUID();
        planetChecking.neighboursDetected( p0, p1, null, null, null );

        System.out.println("p0: " + p0 + "\n" +
                "p1: " + p1);

        // when
        robotControl.executeOrder( "[explore," + robot1 + "]" );

        // then
        assertEquals( p1, robotControl.getRobotPlanet( robot1 ) );
    }

    @Test
    public void robotExploresTheOtherAvailablePlanetEvenIfHeVisitedBefore() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID p0 = robotControl.getRobotPlanet( robot1 );
        UUID p1 = UUID.randomUUID();
        planetChecking.neighboursDetected( p0, p1, null, null, null );

        System.out.println("p0: " + p0 + "\n" +
                "p1: " + p1);

        // when
        robotControl.executeOrder( "[north," + robot1 + "]" );
        robotControl.executeOrder( "[explore," + robot1 + "]" );

        // then
        assertEquals( p0, robotControl.getRobotPlanet( robot1 ) );
    }

    @Test
    public void exploreOrderThrowsExceptionIfPlanetHasNoNeighbours() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID p0 = robotControl.getRobotPlanet( robot1 );

        // when

        // then
        assertThrows( RobotControlException.class, () -> robotControl.executeOrder( "[explore," + robot1 + "]" ));
    }

    @Test
    public void shouldReturnAtHomeAfterNorthNorthGohomeGohome() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID p0 = robotControl.getRobotPlanet( robot1 );
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        planetChecking.neighboursDetected( p0, p1, null, null, null );
        planetChecking.neighboursDetected( p1, p2, null, p0, null );

        // when
        robotControl.executeOrder( "[north," + robot1 + "]" );
        robotControl.executeOrder( "[north," + robot1 + "]" );
        assertEquals( p2, robotControl.getRobotPlanet( robot1 ) );
        robotControl.executeOrder( "[gohome," + robot1 + "]" );
        robotControl.executeOrder( "[gohome," + robot1 + "]" );

        // then
        assertEquals( p0, robotControl.getRobotPlanet( robot1 ) );
    }

    @Test
    public void shouldThrowErrorIfRobotIsAtHomeButIncomingOrderGohome() {
        // given
        UUID robot1 = testHelper.performCreate( robotControl );
        UUID p0 = robotControl.getRobotPlanet( robot1 );
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        planetChecking.neighboursDetected( p0, p1, null, null, p2 );

        // when
        robotControl.executeOrder( "[north," + robot1 + "]" );
        robotControl.executeOrder( "[south," + robot1 + "]" );
        robotControl.executeOrder( "[west," + robot1 + "]" );
        robotControl.executeOrder( "[gohome," + robot1 + "]" );

        // then
        assertThrows( RobotControlException.class, () -> robotControl.executeOrder( "[gohome," + robot1 + "]" ));
    }

}
