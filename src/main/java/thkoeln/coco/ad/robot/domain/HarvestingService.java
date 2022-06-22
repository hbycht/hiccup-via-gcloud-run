package thkoeln.coco.ad.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.planet.domain.Planet;

import java.util.List;

@Service
public class HarvestingService {
    RobotRepository robotRepository;

    @Autowired
    public HarvestingService( RobotRepository robotRepository ) {
        this.robotRepository = robotRepository;
    }

    public void tryLegalHarvestWithRobot( Robot robot ) {
        robot.harvest( getLegalUraniumRobotCanHarvestOnPlanet( robot ) );
    }

    public Uranium getLegalUraniumRobotCanHarvestOnPlanet( Robot robot ) {
        if( ! hasRobotTheHighestCargoCapacityOnHisPlanet( robot ) )
            throw new RobotException( String.format( "HICCUP fairness-rule for harvesting: " +
                    "Robot with ID %s is not the emptiest robot.", robot.getId() ) );
        return getUraniumRobotCanHarvestOnPlanet( robot );
    }

    private Boolean hasRobotTheHighestCargoCapacityOnHisPlanet( Robot robot ) {
        List<Robot> allRobotsOnPlanet = this.robotRepository.getAllByCurrentPositionEquals( robot.getCurrentPosition() );
        int highestCargoCapacity = allRobotsOnPlanet.get( 0 ).getCargo().getAvailableCapacity();
        for( Robot r : allRobotsOnPlanet ){
            if( r.getCargo().getAvailableCapacity() > highestCargoCapacity )
                highestCargoCapacity = r.getCargo().getAvailableCapacity();
        }
        return robot.getCargo().getAvailableCapacity() == highestCargoCapacity;
    }

    private Uranium getUraniumRobotCanHarvestOnPlanet( Robot robot ) {
        Planet actualPlanet = robot.getCurrentPosition();
        int planetsUraniumAmount = actualPlanet.getUranium().getAvailableAmount();
        int robotsCargoCapacity = robot.getCargo().getAvailableCapacity();
        if( planetsUraniumAmount == 0 || robotsCargoCapacity == 0 )
            throw new RobotException( "Harvesting blocked: Robot's cargo is full or planets uranium stock is empty." );
        if( robotsCargoCapacity <= planetsUraniumAmount )
            return Uranium.fromAmount( robotsCargoCapacity );
        return Uranium.fromAmount( planetsUraniumAmount );
    }
}
