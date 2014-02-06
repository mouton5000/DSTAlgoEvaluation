package graphTheory.utils;

/**
 * Abstract class which, like {@link #Comparable()} interface, define a
 * compareTo method to compare two instances.
 * 
 * It also add two helper methods to easily now if an element is greater or less
 * than another.
 * 
 * Finally it adds two methods to have an positive infinity and negative
 * infinity, in other words two instances which are respectively greater and
 * less than any other instance.
 * 
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 */
public abstract class Comparable2<T> {

	/**
	 * Return a negative, zero ou positive value if this element is less, equal
	 * or bigger than o.
	 * 
	 * If this.equals(o) returns true, this method should return 0.
	 * 
	 * @param o
	 * @return
	 */
	public abstract int compareTo(T o);

	/**
	 * Return true if this is less than o, meaning that
	 * {@link #compareTo(Object)} return a negative value.
	 * 
	 * @param o
	 * @return
	 */
	public boolean isStrLess(T o) {
		return this.compareTo(o) < 0;
	}

	/**
	 * Return true if this is bigger than o , meaning that
	 * {@link #compareTo(Object)} return a positive value.
	 * 
	 * @param o
	 * @return
	 */
	public boolean isStrBigger(T o) {
		return this.compareTo(o) > 0;
	}

	/**
	 * @return an instance which is less than any other element, considering the
	 *         {@link #compareTo(Object) method.
	 */
	public abstract T getNegativeInfinity();

	/**
	 * @return an instance which is greater than any other element, considering
	 *         the {@link #compareTo(Object) method.
	 */
	public abstract T getPositiveInfinity();
}
