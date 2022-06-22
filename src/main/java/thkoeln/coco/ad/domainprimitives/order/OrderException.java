package thkoeln.coco.ad.domainprimitives.order;

import thkoeln.coco.ad.certification.RobotControlException;

public class OrderException extends RobotControlException {
    public OrderException(String message) {
        super(message);
    }
}
