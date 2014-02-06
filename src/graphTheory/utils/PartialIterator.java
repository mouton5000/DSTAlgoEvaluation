package graphTheory.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ReadOnly Iterator over a subset of a collection. Only the subset defined by a
 * boolean predicate is returned while iterating. If the predicate is true over
 * an element if the collection, it is iterated. Otherwise it is ignored.
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 */
public class PartialIterator<T> implements Iterator<T> {

	/**
	 * Classic iterator over the iterated collection.
	 */
	private Iterator<T> it;

	/**
	 * Predicate deciding which elements have to be iterated and which have to
	 * be ignored.
	 */
	private Foncteur<T, Boolean> isIterable;

	/**
	 * Next element to be returned.
	 */
	private T next;

	/**
	 * Create a new PartialIterator over the collection col, using isIterable
	 * predicate to decide which elements have to be iterated and which have to
	 * be ignored.
	 * 
	 * @param col
	 * @param isIterable
	 */
	public PartialIterator(Collection<T> col, Foncteur<T, Boolean> isIterable) {
		this.isIterable = isIterable;
		it = col.iterator();
		next = null;
		while (it.hasNext()) {
			T m = it.next();
			if (isIterable.apply(m)) {
				next = m;
				break;
			}
		}
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public T next() {
		if (next == null)
			throw new NoSuchElementException();

		T n = next;
		next = null;
		while (it.hasNext()) {
			T m = it.next();
			if (isIterable.apply(m)) {
				next = m;
				break;
			}
		}
		return n;
	}

	@Override
	public void remove() {
	}

}
