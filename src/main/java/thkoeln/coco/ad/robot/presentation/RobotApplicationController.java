package thkoeln.coco.ad.robot.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import thkoeln.coco.ad.domainprimitives.path.PathModel;
import thkoeln.coco.ad.robot.application.RobotApplicationService;
import thkoeln.coco.ad.robot.domain.MovingService;
import thkoeln.coco.ad.robot.domain.PathFindingService;
import thkoeln.coco.ad.robot.domain.Robot;

import java.util.List;
import java.util.UUID;

@RestController
public class RobotApplicationController {
    RobotApplicationService robotApplicationService;
    PathFindingService pathFindingService;
    MovingService movingService;

    @Autowired
    public RobotApplicationController(RobotApplicationService robotApplicationService, PathFindingService pathFindingService, MovingService movingService) {
        this.robotApplicationService = robotApplicationService;
        this.pathFindingService = pathFindingService;
        this.movingService = movingService;

    }

    @GetMapping("/robots")
    public List<RobotModel> getAllRobots() {
        return robotApplicationService.getAllRobotModels();
    }


    @PostMapping("/robots/create")
    public RobotModel buyLicenseAndCreateBot() {
        UUID newRobotUUID = robotApplicationService.buyNewLicenseAndCreateBot();
        RobotModel newRobot = robotApplicationService.getRobotModelById(newRobotUUID);
        return newRobot;
    }

    @GetMapping("/robot/path")
    public PathModel findPath( @RequestParam( "robotId" ) String robotId, @RequestParam( "targetPlanetId" ) String targetPlanetId ){
        UUID robotUuid = UUID.fromString( robotId );
        UUID targetPlanetUuid = UUID.fromString( targetPlanetId );
        return pathFindingService.calculatePath( robotUuid, targetPlanetUuid ) ;
    }

    @PostMapping("/robot/move")
    public PathModel moveOnPath( @RequestBody String jsonString ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PathModel pathModel = objectMapper.readValue(jsonString, PathModel.class);
        movingService.executeMoveOnPath( pathModel );
        return pathModel;
    }

    @GetMapping("/robot/mine")
    public RobotModel mineWithRobot( @RequestParam( "robotId" ) String robotId){
        UUID robotUUID = UUID.fromString(robotId);
        Robot robot = robotApplicationService.getRobotById(robotUUID);
        robotApplicationService.executeHarvest(robot);
        RobotModel currentRobot = robotApplicationService.getRobotModelById(robotUUID);
        return currentRobot;
    }





}
