package thkoeln.coco.ad.core;

import thkoeln.coco.ad.certification.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestHelper {
    private static final int MAX_CELLS = 16;
    private UUID[] cells = new UUID[MAX_CELLS];



    public UUID performCreate( RobotControl robotControl ) {
        UUID robotId = UUID.randomUUID();
        performCreate( robotControl, robotId );
        return robotId;
    }

    public void performCreate( RobotControl robotControl, UUID robotId ) {
        String OrderString = "[create," + String.valueOf( robotId ) + "]";
        robotControl.executeOrder( OrderString );
    }

    public UUID performActions( RobotControl robotControl, String OrderSequence ) {
        UUID robotId = UUID.randomUUID();
        performActions( robotControl, OrderSequence, robotId );
        return robotId;
    }

    public UUID createNorthNeighbour(RobotControl robotControl, PlanetChecking planetChecking ) {
        UUID originId = robotControl.getPlanets().get( 0 );
        UUID neighBourId = UUID.randomUUID();
        planetChecking.neighboursDetected( originId, neighBourId, null, null, null );
        return neighBourId;
    }

    public void performActions( RobotControl robotControl, String orderSequence, UUID robotId ) {
        String[] orderArray = orderSequence.split( ";" );
        for ( int i = 0; i < orderArray.length - 1; i++ ) {
            final String orderString = createOrderString( orderArray[i], robotId );
            if ( i < orderArray.length - 2 ) {
                robotControl.executeOrder( orderString );
            } else {
                Integer cargo = Integer.valueOf( orderArray[orderArray.length-1] );
                if ( cargo < 0 ) {
                    // -1 means "energy field" => Exception expected
                    assertThrows( RobotControlException.class, () -> {
                        robotControl.executeOrder( orderString );
                    });
                }
                else {
                    robotControl.executeOrder( orderString );
                    assertEquals( cargo, robotControl.getRobotCargo( robotId ) );
                }
            }
        }
    }

    private String createOrderString( String fragment, UUID robotId ) {
        String action;
        if ( fragment.equals( "mmm" ) ) {
            action = "harvest";
        }
        else if ( fragment.equals( "ccc" ) ) {
            action = "create";
        }
        else {
            action = fragment;
        }
        return "[" + action + "," + String.valueOf( robotId ) + "]";
    }

    /**
     * To make this a little easier for you to debug: This method is creating a small galaxy
     * that you find depicted in src/test/resources/Grid.png (small numbers = cell numbers,
     * large numbers = uranium, red cell = space shipyard, grey cells = "holes"
     */
    public void setUpGalaxy( PlanetChecking planetChecking,
                                    String holes, String minCells, String minAmounts,
                                    UUID originId ) {
        cells[0] = originId;
        for ( int i = 1; i < MAX_CELLS; i++ ) {
            cells[i] = UUID.randomUUID();
        }

        // Hole = no planet
        Integer[] holesArray = decode4ElementIntArray( holes );
        for ( int i = 0; i < 4; i++ ) {
            cells[ holesArray[i] ] = null;
        }

        // see Excel maps and numbering there
        planetChecking.neighboursDetected( cells[0], cells[1], cells[2], cells[3], cells[4] );
        planetChecking.neighboursDetected( cells[1], cells[6], cells[7], cells[0], cells[5] );
        planetChecking.neighboursDetected( cells[6], null, cells[9], cells[1], cells[8] );
        planetChecking.neighboursDetected( cells[3], cells[0], cells[10], cells[11], cells[12] );
        planetChecking.neighboursDetected( cells[11], cells[3], cells[13], cells[14], cells[15] );

        // indexes, where uranium is, and the corresponding amount
        Integer[] minCellsArray = decode4ElementIntArray( minCells );
        Integer[] minAmountsArray = decode4ElementIntArray( minAmounts );
        for ( int i = 0; i < 4; i++ ) {
            planetChecking.uraniumDetected( cells[ minCellsArray[i] ], minAmountsArray[i] );
        }
    }



    private Integer[] decode4ElementIntArray( String arrayString ) {
        Integer[] intArray = new Integer[4];
        String[] numberStrings = arrayString.split( "," );
        for ( int i = 0; i < 4; i++ ) {
            intArray[i] = Integer.valueOf( numberStrings[i] );
        }
        return intArray;
    }


    public UUID getPlanetId( int i ) {
        return cells[i];
    }

}
