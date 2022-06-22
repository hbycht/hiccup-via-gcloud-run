package thkoeln.coco.ad.robot.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.coco.ad.planet.domain.Planet;

import java.util.List;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    List<Robot> getAllByCurrentPositionEquals( Planet planet );
}
