package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import thkoeln.coco.ad.domainprimitives.cargo.Cargo;
import thkoeln.coco.ad.domainprimitives.cargo.CargoException;
import thkoeln.coco.ad.domainprimitives.direction.Direction;
import thkoeln.coco.ad.domainprimitives.stepstack.StepStack;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.domainprimitives.uranium.UraniumException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DomainPrimitivesTests {

    @Test
    public void uraniumsShouldBeEqualIfBothAmountsAre7(){
        // given
        Uranium uranium1 = Uranium.fromAmount( 7 );
        Uranium uranium2 = Uranium.fromAmount( 7 );

        // when

        // then
        assertEquals( uranium1, uranium2 );
    }

    @Test
    public void cargosShouldBeEqualIfBothHave7Uranium(){
        // given
        Uranium uranium1 = Uranium.fromAmount( 7 );
        Uranium uranium2 = Uranium.fromAmount( 7 );

        Cargo cargo1 = Cargo.fromInitialUranium( uranium1 );
        Cargo cargo2 = Cargo.fromInitialUranium( uranium2 );

        // when

        // then
        assertEquals( cargo1, cargo2 );
    }

    @Test
    public void stepStacksShouldBeEqualIfBothHaveSameSteps(){
        // given
        StepStack steps1 = StepStack.fromInit();
        StepStack steps2 = StepStack.fromInit();

        // when
        steps1 = steps1.afterAdding( Direction.NORTH );
        steps1 = steps1.afterAdding( Direction.WEST );
        steps2 = steps2.afterAdding( Direction.NORTH );
        steps2 = steps2.afterAdding( Direction.WEST );

        // then
        assertEquals( steps1, steps2 );
    }
}
