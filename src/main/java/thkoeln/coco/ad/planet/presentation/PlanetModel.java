package thkoeln.coco.ad.planet.presentation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.planet.domain.Planet;

@Getter
@EqualsAndHashCode
public class PlanetModel {
    private String id;
    private Integer resourceAmount;
    private String type;
    private Coordinate coordinate;

    private PlanetModel( Planet planet ) {
        this.id = planet.getId().toString();
        this.resourceAmount = planet.getUranium().getAvailableAmount();
        this.type = planet.getType().toString();
        this.coordinate = planet.getCoordinate();
    }

    public static PlanetModel fromPlanet( Planet planet ) {
        return new PlanetModel( planet );
    }
}
