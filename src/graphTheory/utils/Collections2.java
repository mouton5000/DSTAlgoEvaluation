package graphTheory.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A list of helper method for Collections, different than the Collection java
 * class methods
 * 
 * @author Watel Dimitri
 * 
 */
public class Collections2 {

	/**
	 * Source : http://eyalsch.wordpress.com/2010/04/01/random-sample
	 * 
	 * @param col
	 * @param size
	 * @return a random subset of the set col which size is size.
	 */
	public static <T> Set<T> randomSubset(Set<T> col, int size) {
		if (size == 0) {
			return new HashSet<T>();
		}
		int s = col.size();
		int dif = s - size;
		if (dif == 0) {
			return new HashSet<T>(col);
		}
		if (dif >= 0 && dif < s) {
			HashSet<T> res = new HashSet<T>();
			int visited = 0;
			Iterator<T> it = col.iterator();
			T item;

			HighQualityRandom rand = new HighQualityRandom();

			while (size > 0) {
				item = it.next();
				if (rand.nextDouble() < ((double) size) / (s - visited)) {
					res.add(item);
					size--;
				}
			}
			return res;
		}
		return null;
	}

	/**
	 * Source : http://eyalsch.wordpress.com/2010/04/01/random-sample
	 * 
	 * @param col
	 * @param size
	 * @return a random subset of the list col which size is size.
	 */
	public static <T> Set<T> randomSubset(List<T> col, int size) {
		if (size == 0) {
			return new HashSet<T>();
		}
		int s = col.size();
		int dif = s - size;
		if (dif == 0) {
			return new HashSet<T>(col);
		}
		if (dif >= 0 && dif < s) {
			HashSet<T> res = new HashSet<T>();
			T item;

			HighQualityRandom rand = new HighQualityRandom();

			for (int i = dif; i < s; i++) {
				item = col.get(rand.nextInt(i + 1));
				if (!res.add(item))
					res.add(col.get(i));
			}
			return res;
		}
		return null;
	}

	/**
	 * @param col
	 * @return a random element of the list col.
	 */
	public static <T> T randomElement(List<T> col) {
		int s = col.size();
		if (s == 0)
			return null;
		int i = Math2.randomInt(s);
		return col.get(i);
	}

	/**
	 * @param k
	 * @return an array containing every integers from 0 to k-1, in that order.
	 */
	public static int[] range(int k) {
		int[] ar = new int[k];
		for (int i = 0; i < k; i++) {
			ar[i] = i;
		}
		return ar;
	}

}
