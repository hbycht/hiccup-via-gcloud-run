package thkoeln.coco.ad.domainprimitives.stepstack;

import lombok.EqualsAndHashCode;
import thkoeln.coco.ad.domainprimitives.direction.Direction;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@EqualsAndHashCode
public class StepStack {
    @ElementCollection( fetch = FetchType.EAGER )
    private List<Direction> steps;

    private StepStack( List<Direction> steps ) {
        this.steps = steps;
    }

    protected StepStack(){
        this.steps = new ArrayList<>();
    }

    public static StepStack fromInit(){
        return new StepStack();
    }

    private static StepStack withSteps( List<Direction> steps ){
        return new StepStack( steps );
    }

    public StepStack afterAdding( Direction direction ){
        List<Direction> includingSteps = this.steps;
        includingSteps.add( direction );
        return StepStack.withSteps( includingSteps );
    }

    public StepStack afterSteppingBack(){
        return StepStack.withSteps( this.steps.subList( 0, this.steps.size() - 1) );
    }

    public Direction getLastInvertedStep(){
        return this.steps.get( this.steps.size() - 1 ).getOpposite();
    }

    public boolean isEmpty() {
        return this.steps.isEmpty();
    }
}
