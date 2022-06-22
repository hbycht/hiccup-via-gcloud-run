package thkoeln.coco.ad.robot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.coco.ad.domainprimitives.cargo.Cargo;
import thkoeln.coco.ad.domainprimitives.stepstack.StepStack;
import thkoeln.coco.ad.domainprimitives.direction.Direction;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.planet.domain.Planet;
import thkoeln.coco.ad.planet.domain.PlanetType;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Robot {
    @Id
    private UUID id;
    @ManyToOne
    private Planet currentPosition;
    @Embedded
    private Cargo cargo;
    @Embedded
    private StepStack stepsAwayFromHome;
    @Transient
    private HarvestingService harvestingService;

    public Robot( UUID robotID, Planet spaceShipyard ) {
        this.id = robotID;
        this.currentPosition = spaceShipyard;
        this.cargo = Cargo.fromZero();
        this.stepsAwayFromHome = StepStack.fromInit();
    }

    public void move( Direction direction ) {
        executeStep( direction );
        this.stepsAwayFromHome = this.stepsAwayFromHome.afterAdding( direction );
    }

    public void gohome(){
        if( this.stepsAwayFromHome.isEmpty() )
            throw new RobotException( String.format( "Robot with ID %s is still at his space shipyard. No more steps to go.", this.id ) );
        Direction stepTowardsHome = this.stepsAwayFromHome.getLastInvertedStep();
        executeStep( stepTowardsHome );
        if( currentPosition.getType() == PlanetType.SPACE_SHIPYARD ) {
            this.stepsAwayFromHome = StepStack.fromInit();
            return;
        }
        this.stepsAwayFromHome = this.stepsAwayFromHome.afterSteppingBack();
    }

    public void explore() {
        Direction explorationDirection = chooseExplorationDirection();
        move( explorationDirection );
    }

    protected void harvest( Uranium uranium ) {
        if( currentPosition.getType() == PlanetType.SPACE_SHIPYARD )
            throw new RobotException( "Harvesting on space shipyards is not allowed!" );
        this.cargo = this.cargo.afterIncreasing( uranium );
    }

    private Direction chooseExplorationDirection() {
        ArrayList<Planet> allNeighbours = currentPosition.getNeighbours();
        if( allNeighbours.isEmpty() )
            throw new RobotException( "This planet has no neighbours." );
        ArrayList<Planet> unknownNeighbours = currentPosition.getUnknownNeighbours();
        if( ! unknownNeighbours.isEmpty() ) {
            Planet randomNeighbour = unknownNeighbours.get( (int) ( Math.random() * unknownNeighbours.size() ) );
            return currentPosition.getNeighboursDirection( randomNeighbour );
        }
        Planet randomNeighbour = allNeighbours.get( (int) ( Math.random() * allNeighbours.size() ) );
        return currentPosition.getNeighboursDirection( randomNeighbour );
    }

    private void executeStep( Direction direction ) {
        Planet neighbour;
        try {
            Method neighbourGetter = Planet.class.getMethod( "getNeighbour" + direction.getCapitalizedName() );
            neighbour = (Planet) neighbourGetter.invoke( currentPosition );
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
            throw new RobotException( e.getMessage() + ": This method is not available on a planet." );
        }
        if( neighbour == null )
            throw new RobotException( String.format( "In the %s is no planet to visit.", direction ) );
        setCurrentPosition( neighbour );
    }
}
