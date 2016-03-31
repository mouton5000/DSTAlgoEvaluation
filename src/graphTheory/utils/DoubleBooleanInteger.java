package graphTheory.utils;

/**
 * This class associates, as a couple, a double and a boolean.
 * 
 * This couple extends the {@link Comparable2} abstract class, as any two
 * couples can be compared. The doubles are firstly compared. If equals, the
 * booleans are compared. True is bigger than False.
 * 
 * (Positive_infinity, true) is greater than any DoubleBoolean.
 * (Negative_infinity, false) is less than any DoubleBoolean.
 * 
 * @author Watel Dimitri
 * 
 */
public class DoubleBooleanInteger extends Comparable2<DoubleBooleanInteger> {

	/**
	 * The double of the triplet
	 */
	private Double d;

	/**
	 * The boolean of the triplet
	 */
	private Boolean b;

    /**
     * The integer of the triplet
     */

    private Integer i;

	/**
	 * @return the double of the triplet
	 */
	public Double getDoubleValue() {
		return d;
	}

	/**
	 * @return the boolean of the triplet
	 */
	public Boolean getBooleanValue() {
		return b;
	}

    /**
     * @return the integer of the triplet
     */
    public Integer getIntegerValue() {
        return i;
    }


    /**
	 * Create a new triplet of double, boolean and integer with double d, boolean b and integer i.
	 *
	 * @param d
	 * @param b
     * @param i
	 */
	public DoubleBooleanInteger(Double d, Boolean b, Integer i) {
		super();
		this.d = d;
		this.b = b;
        this.i = i;
	}

	/**
	 * The greatest triplet of double, boolean and integer
	 */
	public static final DoubleBooleanInteger POSITIVE_INFINITY = new DoubleBooleanInteger(
			Double.POSITIVE_INFINITY, true, Integer.MAX_VALUE);

	/**
	 * The smallest triplet of double, boolean and integer
	 */
	public static final DoubleBooleanInteger NEGATIVE_INFINITY = new DoubleBooleanInteger(
			Double.NEGATIVE_INFINITY, false, Integer.MIN_VALUE);

	@Override
	public int compareTo(DoubleBooleanInteger o) {
		int j  = this.d.compareTo(o.d);
		if (j != 0)
			return j;
		else {
			if (this.b == o.b)
				return this.i.compareTo(o.i);
			else
				return (this.b) ? 1 : -1;
		}
	}

	@Override
	public int hashCode() {
		int i1 = (d == null) ? 0 : d.hashCode();
		int i2 = (b == null) ? 0 : b.hashCode();
        int i3 = (i == null) ? 0 : i.hashCode();
		return i1 ^ (i2+i3);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DoubleBooleanInteger))
			return false;
		DoubleBooleanInteger dbi = (DoubleBooleanInteger) obj;
		return d.equals(dbi.d) && b == dbi.b && i.equals(dbi.i);
	}

	@Override
	public DoubleBooleanInteger getNegativeInfinity() {
		return NEGATIVE_INFINITY;
	}

	@Override
	public DoubleBooleanInteger getPositiveInfinity() {
		return POSITIVE_INFINITY;
	}

	public String toString() {
		return "[" + d + ", " + b + ", " + i +"]";
	}

}
