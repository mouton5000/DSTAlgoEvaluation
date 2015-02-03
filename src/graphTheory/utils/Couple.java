package graphTheory.utils;

/**
 * This class implement a simple 2-upplet of any two classes T and U.
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 * @param <U>
 */
public class Couple<T, U> implements Cloneable {

	/**
	 * First element of the couple.
	 */
	public T first;

	/**
	 * Second element of the couple.
	 */
	public U second;

	/**
	 * Create a new empty couple
	 */
	public Couple() {
		first = null;
		second = null;
	}

	/**
	 * Create a new couple with {@link #first} set to f and {@link #second} set
	 * to s.
	 */
	public Couple(T f, U s) {
		first = f;
		second = s;
	}

	/**
	 * Create a new couple copy of c
	 */
	public Couple(Couple<T, U> c) {
		first = c.first;
		second = c.second;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('(');
		s.append(first);
		s.append(',');
		s.append(second);
		s.append(')');
		return s.toString();
	}

	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (o instanceof Couple) {
			return first.equals(((Couple) o).first)
					&& second.equals(((Couple) o).second);
		}
		return false;
	}

	public int hashCode() {
		int i1 = (first == null) ? 0 : first.hashCode();
		int i2 = (second == null) ? 0 : second.hashCode();
		return i1 ^ i2;
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			return (Couple<T, U>) super.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the couple where second and first are switched.
	 */
	public Couple<U, T> reverse() {
		return new Couple<U, T>(second, first);
	}

	public static void main(String[] args) {
		int[] a = { 1, 2 };
		int[] b = { 2, 3 };
		Couple<int[], int[]> c = new Couple<int[], int[]>(a, b);

		System.out.println(c);
		System.out.println(c.clone());
	}
}
