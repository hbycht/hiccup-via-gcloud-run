package thkoeln.coco.ad.owntests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import thkoeln.coco.ad.domainprimitives.order.Order;
import thkoeln.coco.ad.domainprimitives.order.OrderException;
import thkoeln.coco.ad.domainprimitives.order.OrderType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class OrderClassTests {

    @Test
    public void shouldReturnOrderUUIDFromValidOrderString(){
        // given
        UUID randomUUID = UUID.randomUUID();

        // when
        Order order = Order.fromCertificationString( "[create," + randomUUID + "]" );

        // then
        assertEquals( order.getId(), randomUUID );
    }

    @Test
    public void shouldReturnOrderTypeFromValidOrderString(){
        // given
        UUID randomUUID = UUID.randomUUID();

        // when
        Order order = Order.fromCertificationString( "[east," + randomUUID + "]" );

        // then
        assertEquals( order.getOrderType(), OrderType.EAST );
    }

    @Test
    public void shouldThrowOrderExceptionIfOrderIsNotSurroundedBySquareBracket(){
        // given
        UUID randomUUID = UUID.randomUUID();

        // when

        // then
        Assertions.assertThrows( OrderException.class, () -> {
            Order order = Order.fromCertificationString( "create," + randomUUID + "" );
        } );

    }

    @Test
    public void shouldThrowOrderExceptionIfOrderDoesNotContainAnUUID(){
        // given
        UUID randomUUID = UUID.randomUUID();
        String randomString = "NotAnUUID...";

        // when

        // then
        Assertions.assertThrows( OrderException.class, () -> {
            Order order = Order.fromCertificationString( "[create," + randomString + "]" );
        } );
    }

    @Test
    public void shouldThrowOrderExceptionIfOrderDoesNotContainAnComma(){
        // given
        UUID randomUUID = UUID.randomUUID();

        // when

        // then
        Assertions.assertThrows( OrderException.class, () -> {
            Order order = Order.fromCertificationString( "[create" + randomUUID + "]" );
        } );
    }

    @Test
    public void shouldNotThrowOrderExceptionIfOrderIsValid(){
        // given
        String validOrderString = "[create,70343c17-11d1-46bb-8159-74c6fc17d40]";

        // when
        Order order = Order.fromCertificationString( validOrderString );

        // then
        Assertions.assertEquals( order.getOrderType(), OrderType.CREATE );
        Assertions.assertEquals( order.getId(), UUID.fromString( "70343c17-11d1-46bb-8159-74c6fc17d40" ) );
    }
}
