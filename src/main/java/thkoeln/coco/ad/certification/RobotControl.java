package thkoeln.coco.ad.certification;


import java.util.List;
import java.util.UUID;

public interface RobotControl {
    /**
     * Execute the given order
     * @param orderString - order as String
     * @throws RobotControlException if the order is an invalid string, or cannot be executed
     */
    public void executeOrder( String orderString );

    /**
     * @param robotId
     * @throws RobotControlException if robotId is not a valid robot id
     * @return current cargo of the robot
     */
    Integer getRobotCargo( UUID  robotId );

    /**
     * @param robotId
     * @throws RobotControlException if robotId is not a valid robot id
     * @return id of the planet the robot is currently located on
     */
    UUID getRobotPlanet( UUID robotId );

    /**
     * @param planetId - Id of the planet
     * @throws RobotControlException if planetId is not a valid planet id
     * @return Type of the planet, either "space shipyard", "regular", or "unknown"
     */
    String getPlanetType( UUID planetId );

    /**
     * @param planetId
     * @throws RobotControlException if planetId is not a valid planet id
     * @return the amount of uranium still left on the planet (can be 0)
     */
    Integer getPlanetUraniumAmount( UUID planetId );

    /**
     * @param planetId
     * @throws RobotControlException if planetId is not a valid planet id
     * @return List of robot ids currently located on this planet (List can be empty)
     */
    List<UUID> getPlanetRobots( UUID planetId );


    /**
     * @return List of all planet ids currently known
     */
    List<UUID> getPlanets();

    /**
     * Delete all robots and planets. Only the space shipyard remains, with 0 uranium.
     * @return UUID of the space shipyard
     */
    public UUID resetAll();
}
