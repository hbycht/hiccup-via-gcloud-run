package thkoeln.coco.ad;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.coco.ad.certification.*;
import thkoeln.coco.ad.core.TestHelper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class E1BasicTests {
    @Autowired
    private RobotControl robotControl;
    @Autowired
    private PlanetChecking planetChecking;

    private TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        robotControl.resetAll();
    }

    @Test
    public void testJustOnePlanetFoundAfterReset() {
        List<UUID> found = robotControl.getPlanets();
        assertEquals( 1, found.size() );
        String type = robotControl.getPlanetType( found.get( 0 ) );
        assertEquals( "space shipyard", type );
    }

    @Test
    public void testNoHarvestingOnOrigin() {
        testHelper.performActions( robotControl, "ccc;mmm;-1" );
    }

    @Test
    public void testNoDoubleCreate() {
        UUID robotId = testHelper.performCreate( robotControl );
        Assertions.assertThrows( RobotControlException.class, () -> {
            testHelper.performCreate( robotControl, robotId );
        }, "Error for double creation expected" );
    }

    @Test
    public void testNoDoubleHarvest() {
        // given
        UUID neighbourId = testHelper.createNorthNeighbour( robotControl, planetChecking );
        planetChecking.uraniumDetected( neighbourId, 5 );
        UUID robotId = UUID.randomUUID();

        // when
        robotControl.executeOrder( "[create," + robotId.toString() + "]" );
        robotControl.executeOrder( "[north," + robotId.toString() + "]" );
        robotControl.executeOrder( "[harvest," + robotId.toString() + "]" );
        assertEquals( 5, robotControl.getRobotCargo( robotId ) );

        // then
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( "[harvest," + robotId.toString() + "]" );
        }, "Error for double harvesting expected" );
    }

    @Test
    public void testSimpleExplore() {
        // given
        UUID robotId = testHelper.performCreate( robotControl );

        // when
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( "[explore," + robotId.toString() + "]" );
        }, "Error for nothing to explore expected" );
        UUID neighbourId = testHelper.createNorthNeighbour( robotControl, planetChecking );
        robotControl.executeOrder( "[explore," + robotId.toString() + "]" );

        // then
        assertEquals( neighbourId, robotControl.getRobotPlanet( robotId ) );
    }
}
