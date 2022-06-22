package thkoeln.coco.ad.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.domainprimitives.order.Order;
import thkoeln.coco.ad.domainprimitives.order.OrderType;
import thkoeln.coco.ad.domainprimitives.path.PathModel;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;
import thkoeln.coco.ad.domainprimitives.direction.Direction;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.planet.domain.PlanetType;
import thkoeln.coco.ad.robot.domain.*;
import thkoeln.coco.ad.robot.presentation.RobotModel;
import thkoeln.coco.atc.Hiccup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RobotApplicationService {
    private final RobotRepository robotRepository;
    private final PlanetApplicationService planetApplicationService;
    private final HarvestingService harvestingService;
    private final Hiccup hiccup;
    private final PlanetChecking planetChecking;

    @Autowired
    public RobotApplicationService(RobotRepository robotRepository,
                                   PlanetApplicationService planetApplicationService,
                                   HarvestingService harvestingService, Hiccup hiccup,
                                   PlanetChecking planetChecking
    ) {
        this.robotRepository = robotRepository;
        this.planetApplicationService = planetApplicationService;
        this.harvestingService = harvestingService;
        this.hiccup = hiccup;
        this.planetChecking = planetChecking;
    }

    public void executeOrder( Order order ) {
        OrderType orderType = order.getOrderType();
        UUID robotID = order.getId();
        if( orderType == OrderType.CREATE ) {
            createRobot( robotID );
            return;
        }
        Robot robot = getRobotById( robotID );
        if( orderType == OrderType.HARVEST )
            executeHarvest( robot );
        else
            invokeOrderExecuteMethod( orderType, robot );
        this.robotRepository.save( robot );
        updateRobotsActualPlanet( robot );
    }

    public void resetAll() {
        this.robotRepository.deleteAll();
    }

    public Robot getRobotById( UUID robotId ) {
        Optional<Robot> maybeARobot = this.robotRepository.findById( robotId );
        if(maybeARobot.isEmpty())
            throw new RobotException( String.format( "There is no robot with the ID %s.", robotId ) );
        return maybeARobot.get();
    }

    public List<Robot> getAllRobots() {
        Iterable<Robot> robotIterable = this.robotRepository.findAll();
        return Streamable.of( robotIterable ).toList();
    }

    public List<RobotModel> getAllRobotModels() {
        ArrayList<RobotModel> robotModels = new ArrayList<>();
        for( Robot robot : getAllRobots() ){
            robotModels.add( RobotModel.fromRobot( robot ) );
        }
        return robotModels;
    }

    public RobotModel getRobotModelById( UUID robotId ) {
        return RobotModel.fromRobot( getRobotById( robotId ) );
    }

    public List<Robot> getAllRobotsOnPlanet( Planet planet ) {
        Iterable<Robot> robotIterable = this.robotRepository.getAllByCurrentPositionEquals( planet );
        return Streamable.of( robotIterable ).toList();
    }

    private void createRobot( UUID robotID ) {
        if( robotRepository.existsById( robotID ) )
            throw new RobotException( String.format( "Robot with ID '%s' already exists.", robotID) );
        Planet spaceShipyard = planetApplicationService.getSpaceShipyard();
        Robot newRobot = new Robot( robotID, spaceShipyard );
        robotRepository.save( newRobot );
    }

    public void executeHarvest( Robot robot ) {
        Uranium onPlanetHarvestableUranium = harvestingService.getLegalUraniumRobotCanHarvestOnPlanet( robot );
        harvestingService.tryLegalHarvestWithRobot( robot );
        robot.getCurrentPosition().harvest( onPlanetHarvestableUranium );
    }

    private void invokeOrderExecuteMethod(OrderType orderType, Robot robot) {
        String orderExecuteName = orderType.toString().toLowerCase();
        try {
            Method orderExecute;
            if ( orderType.getDirection() != null ) {
                orderExecute = Robot.class.getMethod("move", Direction.class );
                orderExecute.invoke( robot, orderType.getDirection() );
            } else {
                orderExecute = Robot.class.getMethod( orderExecuteName );
                orderExecute.invoke( robot );
            }
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
            throw new RobotException("This kind of order is not available on a robot: "
                    + orderExecuteName + "(" + e.getClass() + ")." );
        }
    }

    private void updateRobotsActualPlanet( Robot robot ) {
        Planet currentPosition = robot.getCurrentPosition();
        if( currentPosition.getType() == PlanetType.UNKNOWN )
            currentPosition.setType( PlanetType.REGULAR );
        this.planetApplicationService.updatePlanet( currentPosition );
    }


    public UUID buyNewLicenseAndCreateBot() {
        UUID newMiningLicense = hiccup.buyNewMiningLicense(planetChecking);
        Order createOrder = Order.fromCertificationString("[create," + newMiningLicense.toString() + "]");
        executeOrder(createOrder);
        return newMiningLicense;
    }
}
