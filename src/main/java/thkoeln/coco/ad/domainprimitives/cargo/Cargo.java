package thkoeln.coco.ad.domainprimitives.cargo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import thkoeln.coco.ad.domainprimitives.uranium.Uranium;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Getter
@EqualsAndHashCode
public class Cargo {
    @Embedded
    private Uranium uranium;
    private static final int MAX_CAPACITY = 20;

    protected Cargo() {
        this.uranium = Uranium.fromZero();
    }

    private Cargo( Uranium initialUranium ) {
        this.uranium = initialUranium;
    }

    public static Cargo fromZero() {
        return new Cargo();
    }

    public static Cargo fromInitialUranium( Uranium uranium ) {
        if( uranium.getAvailableAmount() > Cargo.MAX_CAPACITY )
            throw new CargoException( String.format( "The amount of uranium in a cargo can't be greater than %d.",
                    Cargo.MAX_CAPACITY ) );
        return new Cargo( uranium );
    }

    public Cargo afterTaking( Uranium uranium ) {
        Uranium uraniumAfterTaking = this.getUranium().afterTaking( uranium );
        return Cargo.fromInitialUranium( uraniumAfterTaking );
    }

    public Cargo afterIncreasing( Uranium uranium ) {
        Uranium uraniumAfterIncreasing = this.getUranium().afterIncreasing( uranium );
        if( uraniumAfterIncreasing.getAvailableAmount() > MAX_CAPACITY )
            throw new CargoException( String.format( "The amount of %d uranium doesn't fit into this cargo. " +
                    "There is just space for %d uranium left.",
                    uranium.getAvailableAmount(), this.getAvailableCapacity() ) );
        return Cargo.fromInitialUranium( uraniumAfterIncreasing );
    }

    public int getAvailableCapacity() {
        return Cargo.MAX_CAPACITY - this.uranium.getAvailableAmount();
    }
}
