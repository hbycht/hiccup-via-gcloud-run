package thkoeln.coco.ad.planet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.domainprimitives.direction.Direction;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.robot.domain.HarvestingService;
import thkoeln.coco.ad.robot.domain.Robot;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Planet {
    @Id
    private UUID id;
    @Embedded
    private Uranium uranium;
    @Enumerated
    private PlanetType type;
    @OneToOne
    private Planet neighbourNorth;
    @OneToOne
    private Planet neighbourEast;
    @OneToOne
    private Planet neighbourSouth;
    @OneToOne
    private Planet neighbourWest;
    @Embedded
    private Coordinate coordinate;

    public Planet( UUID id, Coordinate coordinate ) {
        this.id = id;
        this.uranium = Uranium.fromZero();
        this.type = PlanetType.UNKNOWN;
        this.neighbourNorth = null;
        this.neighbourEast = null;
        this.neighbourSouth = null;
        this.neighbourWest = null;
        this.coordinate = coordinate;
    }

    public Planet( UUID id, Coordinate coordinate, PlanetType type ) {
        this( id, coordinate );
        this.type = type;
    }

    public ArrayList<Planet> getNeighbours() {
        ArrayList<Planet> allNeighbours = new ArrayList<>();
        if( neighbourNorth != null )
            allNeighbours.add( neighbourNorth );
        if( neighbourEast != null )
            allNeighbours.add( neighbourEast );
        if( neighbourWest != null )
            allNeighbours.add( neighbourWest );
        if( neighbourSouth != null )
            allNeighbours.add( neighbourSouth );
        return allNeighbours;
    }

    public ArrayList<Planet> getUnknownNeighbours() {
        ArrayList<Planet> unknownNeighbours = new ArrayList<>();
        for( Planet planet : getNeighbours() ){
            if( planet.getType() == PlanetType.UNKNOWN )
                unknownNeighbours.add( planet );
        }
        return unknownNeighbours;
    }

    public Direction getNeighboursDirection( Planet planet ){
        if( planet.equals(neighbourNorth) )
            return Direction.NORTH;
        if( planet.equals(neighbourEast) )
            return Direction.EAST;
        if( planet.equals(neighbourSouth) )
            return Direction.SOUTH;
        if( planet.equals(neighbourWest) )
            return Direction.WEST;
        return null;
    }

    public void harvest( Uranium uranium ){
        this.uranium = this.uranium.afterTaking( uranium );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Planet planet = (Planet) o;
        return id.equals(planet.id);
    }
}
