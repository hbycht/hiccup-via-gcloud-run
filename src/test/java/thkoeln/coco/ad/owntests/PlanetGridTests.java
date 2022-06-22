package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.certification.RobotControl;
import thkoeln.coco.ad.certification.RobotControlException;
import thkoeln.coco.ad.core.TestHelper;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlanetGridTests {
    @Autowired
    private RobotControl robotControl;
    @Autowired
    private PlanetChecking planetChecking;
    @Autowired
    private PlanetApplicationService planetApplicationService;

    private TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        UUID originId = robotControl.resetAll();
        testHelper.setUpGalaxy( planetChecking,
                "7,8,9,13", "1,3,5,11", "5,3,9,31",
                originId );
    }

    @Test
    public void minAndMaxCoordsOfGalaxyAligningWithGrid() {
        // given
        int minX = planetApplicationService.getGalaxyMinX();
        int maxX = planetApplicationService.getGalaxyMaxX();
        int minY = planetApplicationService.getGalaxyMinY();
        int maxY = planetApplicationService.getGalaxyMaxY();

        // then
        assertEquals( minX, -1 );
        assertEquals( maxX, 1 );
        assertEquals( minY, -2 );
        assertEquals( maxY, 3 );
    }

//    @Test
//    public void widthAndHeightOfGalaxyAligningWithGrid() {
//        // given
//        int width = planetApplicationService.getGalaxyWidth();
//        int height = planetApplicationService.getGalaxyHeight();
//
//        // then
//        assertEquals( 3, width );
//        assertEquals( 6, height );
//    }

}
