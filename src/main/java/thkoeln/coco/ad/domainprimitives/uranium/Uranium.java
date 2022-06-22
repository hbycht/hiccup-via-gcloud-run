package thkoeln.coco.ad.domainprimitives.uranium;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@EqualsAndHashCode
public class Uranium {
    private int availableAmount;

    protected Uranium() {
        this.availableAmount = 0;
    }

    private Uranium( int startAmount ) {
        this.availableAmount = startAmount;
    }

    public static Uranium fromAmount( int amount ){
        if( amount < 0 )
            throw new UraniumException( "The uranium amount has to be >= 0." );
        return new Uranium( amount );
    }

    public static Uranium fromZero(){
        return new Uranium();
    }

    public Uranium afterTaking( Uranium uranium ) {
        int amountAfterTaking = this.availableAmount - uranium.getAvailableAmount();
        if( amountAfterTaking < 0 )
            throw new UraniumException( String.format( "Not enough uranium: " +
                    "You wanted to take %d but there is just an amount of %d.",
                    uranium.getAvailableAmount(), this.availableAmount ) );
        return Uranium.fromAmount( amountAfterTaking );
    }

    public Uranium afterIncreasing( Uranium uranium ){
        int amountAfterIncreasing = this.availableAmount + uranium.getAvailableAmount();
        return Uranium.fromAmount( amountAfterIncreasing );
    }
}
