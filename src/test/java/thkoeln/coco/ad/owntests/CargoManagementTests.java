package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import thkoeln.coco.ad.domainprimitives.cargo.Cargo;
import thkoeln.coco.ad.domainprimitives.cargo.CargoException;
import thkoeln.coco.ad.domainprimitives.order.Order;
import thkoeln.coco.ad.domainprimitives.order.OrderException;
import thkoeln.coco.ad.domainprimitives.order.OrderType;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;
import thkoeln.coco.ad.domainprimitives.uranium.UraniumException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CargoManagementTests {

    @Test
    public void shouldThrowUraniumExceptionAfterAttemptToCreateUraniumWithNegativeAmount(){
        // given

        // then
        Assertions.assertThrows( UraniumException.class, () -> {
            Uranium.fromAmount( -10 ) ;
        } );
    }

    @Test
    public void shouldReturn0AsAvailableAmountAfterInitFromZero(){
        // given
        Uranium uranium = Uranium.fromZero();

        // when
        int availableAmount = uranium.getAvailableAmount();

        // then
        assertEquals( availableAmount, 0 );
    }

    @Test
    public void shouldReturn7AsAvailableAmountAfterInitFromAmount7(){
        // given
        Uranium uranium = Uranium.fromAmount( 7 );

        // when
        int availableAmount = uranium.getAvailableAmount();

        // then
        assertEquals( availableAmount, 7 );
    }

    @Test
    public void shouldReturn7AsAvailableAmountAfterAdding7Uranium(){
        // given
        Uranium uranium = Uranium.fromZero();
        Uranium sevenUranium = Uranium.fromAmount( 7 );

        // when
        Uranium uraniumAfterAdding = uranium.afterIncreasing( sevenUranium );
        int availableAmount = uraniumAfterAdding.getAvailableAmount();

        // then
        assertEquals( availableAmount, 7 );
    }

    @Test
    public void shouldReturn7AsAvailableAmountAfterTaking7UraniumFrom14(){
        // given
        Uranium fourteenUranium = Uranium.fromAmount( 14 );
        Uranium sevenUranium = Uranium.fromAmount( 7 );

        // when
        Uranium uraniumAfterTaking = fourteenUranium.afterTaking( sevenUranium );
        int availableAmount = uraniumAfterTaking.getAvailableAmount();

        // then
        assertEquals( availableAmount, 7 );
    }

    @Test
    public void shouldThrowUraniumExceptionAfterAttemptToTakeMoreThanAvailable(){
        // given
        Uranium uranium = Uranium.fromZero();

        // then
        Assertions.assertThrows( UraniumException.class, () -> {
            uranium.afterTaking( Uranium.fromAmount( 10 ) );
        } );
    }

    @Test
    public void shouldReturn20AsAvailableCapacityFromCargo(){
        // given
        Cargo cargo = Cargo.fromZero();

        // when
        int availableCapacity = cargo.getAvailableCapacity();

        // then
        assertEquals( availableCapacity, 20 );
    }

    @Test
    public void shouldReturn10AsAvailableCapacityFromCargoAfterAdding10Uranium(){
        // given
        Cargo cargo = Cargo.fromZero();
        Cargo cargoAfterAdding10Uranium = cargo.afterIncreasing( Uranium.fromAmount( 10 ) );

        // when
        int availableCapacity = cargoAfterAdding10Uranium.getAvailableCapacity();

        // then
        assertEquals( availableCapacity, 10 );
    }

    @Test
    public void shouldReturn10AsAvailableCapacityFromCargoAfterTaking10UraniumFromInitial20(){
        // given
        Cargo cargo = Cargo.fromInitialUranium( Uranium.fromAmount( 20) );
        Cargo cargoAfterTaking10Uranium = cargo.afterTaking( Uranium.fromAmount( 10 ) );

        // when
        int availableCapacity = cargoAfterTaking10Uranium.getAvailableCapacity();

        // then
        assertEquals( availableCapacity, 10 );
    }

    @Test
    public void shouldThrowUraniumExceptionAfterAttemptToTakeFromEmptyCargo(){
        // given
        Cargo cargo = Cargo.fromZero();

        // then
        Assertions.assertThrows( UraniumException.class, () -> {
            cargo.afterTaking( Uranium.fromAmount( 10 ) );
        } );
    }

    @Test
    public void shouldThrowCargoExceptionAfterAttemptToIncreaseCargoOverMaxCapacity(){
        // given
        Cargo cargo = Cargo.fromInitialUranium( Uranium.fromAmount( 10 ) );

        // then
        Assertions.assertThrows( CargoException.class, () -> {
            cargo.afterIncreasing( Uranium.fromAmount( 100 ) );
        } );
    }
}
