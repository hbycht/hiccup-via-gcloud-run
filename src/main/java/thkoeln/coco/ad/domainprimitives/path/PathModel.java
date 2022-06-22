package thkoeln.coco.ad.domainprimitives.path;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import thkoeln.coco.ad.domainprimitives.coordinate.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class PathModel {
    private UUID robotId;
    private List<Coordinate> steps;
    private boolean legal;

    private PathModel( UUID robotId ) {
        this.robotId = robotId;
        this.steps = new ArrayList<>();
        this.legal = true;
    }

    private PathModel( UUID robotId, List<Coordinate> steps ) {
        this.robotId = robotId;
        this.steps = steps;
        this.legal = true;
    }

//    private PathModel( UUID robotId, List<Coordinate> steps, Boolean legal ){
//        this.robotId = robotId;
//        this.steps = steps;
//        this.legal = legal;
//    }

    public PathModel(@JsonProperty("robotId") UUID robotId, @JsonProperty("steps") List<Coordinate> steps, @JsonProperty("legal") Boolean legal ){
        this.robotId = robotId;
        this.steps = steps;
        this.legal = legal;
    }

    public static PathModel fromInit( UUID robotId ) {
        return new PathModel( robotId );
    }

    public static PathModel withSteps( UUID robotId, List<Coordinate> steps ){
        return new PathModel( robotId, steps );
    }

    public PathModel afterAdding( Coordinate coordinate ){
        List<Coordinate> actualPath = this.steps;
        actualPath.add( coordinate );
        return PathModel.withSteps( this.robotId, actualPath );
    }

    public PathModel afterFlagAsIllegal(){
        return new PathModel( this.robotId, this.steps, false );
    }
}
