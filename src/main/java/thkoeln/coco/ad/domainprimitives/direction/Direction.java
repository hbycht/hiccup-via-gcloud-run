package thkoeln.coco.ad.domainprimitives.direction;

import java.util.Arrays;
import java.util.List;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;
    
    public Direction getOpposite() {
        List<Direction> directions = Arrays.asList( Direction.values() );
        int indexOfActualDirection = directions.indexOf( this );
        int indexOfOppositeDirection = ( directions.size() + indexOfActualDirection - 2 ) % 4;
        return directions.get( indexOfOppositeDirection );
    }

    public String getCapitalizedName() {
        String direction = this.name().toLowerCase();
        String firstLetterCapital = direction.substring(0, 1).toUpperCase();
        return firstLetterCapital + direction.substring(1);
    }
}
