package thkoeln.coco.ad.planet.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import thkoeln.coco.ad.planet.application.PlanetApplicationService;

import java.util.List;

@RestController
public class PlanetApplicationController {
    PlanetApplicationService planetApplicationService;

    @Autowired
    public PlanetApplicationController(PlanetApplicationService planetApplicationService) {
        this.planetApplicationService = planetApplicationService;
    }

    @GetMapping("/planets")
    public GalaxyModel getAllPlanets() {
        int minX = planetApplicationService.getGalaxyMinX();
        int maxX = planetApplicationService.getGalaxyMaxX();
        int minY = planetApplicationService.getGalaxyMinY();
        int maxY = planetApplicationService.getGalaxyMaxY();
        List<PlanetModel> planets = planetApplicationService.getAllPlanetModels();
        return GalaxyModel.fromAttributes(minX, maxX, minY, maxY, planets);
    }
}
