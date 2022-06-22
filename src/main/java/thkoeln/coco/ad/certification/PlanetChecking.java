package thkoeln.coco.ad.certification;

import java.util.UUID;

public interface PlanetChecking {
    /**
     * By calling this method by some external system (e.g. by HICCUP's sensor mapping system),
     * your system is told about newly detected planets.
     * @param planetId - the planet id where the neighbours have been checked.
     * @param northNeighbourOrNull - UUID of the northern neighbour, or null if there is no northern neighbour
     * @param eastNeighbourOrNull - see above
     * @param southNeighbourOrNull - see above
     * @param westNeighbourOrNull - see above
     */
    void neighboursDetected(UUID planetId,
                            UUID northNeighbourOrNull, UUID eastNeighbourOrNull,
                            UUID southNeighbourOrNull, UUID westNeighbourOrNull);

    /**
     * By calling this method by some external system (e.g. by HICCUP's sensor mapping system),
     * your system is told about newly detected uranium.
     * @param planetId - the id of the planet id that has been checked for uranium.
     * @param uraniumQuantity - the amount of uranium on this planet (can be 0).
     */
    void uraniumDetected( UUID planetId, Integer uraniumQuantity );
}
