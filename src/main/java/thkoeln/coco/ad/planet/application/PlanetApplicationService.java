package thkoeln.coco.ad.planet.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.domainprimitives.direction.Direction;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.planet.domain.*;
import thkoeln.coco.ad.planet.presentation.PlanetModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class PlanetApplicationService {
    private final PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }

    public UUID resetAll() {
        this.planetRepository.deleteAll();
        Planet newSpaceShipyard = createShipyard();
        return newSpaceShipyard.getId();
    }

    public Planet createPlanetOrReuse( UUID planetID, Coordinate coordinate ) {
        if( planetID == null )
            return null;

        if( planetRepository.existsById( planetID ) )
            return getPlanetById( planetID );

        Planet newPlanet = new Planet( planetID, coordinate );
        planetRepository.save( newPlanet );
        return newPlanet;
    }

    private Planet createShipyard() {
        if( ! getAllPlanets().isEmpty() )
            throw new PlanetException( "Space shipyard has to be the first planet." );
        UUID spaceShipyardID = UUID.randomUUID();
        Planet newShipyard = new Planet( spaceShipyardID, Coordinate.fromXY( 0, 0 ), PlanetType.SPACE_SHIPYARD );
        planetRepository.save( newShipyard );
        return newShipyard;
    }

    public List<Planet> getAllPlanets() {
        Iterable<Planet> planetIterable = this.planetRepository.findAll();
        return Streamable.of( planetIterable ).toList();
    }

    public Planet getPlanetById( UUID planetId ) {
        Optional<Planet> maybeAPlanet = this.planetRepository.findById( planetId );
        if( maybeAPlanet.isEmpty() )
            throw new PlanetException( String.format( "There is no planet with the ID %s.", planetId ) );
        return maybeAPlanet.get();
    }

    public List<PlanetModel> getAllPlanetModels() {
        ArrayList<PlanetModel> planetModels = new ArrayList<>();
        for( Planet planet : getAllPlanets() ){
            planetModels.add( PlanetModel.fromPlanet( planet ) );
        }
        return planetModels;
    }

    public PlanetModel getPlanetModelById( UUID planetId ) {
        return PlanetModel.fromPlanet( getPlanetById( planetId ) );
    }

    public Planet getSpaceShipyard(){
        return planetRepository.findPlanetByTypeEquals( PlanetType.SPACE_SHIPYARD ).orElse( null );
    }

    public void updatePlanet( Planet planet ){
        this.planetRepository.save( planet );
    }

    public void setPlanetsUranium( Planet planet, Uranium uranium ) {
        planet.setUranium( uranium );
        this.planetRepository.save( planet );
    }

    public void setPlanetsNeighbours( Planet planet, Planet northNeighbour, Planet eastNeighbour,
                                      Planet southNeighbour, Planet westNeighbour) {
        setPossibleNeighbour( Direction.NORTH, planet, northNeighbour );
        setPossibleNeighbour( Direction.EAST, planet, eastNeighbour );
        setPossibleNeighbour( Direction.SOUTH, planet, southNeighbour );
        setPossibleNeighbour( Direction.WEST, planet, westNeighbour );
    }

    private void setPossibleNeighbour( Direction direction, Planet planet, Planet neighbour ){
        String getterName = "getNeighbour" + direction.getCapitalizedName();
        String setterName = "setNeighbour" + direction.getCapitalizedName();
        try {
            Method neighbourGetter = Planet.class.getMethod( getterName );
            Method neighbourSetter = Planet.class.getMethod( setterName, Planet.class );
            if( neighbourGetter.invoke( planet ) == null && neighbour != null ){
                neighbourSetter.invoke( planet, neighbour );
                this.planetRepository.save( planet );
                connectAllKnownNeighbours( neighbour );
                this.planetRepository.save( neighbour );
            }
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
            throw new PlanetException( e.getMessage() + ": Methode not available on Planet class." );
        }
    }

    public void connectAllKnownNeighbours( Planet planet ){
        Coordinate planetsCoordinate = planet.getCoordinate();
        Planet neighbourNorth = this.planetRepository.findPlanetByCoordinate( Coordinate.northFromCoordinate( planetsCoordinate ) ).orElse( null );
        Planet neighbourEast = this.planetRepository.findPlanetByCoordinate( Coordinate.eastFromCoordinate( planetsCoordinate ) ).orElse( null );
        Planet neighbourSouth = this.planetRepository.findPlanetByCoordinate( Coordinate.southFromCoordinate( planetsCoordinate ) ).orElse( null );
        Planet neighbourWest = this.planetRepository.findPlanetByCoordinate( Coordinate.westFromCoordinate( planetsCoordinate ) ).orElse( null );

        setPlanetsNeighbours( planet, neighbourNorth, neighbourEast, neighbourSouth, neighbourWest );
    }

//    public int getGalaxyWidth(){
//        return getGalaxyMinX() * -1 + getGalaxyMaxX() + 1;
//    }
//
//    public int getGalaxyHeight(){
//        return getGalaxyMinY() * -1 + getGalaxyMaxY() + 1;
//    }

    public int getGalaxyMinX(){
        Planet planetWithMinX = planetRepository.findFirstByOrderByCoordinate_XAsc();
        return planetWithMinX.getCoordinate().getX();
    }

    public int getGalaxyMaxX(){
        Planet planetWithMaxX = planetRepository.findFirstByOrderByCoordinate_XDesc();
        return planetWithMaxX.getCoordinate().getX();
    }

    public int getGalaxyMinY(){
        Planet planetWithMinY = planetRepository.findFirstByOrderByCoordinate_YAsc();
        return planetWithMinY.getCoordinate().getY();
    }

    public int getGalaxyMaxY(){
        Planet planetWithMaxY = planetRepository.findFirstByOrderByCoordinate_YDesc();
        return planetWithMaxY.getCoordinate().getY();
    }

    public Planet getPlanetAtCoordinate( Coordinate coordinate ){
        Optional<Planet> maybeAPlanet = this.planetRepository.findPlanetByCoordinate( coordinate );
        if( maybeAPlanet.isEmpty() )
            throw new PlanetException( String.format( "There is no planet with the coordinate X: %d | Y: %d.", coordinate.getY(), coordinate.getY() ) );
        return maybeAPlanet.get();
    }

    public Boolean isPlanetAtCoordinate( Coordinate coordinate ){
        return planetRepository.findPlanetByCoordinate( coordinate ).isPresent();
    }

    public Direction getDirectionOfNeighbourByCoordinate( Planet planet, Coordinate neighboursCoordinate ){
        Planet neighbourPlanet;
        if( isPlanetAtCoordinate( neighboursCoordinate ) )
            neighbourPlanet = planetRepository.findPlanetByCoordinate( neighboursCoordinate ).get();
        else
            throw new PlanetException( "Their is no neighbour with this coordinate." );
        return planet.getNeighboursDirection( neighbourPlanet );
    }
}
