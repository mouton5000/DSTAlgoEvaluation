package graphTheory.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class implements an iterator over more than one collection. It firstly
 * iterates every elements of the first one, then the second one, ... until it
 * has iterated the last element of the last collection. <br/>
 * It acts as every collections were concatenated and then iterated. <br/>
 * For example, a MultiIterator over the lists (1 3 5) (2 4 6) would return
 * successily 1, 3, 5, 2, 4 and 6.
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 */
public class MultiIterator<T> implements Iterator<T> {

	/**
	 * List of iterators iterating collections
	 */
	private ArrayList<Iterator<T>> its;

	/**
	 * Index of the iterator currently iterating in the list its.
	 */
	private int icol;

	/**
	 * Create a new iterator over every collection of the list collections.
	 * 
	 * @param collections
	 */
	public MultiIterator(ArrayList<Collection<T>> collections) {
		its = new ArrayList<Iterator<T>>();
		for (Collection<T> col : collections)
			its.add(col.iterator());
		icol = (collections.size() > 0) ? 0 : Integer.MIN_VALUE;
	}

	/**
	 * Return an iterator over every collection iterated by the iterators in the
	 * list iterators.
	 * 
	 * @param iterators
	 */
	@SafeVarargs
	public MultiIterator(Iterator<T>... iterators) {
		its = new ArrayList<Iterator<T>>();
		for (Iterator<T> it : iterators)
			its.add(it);
		icol = (iterators.length > 0) ? 0 : Integer.MIN_VALUE;
	}

	@Override
	public boolean hasNext() {
		if (icol == Integer.MIN_VALUE)
			return false;
		else {
			Iterator<T> it;
			if (icol == -1)
				icol++;
			while (icol < its.size()) {
				it = its.get(icol);
				if (it.hasNext())
					return true;
				icol++;
			}
			return false;
		}
	}

	@Override
	public T next() {
		if (icol == Integer.MIN_VALUE)
			return null;
		else {
			Iterator<T> it;
			while (icol < its.size()) {
				it = its.get(icol);
				if (it.hasNext())
					return it.next();
				icol++;
			}
			return null;
		}
	}

	@Override
	public void remove() {

	}

}
