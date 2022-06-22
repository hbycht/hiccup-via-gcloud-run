package thkoeln.coco.ad.anticorruptionlayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.certification.RobotControl;
import thkoeln.coco.ad.certification.RobotControlException;
import thkoeln.coco.ad.domainprimitives.order.Order;
import thkoeln.coco.ad.domainprimitives.order.OrderException;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.planet.domain.PlanetException;
import thkoeln.coco.ad.robot.application.RobotApplicationService;
import thkoeln.coco.ad.robot.domain.Robot;
import thkoeln.coco.ad.robot.domain.RobotException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RobotControlService implements RobotControl {
    private final RobotApplicationService robotApplicationService;
    private final PlanetApplicationService planetApplicationService;

    @Autowired
    public RobotControlService(RobotApplicationService robotApplicationService, PlanetApplicationService planetApplicationService) {
        this.robotApplicationService = robotApplicationService;
        this.planetApplicationService = planetApplicationService;
    }

    @Override
    public void executeOrder( String orderString ) {
        try {
            Order order = Order.fromCertificationString( orderString );
            this.robotApplicationService.executeOrder( order );
        } catch ( RobotException | OrderException e ) {
            throw new RobotControlException( e.getMessage() );
        } catch ( NullPointerException n ) {
            throw new RobotControlException( "The order is <null>. Please provide a valid order!" );
        }
    }

    @Override
    public Integer getRobotCargo( UUID robotId ) {
        Robot selectedRobot = this.getRobotByIDOrThrowException( robotId );
        return selectedRobot.getCargo().getUranium().getAvailableAmount();
    }

    @Override
    public UUID getRobotPlanet( UUID robotId ) {
        Robot selectedRobot = this.getRobotByIDOrThrowException( robotId );
        return selectedRobot.getCurrentPosition().getId();
    }

    @Override
    public String getPlanetType( UUID planetId ) {
        Planet selectedPlanet = this.getPlanetByIDOrThrowException( planetId );
        return selectedPlanet.getType().toString();
    }

    @Override
    public Integer getPlanetUraniumAmount( UUID planetId ) {
        Planet selectedPlanet = this.getPlanetByIDOrThrowException( planetId );
        return selectedPlanet.getUranium().getAvailableAmount();
    }

    @Override
    public List<UUID> getPlanetRobots( UUID planetId ) {
        Planet selectedPlanet = this.getPlanetByIDOrThrowException( planetId );
        List<Robot> allRobotsOnPlanet = robotApplicationService.getAllRobotsOnPlanet( selectedPlanet );
        List<UUID> allRobotIDs = new ArrayList<>();
        for( Robot robot : allRobotsOnPlanet ) {
            allRobotIDs.add( robot.getId() );
        }
        return allRobotIDs;
    }

    @Override
    public List<UUID> getPlanets() {
        List<Planet> allPlanets = this.planetApplicationService.getAllPlanets();
        List<UUID> allPlanetIDs = new ArrayList<>();
        for( Planet planet : allPlanets ) {
            allPlanetIDs.add( planet.getId() );
        }
        return allPlanetIDs;
    }

    @Override
    public UUID resetAll() {
        this.robotApplicationService.resetAll();
        return this.planetApplicationService.resetAll();
    }

    private Planet getPlanetByIDOrThrowException( UUID planetId ) {
        try {
            return this.planetApplicationService.getPlanetById( planetId );
        } catch ( NullPointerException | IllegalArgumentException | InvalidDataAccessApiUsageException n ) {
            throw new RobotControlException( "The given planetId is not a valid UUID!" );
        } catch ( PlanetException e ) {
            throw new RobotControlException( e.getMessage() );
        }
    }

    private Robot getRobotByIDOrThrowException( UUID robotId ) {
        try {
            return this.robotApplicationService.getRobotById( robotId );
        } catch ( NullPointerException | IllegalArgumentException | InvalidDataAccessApiUsageException n ) {
            throw new RobotControlException( "The given robotId is not a valid UUID!" );
        } catch ( RobotException e ) {
            throw new RobotControlException( e.getMessage() );
        }
    }
}