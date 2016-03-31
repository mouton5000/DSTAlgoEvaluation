package graphTheory.utils;

/**
 * A class implementing a 3-upplet of any classes T U and V
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 * @param <U>
 * @param <V>
 */
public class Triplet<T, U, V> {
	public T first;
	public U second;
	public V third;

	public Triplet(){
	}
	
	public Triplet(T f, U s, V t) {
		first = f;
		second = s;
		third = t;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('(');
		s.append(first);
		s.append(',');
		s.append(second);
		s.append(',');
		s.append(third);
		s.append(')');
		return s.toString();
	}

	public int hashCode() {
		int i1 = (first == null) ? 0 : first.hashCode();
		int i2 = (second == null) ? 0 : second.hashCode();
		int i3 = (third == null) ? 0 : third.hashCode();

		return (i1 ^ i2) ^ i3;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof Triplet) {
			Triplet<T, U, V> t = (Triplet<T, U, V>) o;
			return (first == null && t.first == null)
					|| (first != null && first.equals(t.first))
					&& (second == null && t.second == null)
					|| (second != null && second.equals(t.second))
					&& (third == null && t.third == null)
					|| (third != null && third.equals(t.third));
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			return (Triplet<T, U, V>) super.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}