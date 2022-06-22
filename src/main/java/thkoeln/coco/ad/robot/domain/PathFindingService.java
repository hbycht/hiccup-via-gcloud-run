package thkoeln.coco.ad.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.domainprimitives.path.PathModel;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.robot.application.RobotApplicationService;

import java.util.UUID;

@Service
public class PathFindingService {
    private final RobotApplicationService robotApplicationService;
    private final PlanetApplicationService planetApplicationService;

    @Autowired
    public PathFindingService(RobotApplicationService robotApplicationService, PlanetApplicationService planetApplicationService) {
        this.robotApplicationService = robotApplicationService;
        this.planetApplicationService = planetApplicationService;
    }

    public PathModel calculatePath(UUID robotUuid, UUID targetPlanetUuid ) {
        Robot robot = robotApplicationService.getRobotById( robotUuid );
        Planet targetPlanet = planetApplicationService.getPlanetById( targetPlanetUuid );
        Coordinate robotCoordinate = robot.getCurrentPosition().getCoordinate();

        PathModel path = findPathInXYOrder(robotUuid, targetPlanet);
        for( Coordinate coordinate : path.getSteps() ){
            if( ! planetApplicationService.isPlanetAtCoordinate( coordinate ) ){
                path = path.afterFlagAsIllegal();
                break;
            }
        }
        if( ! path.isLegal() ){
            path = findPathInYXOrder(robotUuid, targetPlanet);
            for( Coordinate coordinate : path.getSteps() ){
                if( ! planetApplicationService.isPlanetAtCoordinate( coordinate ) ){
                    return path.afterFlagAsIllegal();
                }
            }
        }
        return path;
    }

    private PathModel findPathInXYOrder(UUID robotId, Planet targetPlanet ) {
        Coordinate robotCoordinate = robotApplicationService.getRobotById( robotId ).getCurrentPosition().getCoordinate();
        Coordinate targetPlanetCoordinate = targetPlanet.getCoordinate();
        int xCoordOffset = robotCoordinate.getX() - targetPlanetCoordinate.getX();
        int yCoordOffset = robotCoordinate.getY() - targetPlanetCoordinate.getY();
        PathModel path = PathModel.fromInit( robotId );
        Coordinate currentCoordinate = robotCoordinate;
        path = getPathInXDirection(path, xCoordOffset, currentCoordinate);
        if(path.getSteps().size() > 0)
            currentCoordinate = path.getSteps().get( path.getSteps().size() - 1 );
        path = getPathInYDirection(path, yCoordOffset, currentCoordinate);
        return path;
    }

    private PathModel findPathInYXOrder( UUID robotId, Planet targetPlanet ) {
        Coordinate robotCoordinate = robotApplicationService.getRobotById( robotId ).getCurrentPosition().getCoordinate();
        Coordinate targetPlanetCoordinate = targetPlanet.getCoordinate();
        int xCoordOffset = robotCoordinate.getX() - targetPlanetCoordinate.getX();
        int yCoordOffset = robotCoordinate.getY() - targetPlanetCoordinate.getY();
        PathModel path = PathModel.fromInit( robotId );
        Coordinate currentCoordinate = robotCoordinate;
        path = getPathInYDirection(path, yCoordOffset, currentCoordinate);
        if(path.getSteps().size() > 0)
            currentCoordinate = path.getSteps().get( path.getSteps().size() - 1 );
        path = getPathInXDirection(path, xCoordOffset, currentCoordinate);
        return path;
    }

    private PathModel getPathInYDirection(PathModel path, int yCoordOffset, Coordinate currentCoordinate) {
        if( yCoordOffset > 0 ){ // north
            for(int y = 0; y < yCoordOffset; y++ ){
                path = path.afterAdding( Coordinate.fromXY( currentCoordinate.getX(), currentCoordinate.getY() - y - 1 ) );
            }
        }
        else if( yCoordOffset < 0 ) { // south
            for (int y = 0; y > yCoordOffset; y--) {
                path = path.afterAdding(Coordinate.fromXY( currentCoordinate.getX(), currentCoordinate.getY() + Math.abs(y) + 1) );
            }
        }
        return path;
    }

    private PathModel getPathInXDirection(PathModel path, int xCoordOffset, Coordinate currentCoordinate) {
        if( xCoordOffset > 0 ){ // west
            for(int x = 0; x < xCoordOffset; x++ ){
                path = path.afterAdding( Coordinate.fromXY(currentCoordinate.getX() - x - 1, currentCoordinate.getY() ) );
            }
        }
        else if( xCoordOffset < 0 ) { // east
            for (int x = 0; x > xCoordOffset; x--) {
                path = path.afterAdding(Coordinate.fromXY(currentCoordinate.getX() + Math.abs(x) + 1, currentCoordinate.getY()));
            }
        }
        return path;
    }
}
