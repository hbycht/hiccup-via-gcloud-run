package thkoeln.coco.ad.domainprimitives.coordinate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Coordinate {
    private final int x;
    private final int y;

    private Coordinate() {
        this( 0, 0 );
    }

    private Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate fromXY( int x, int y ){
        return new Coordinate( x, y );
    }

    public static Coordinate northFromCoordinate( Coordinate coordinate ){
        int cX = coordinate.getX();
        int cY = coordinate.getY();
        return fromXY( cX, cY - 1 );
    }

    public static Coordinate eastFromCoordinate( Coordinate coordinate ){
        int cX = coordinate.getX();
        int cY = coordinate.getY();
        return fromXY( cX + 1, cY );
    }

    public static Coordinate southFromCoordinate( Coordinate coordinate ){
        int cX = coordinate.getX();
        int cY = coordinate.getY();
        return fromXY( cX, cY + 1 );
    }

    public static Coordinate westFromCoordinate( Coordinate coordinate ){
        int cX = coordinate.getX();
        int cY = coordinate.getY();
        return fromXY( cX - 1, cY );
    }
}
