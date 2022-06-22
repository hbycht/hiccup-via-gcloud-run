package thkoeln.coco.ad.planet.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanetRepository extends CrudRepository<Planet, UUID> {
    Optional<Planet> findPlanetByCoordinate( Coordinate coordinate );
    Optional<Planet> findPlanetByTypeEquals( PlanetType planetType );
    Planet findFirstByOrderByCoordinate_XAsc();
    Planet findFirstByOrderByCoordinate_XDesc();
    Planet findFirstByOrderByCoordinate_YAsc();
    Planet findFirstByOrderByCoordinate_YDesc();
}
