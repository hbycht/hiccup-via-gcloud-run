package thkoeln.coco.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import thkoeln.coco.ad.certification.PlanetChecking;
import thkoeln.coco.ad.certification.RobotControl;
import thkoeln.coco.atc.Hiccup;

import java.util.UUID;

@Component
@Profile("hiccup")
public class HiccupInitialisation implements ApplicationListener<ContextRefreshedEvent> {
    private final PlanetChecking planetChecking;
    private final RobotControl robotControl;
    private final Hiccup hiccup;

    @Autowired
    public HiccupInitialisation(PlanetChecking planetChecking, RobotControl robotControl, Hiccup hiccup) {
        this.planetChecking = planetChecking;
        this.robotControl = robotControl;
        this.hiccup = hiccup;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            final UUID spaceStationId = robotControl.resetAll();
            hiccup.registerForCelestialBodyMap(spaceStationId, planetChecking);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to init communication with HICCUP!", e);
        }
    }
}

