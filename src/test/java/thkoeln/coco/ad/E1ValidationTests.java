package thkoeln.coco.ad;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.coco.ad.certification.*;
import thkoeln.coco.ad.core.TestHelper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class E1ValidationTests {
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
    public void testExecuteOrderValidation() {
        Assertions.assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( null );
        }, "Validation error expected for invalid order <null>");

        final String s1 = "";
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( s1 );
        }, "Validation error expected for invalid order " + s1 );

        final String s2 = "[eeast,554c00de-3a81-43e8-a384-2dcfebdcf34c]";
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( s1 );
        }, "Validation error expected for invalid order " + s2 );

        final String s3 = "[,554c00de-3a81-43e8-a384-2dcfebdcf34c]";
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( s1 );
        }, "Validation error expected for invalid order " + s3 );

        final String s4 = "[gohome,554c00de-3a81-43e8-a384-2dcfebdcf34c";
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( s1 );
        }, "Validation error expected for invalid order " + s4 );

        final String s5 = "[explore,000000-000-00]";
        assertThrows( RobotControlException.class, () -> {
            robotControl.executeOrder( s1 );
        }, "Validation error expected for invalid order " + s5 );
    }

    @Test
    public void testGetRobotCargo() {
        assertThrows( RobotControlException.class, () -> {
            robotControl.getRobotCargo( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetRobotPlanet() {
        assertThrows( RobotControlException.class, () -> {
            robotControl.getRobotPlanet( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetPlanetType() {
        assertThrows( RobotControlException.class, () -> {
            robotControl.getPlanetType( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetPlanetUraniumAmount() {
        assertThrows( RobotControlException.class, () -> {
            robotControl.getPlanetUraniumAmount( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetPlanetRobots() {
        assertThrows( RobotControlException.class, () -> {
            robotControl.getPlanetRobots( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testNeighboursDetected() {
        assertThrows( RobotControlException.class, () -> {
            planetChecking.neighboursDetected( null, null, null, null, null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testUraniumDetected() {
        // given
        // when
        UUID neighBourId = testHelper.createNorthNeighbour( robotControl, planetChecking );

        // then
        assertThrows( RobotControlException.class, () -> {
            planetChecking.uraniumDetected( null, 1 );
        }, "Validation error expected for invalid UUID <null>" );

        assertThrows( RobotControlException.class, () -> {
            planetChecking.uraniumDetected( neighBourId, null );
        }, "Validation error expected for invalid quantity <null>" );

        assertThrows( RobotControlException.class, () -> {
            planetChecking.uraniumDetected( neighBourId, -3 );
        }, "Validation error expected for invalid quantity -3" );
    }
}
