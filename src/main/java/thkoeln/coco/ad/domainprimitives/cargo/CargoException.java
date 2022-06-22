package thkoeln.coco.ad.domainprimitives.cargo;

import thkoeln.coco.ad.certification.RobotControlException;

public class CargoException extends RobotControlException {
    public CargoException(String message) {
        super(message);
    }
}
