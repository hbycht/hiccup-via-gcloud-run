package thkoeln.coco.ad.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.domainprimitives.direction.Direction;
import thkoeln.coco.ad.domainprimitives.order.Order;
import thkoeln.coco.ad.domainprimitives.order.OrderType;
import thkoeln.coco.ad.domainprimitives.path.PathModel;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.robot.application.RobotApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MovingService {
    private final RobotApplicationService robotApplicationService;
    private final PlanetApplicationService planetApplicationService;

    @Autowired
    public MovingService(RobotApplicationService robotApplicationService, PlanetApplicationService planetApplicationService) {
        this.robotApplicationService = robotApplicationService;
        this.planetApplicationService = planetApplicationService;
    }


    public void executeMoveOnPath( PathModel pathModel ) {
        Robot robot = robotApplicationService.getRobotById( pathModel.getRobotId() );
        Planet currentPlanet = robot.getCurrentPosition();
        List<Coordinate> pathCoordinates = pathModel.getSteps();
        List<Planet> pathPlanets = new ArrayList<>();
        pathPlanets.add( currentPlanet );
        for( Coordinate coordinate : pathCoordinates ){
            Planet actualPlanet = planetApplicationService.getPlanetAtCoordinate( coordinate );
            pathPlanets.add( actualPlanet );
        }
        List<OrderType> pathDirectionsAsOrderTypes = new ArrayList<>();
        for( int p = 0; p < pathPlanets.size() - 1; p++ ){
            Planet actualPlanet = pathPlanets.get( p );
            Planet neighbourPlanet = pathPlanets.get( p + 1 );
            Direction neighboursDirection = actualPlanet.getNeighboursDirection( neighbourPlanet );
            OrderType orderTypeFromDirection = OrderType.fromString( neighboursDirection.toString().toLowerCase() );
            pathDirectionsAsOrderTypes.add( orderTypeFromDirection );
        }
        for( OrderType orderType : pathDirectionsAsOrderTypes ){
            Order actualOrder = Order.fromComponents( orderType, pathModel.getRobotId() );
            robotApplicationService.executeOrder( actualOrder );
        }
    }
}
