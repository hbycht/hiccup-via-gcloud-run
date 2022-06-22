package thkoeln.coco.ad.robot.domain;

import thkoeln.coco.ad.certification.RobotControlException;

public class RobotException extends RobotControlException {
    public RobotException(String message) {
        super(message);
    }
}
