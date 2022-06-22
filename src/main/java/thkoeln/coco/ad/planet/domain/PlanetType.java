package thkoeln.coco.ad.planet.domain;

public enum PlanetType {
    SPACE_SHIPYARD,
    REGULAR,
    UNKNOWN;

    @Override
    public String toString() {
        return this.name().toLowerCase().replaceAll( "_", " " );
    }
}
