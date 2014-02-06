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
public class DoubleBoolean extends Comparable2<DoubleBoolean> {

	/**
	 * The double of the couple
	 */
	private Double d;

	/**
	 * The boolean of the couple
	 */
	private Boolean b;

	/**
	 * @return the double of the couple
	 */
	public Double getDoubleValue() {
		return d;
	}

	/**
	 * @return the boolean of the couple
	 */
	public Boolean getBooleanValue() {
		return b;
	}

	/**
	 * Create a new couple of double and boolean with double d and boolean b.
	 * 
	 * @param d
	 * @param b
	 */
	public DoubleBoolean(Double d, Boolean b) {
		super();
		this.d = d;
		this.b = b;
	}

	/**
	 * The greatest couple of double and boolean
	 */
	public static final DoubleBoolean POSITIVE_INFINITY = new DoubleBoolean(
			Double.POSITIVE_INFINITY, true);

	/**
	 * The smallest couple of double and boolean
	 */
	public static final DoubleBoolean NEGATIVE_INFINITY = new DoubleBoolean(
			Double.NEGATIVE_INFINITY, false);

	@Override
	public int compareTo(DoubleBoolean o) {
		int i = this.d.compareTo(o.d);
		if (i != 0)
			return i;
		else {
			if (this.b == o.b)
				return 0;
			else
				return (this.b) ? 1 : -1;
		}
	}

	@Override
	public int hashCode() {
		int i1 = (d == null) ? 0 : d.hashCode();
		int i2 = (b == null) ? 0 : b.hashCode();
		return i1 ^ i2;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DoubleBoolean))
			return false;
		DoubleBoolean db = (DoubleBoolean) obj;
		return d == db.d && b == db.b;

	}

	@Override
	public DoubleBoolean getNegativeInfinity() {
		return NEGATIVE_INFINITY;
	}

	@Override
	public DoubleBoolean getPositiveInfinity() {
		return POSITIVE_INFINITY;
	}

	public String toString() {
		return "[" + d + ", " + b + "]";
	}

}
