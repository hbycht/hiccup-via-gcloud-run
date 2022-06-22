package thkoeln.coco.ad;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class E1MovementTests {
    @Autowired
    private RobotControl robotControl;
    @Autowired
    private PlanetChecking planetChecking;

    private TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        UUID originId = robotControl.resetAll();
        testHelper.setUpGalaxy( planetChecking,
                "7,8,9,13", "1,3,5,11", "5,3,9,31",
                originId );
    }

    @Test
    public void test0PlanetTypeOk() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );

        // when
        robotControl.executeOrder( "[north," + robotId.toString() + "]" );
        UUID planetId = robotControl.getRobotPlanet( robotId );
        UUID planetId6 = testHelper.getPlanetId( 6 );
        UUID planetId11 = testHelper.getPlanetId( 11 );

        // then
        assertEquals( "regular", robotControl.getPlanetType( planetId ) );
        assertEquals( "unknown", robotControl.getPlanetType( planetId6 ) );
        assertEquals( "unknown", robotControl.getPlanetType( planetId11 ) );
    }


    @Test
    public void test1MoveAgainstBlock() {
        UUID robotId = testHelper.performActions( robotControl, "ccc;north;west;south;south;east;south;0" );
        testHelper.performActions( robotControl, "east;-1", robotId );
    }

    @Test
    public void test2HarvestMultipleTimes() {
        // given
        UUID robotId = testHelper.performActions( robotControl, "ccc;south;mmm;south;mmm;20" );

        // when
        UUID planetId = robotControl.getRobotPlanet( robotId );

        // then
        assertEquals( Integer.valueOf( "14" ),
                robotControl.getPlanetUraniumAmount( planetId ) );
    }

    @Test
    public void test3TwoRobotsOnSamePlanet() {
        // given
        UUID robotIdA = testHelper.performActions( robotControl, "ccc;north;west;0" );
        UUID robotIdB = testHelper.performActions( robotControl, "ccc;west;north;0" );

        // when
        UUID planetIdA = robotControl.getRobotPlanet( robotIdA );
        UUID planetIdB = robotControl.getRobotPlanet( robotIdB );

        // then
        assertEquals( planetIdA, planetIdB );
        List<UUID> list = robotControl.getPlanetRobots( planetIdB );
        assertEquals( 2, list.size() );
    }


    @Test
    public void testExploreAndGoHome() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );
        robotControl.executeOrder( "[explore," + robotId.toString() + "]" );
        UUID planetId = robotControl.getRobotPlanet( robotId );
        assertNotEquals( "space shipyard", robotControl.getPlanetType( planetId ) );

        // when --- four more explorations, and five times go home ...
        for ( int i = 0; i < 4; i++ ) {
            robotControl.executeOrder( "[explore," + robotId.toString() + "]" );
        }
        for ( int i = 0; i < 5; i++ ) {
            // wrap in RobotControlException, in case robot arrives "early" at space shipyard
            try {
                robotControl.executeOrder( "[gohome," + robotId.toString() + "]" );
            }
            catch ( RobotControlException e ) {}
        }

        // then --- must be back on space shipyard
        planetId = robotControl.getRobotPlanet( robotId );
        assertEquals( "space shipyard", robotControl.getPlanetType( planetId ) );
    }


    @Test
    public void test4TwoRobotsHarvestOnSamePlanet() {
        // given
        UUID robotIdA = testHelper.performActions( robotControl, "ccc;north;west;south;south;east;south;0" );
        UUID robotIdB = testHelper.performActions( robotControl, "ccc;north;mmm;west;south;south;east;south;5" );

        // when --- only A is allowed to harvest first, since it is empty
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( "[harvest," + robotIdB.toString() + "]" );
        }, "Error expected, as " + robotIdB + " is not the emptiest!" );
        testHelper.performActions( robotControl, "mmm;20", robotIdA );
    }

}
