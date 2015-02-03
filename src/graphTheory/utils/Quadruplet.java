package graphTheory.utils;

/**
 * A class implementing a 4-upplet of any classes T U V and W
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 * @param <U>
 * @param <V>
 * @param <W>
 */
public class Quadruplet<T, U, V, W> {
	public T first;
	public U second;
	public V third;
	public W fourth;

	public Quadruplet(T f, U s, V t, W fo) {
		first = f;
		second = s;
		third = t;
		fourth = fo;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('(');
		s.append(first);
		s.append(',');
		s.append(second);
		s.append(',');
		s.append(third);
		s.append(',');
		s.append(fourth);
		s.append(')');
		return s.toString();
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof Quadruplet) {
			Quadruplet<T, U, V, W> t = (Quadruplet<T, U, V, W>) o;
			return (first == null && t.first == null)
					|| (first != null && first.equals(t.first))
					&& (second == null && t.second == null)
					|| (second != null && second.equals(t.second))
					&& (third == null && t.third == null)
					|| (third != null && third.equals(t.third))
					&& (fourth == null && t.fourth == null)
					|| (fourth != null && fourth.equals(t.fourth));
		}
		return false;
	}

	public int hashCode() {
		int i1 = (first == null) ? 0 : first.hashCode();
		int i2 = (second == null) ? 0 : second.hashCode();
		int i3 = (third == null) ? 0 : third.hashCode();
		int i4 = (fourth == null) ? 0 : fourth.hashCode();

		return ((i1 ^ i2) ^ i3) ^ i4;
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			return (Quadruplet<T, U, V, W>) super.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
