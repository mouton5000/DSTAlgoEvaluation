package graphTheory.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package graph:
// Parametable, Graph, Node

/**
 * This class is a model for an edge in an undirected graph or
 * an edge in a directed graph
 * 
 * @author Watel Dimitri
 */
public class Arc implements Cloneable {

	private Integer input;
	private Integer output;
	private boolean isDirected;

	public Arc(Integer input, Integer output, boolean isDirected) {
		this.input = input;
		this.output = output;
		this.isDirected = isDirected;
	}

	public Integer getInput() {
		return input;
	}

	public Integer getOutput() {
		return output;
	}

	public boolean isDirected() {
		return isDirected;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Arc) {
			Arc a = (Arc) o;

			return isDirected == a.isDirected
					&& ((((input == null && a.input == null) || (input != null && input
							.equals(a.input))) && ((output == null && a.output == null) || (output != null && output
							.equals(a.output)))) || (!isDirected && ((input == null && a.output == null) || (input != null && input
							.equals(a.output))))
							&& ((output == null && a.input == null) || (output != null && (output
									.equals(a.input)))));

		} else {
			return false;
		}
	}

	public String toString() {
		return (new StringBuilder()).append(input).append(" ---")
				.append(isDirected ? ">" : "-").append(" ").append(output)
				.toString();
	}

	public int hashCode() {

		int i1 = input;
		int i2 = output;
		if (isDirected)
			return i1 ^ (i2 * 31);
		else
			return i1 ^ i2;
	}

	public static Arc valueOf(String s) {
		Pattern p = Pattern.compile("(\\d+) ---(>|-) (\\d+)");
		Matcher m = p.matcher(s);
		if (m.find())
			return new Arc(Integer.valueOf(m.group(1)), Integer.valueOf(m
					.group(3)), m.group(2).equals(">"));
		else
			return null;

	}

	public Object clone() {
		Arc a;
		try {
			a = (Arc) super.clone();
			a.input = input;
			a.output = output;
			return a;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
