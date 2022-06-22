package thkoeln.coco.ad.robot.presentation;

import lombok.Getter;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;
import thkoeln.coco.ad.robot.domain.Robot;

@Getter
public class RobotModel {
    private String id;
    private Coordinate currentPosition;
    private Integer cargoAmount;

    public RobotModel( Robot robot ) {
        this.id = robot.getId().toString();
        this.currentPosition = robot.getCurrentPosition().getCoordinate();
        this.cargoAmount = robot.getCargo().getUranium().getAvailableAmount();
    }

    public static RobotModel fromRobot( Robot robot ) {
        return new RobotModel( robot );
    }
}
