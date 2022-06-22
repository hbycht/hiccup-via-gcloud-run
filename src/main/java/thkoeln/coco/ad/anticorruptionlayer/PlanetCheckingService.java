package thkoeln.coco.ad.anticorruptionlayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.certification.RobotControlException;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.domainprimitives.uranium.UraniumException;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.planet.domain.PlanetException;

import java.util.UUID;

@Service
public class PlanetCheckingService implements PlanetChecking {
    private final PlanetApplicationService planetApplicationService;

    @Autowired
    public PlanetCheckingService( PlanetApplicationService planetApplicationService ) {
        this.planetApplicationService = planetApplicationService;
    }

    @Override
    public void neighboursDetected( UUID planetId, UUID northNeighbourOrNull, UUID eastNeighbourOrNull,
                                    UUID southNeighbourOrNull, UUID westNeighbourOrNull ) {
        try {
            Planet selectedPlanet = this.planetApplicationService.getPlanetById( planetId );
            Coordinate selectedPlanetsCoordinates = selectedPlanet.getCoordinate();
            Planet northNeighbour = this.planetApplicationService.createPlanetOrReuse( northNeighbourOrNull, Coordinate.northFromCoordinate( selectedPlanetsCoordinates ) );
            Planet eastNeighbour = this.planetApplicationService.createPlanetOrReuse( eastNeighbourOrNull, Coordinate.eastFromCoordinate( selectedPlanetsCoordinates ) );
            Planet southNeighbour = this.planetApplicationService.createPlanetOrReuse( southNeighbourOrNull, Coordinate.southFromCoordinate( selectedPlanetsCoordinates ) );
            Planet westNeighbour = this.planetApplicationService.createPlanetOrReuse( westNeighbourOrNull, Coordinate.westFromCoordinate( selectedPlanetsCoordinates ) );
            this.planetApplicationService.setPlanetsNeighbours( selectedPlanet, northNeighbour, eastNeighbour, southNeighbour, westNeighbour );
        } catch ( NullPointerException | IllegalArgumentException | InvalidDataAccessApiUsageException n ) {
            throw new RobotControlException( "The given arguments are not valid!" );
        } catch ( PlanetException e ){
            throw new RobotControlException( e.getMessage() );
        }
    }

    @Override
    public void uraniumDetected(UUID planetId, Integer uraniumQuantity) {
        try {
            Planet selectedPlanet = this.planetApplicationService.getPlanetById( planetId );
            Uranium givenUranium = Uranium.fromAmount( uraniumQuantity );
            this.planetApplicationService.setPlanetsUranium( selectedPlanet, givenUranium );
        } catch ( NullPointerException | IllegalArgumentException | InvalidDataAccessApiUsageException n ) {
            throw new RobotControlException( "The given arguments are not valid!" );
        } catch ( PlanetException | UraniumException e ){
            throw new RobotControlException( e.getMessage() );
        }
    }
}
