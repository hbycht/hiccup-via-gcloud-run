package thkoeln.coco.ad.planet.presentation;

import lombok.Getter;

import java.util.List;

@Getter
public class GalaxyModel {
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private List<PlanetModel> planets;

    private GalaxyModel( int minX, int maxX, int minY, int maxY, List<PlanetModel> planets ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.planets = planets;
    }

    public static GalaxyModel fromAttributes(int minX, int maxX, int minY, int maxY, List<PlanetModel> planets){
        return new GalaxyModel(minX, maxX, minY, maxY, planets);
    }
}
