package thkoeln.coco.ad.domainprimitives.order;

import lombok.*;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public class Order {
    private OrderType orderType;
    private UUID id;

    public static Order fromCertificationString( String orderString ) {
        validateOrderString( orderString );

        String[] orderParts = splitIntoOrderParts( orderString );
        checkForTwoComponents( orderParts );

        String orderCommandPart = orderParts[ 0 ];
        OrderType orderType = OrderType.fromString( orderCommandPart );

        String uuidPart = orderParts[ 1 ];
        UUID uuid = getValidUUID( uuidPart );

        return new Order( orderType, uuid );
    }

    public static Order fromComponents( OrderType orderType, UUID robotId ){
        return new Order( orderType, robotId );
    }

    private static void validateOrderString(String orderString) {
        checkForFilledString(orderString);
        checkForSurroundingBrackets(orderString);
    }

    private static void checkForFilledString( String orderString ) {
        int orderStringLength = orderString.length();
        if ( orderStringLength < 1 )
            throw new OrderException("The given order is empty.");
    }

    private static void checkForSurroundingBrackets( String orderString ) {
        char firstCharInOrderString = orderString.charAt( 0 );
        char lastCharInOrderString = orderString.charAt( orderString.length() - 1 );
        boolean isSurroundedByBrackets = firstCharInOrderString == '[' && lastCharInOrderString == ']';
        if( ! isSurroundedByBrackets )
            throw new OrderException( String.format( "'%s' is an invalid order. It has to be surrounded by [ ] brackets.", orderString) );
    }

    private static String[] splitIntoOrderParts( String orderString ) {
        return orderString.replaceAll( "\\[", "" )
                .replaceAll( "]", "" )
                .split("," );
    }

    private static void checkForTwoComponents( String[] orderParts ) {
        if( orderParts.length < 2 )
            throw new OrderException( String.format( "The order '%s' cannot be split into two parts. " +
                    "Perhaps a comma is missing between command type an UUID.", orderParts[ 0 ] ) );
    }

    private static UUID getValidUUID( String uuidPart ) {
        try {
            return UUID.fromString( uuidPart );
        } catch ( IllegalArgumentException e ) {
            throw new OrderException( String.format( "'%s' is not a valid UUID.", uuidPart ) );
        }
    }

}
