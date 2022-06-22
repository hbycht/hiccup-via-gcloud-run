package thkoeln.coco.ad.planet.domain;

import thkoeln.coco.ad.certification.RobotControlException;

public class PlanetException extends RobotControlException {
    public PlanetException(String message) {
        super(message);
    }
}
