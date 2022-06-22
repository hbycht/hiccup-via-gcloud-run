package thkoeln.coco.ad.domainprimitives.order;

import thkoeln.coco.ad.domainprimitives.direction.Direction;

import java.util.Arrays;

public enum OrderType {
    CREATE,
    NORTH,
    EAST,
    SOUTH,
    WEST,
    EXPLORE,
    GOHOME,
    HARVEST;

    public static OrderType fromString( String command ) {
        try {
            return OrderType.valueOf( command.toUpperCase() );
        } catch ( IllegalArgumentException e ) {
            throw new OrderException( String.format( "'%s' is not a valid command type. " +
                    "It has to be 'create', 'north', 'east', 'south', 'west', " +
                    "'explore', 'gohome' or 'harvest'.", command ) );
        }
    }

    public Direction getDirection() {
        try {
            return Direction.valueOf( this.toString() );
        }
        catch ( IllegalArgumentException e ){
            return null;
        }
    }
}
