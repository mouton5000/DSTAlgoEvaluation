package graphTheory.utils;

/**
 * A class implementing any function associating any instance of class T to an
 * instance of class U.
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 * @param <U>
 */
public interface Foncteur<T, U> {

	/**
	 * Apply this function to o and return the result.
	 * 
	 * @param o
	 * @return this function applied to o.
	 */
	public U apply(T o);
}
