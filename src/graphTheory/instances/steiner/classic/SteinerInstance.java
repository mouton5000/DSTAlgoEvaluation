package graphTheory.instances.steiner.classic;

import graphTheory.graph.Arc;
import graphTheory.graph.Graph;
import graphTheory.instances.ArcCostGraphInstance;
import graphTheory.utils.Collections2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Instance merging the common elements of the Directed or Undirected Steiner Tree problem:
 * the costs and the terminals (or required vertices). 
 * 
 * @author Watel Dimitri
 * 
 */
public abstract class SteinerInstance extends ArcCostGraphInstance implements
		Cloneable {

	protected ArrayList<Integer> requiredVertices = new ArrayList<Integer>();

	public SteinerInstance(Graph g) {
		super(g);
	}

	public Graph getGraph() {
		return graph;
	}

	public ListIterator<Integer> getRequiredVerticesIterator() {
		return requiredVertices.listIterator();
	}

	public ListIterator<Integer> getRequiredVerticesIterator(int index) {
		return requiredVertices.listIterator(index);
	}

	public ArrayList<Integer> getRequiredVertices() {
		return new ArrayList<Integer>(requiredVertices);
	}

	public Integer getRequiredVertice(int i) {
		return requiredVertices.get(i);
	}

	public int getNumberOfRequiredVertices() {
		return requiredVertices.size();
	}

	public boolean isRequired(Integer n) {
		return requiredVertices.contains(n);
	}

	public void setRequired(Integer n) {
		setRequired(n, true);
	}

	public void setRequiredNodes(Integer... ns) {
		for (Integer n : ns)
			setRequired(n);
	}

	public void setRequired(Integer n, boolean isRequired) {
		if (isRequired) {
			if (!requiredVertices.contains(n)) {
				graph.setColor(n, Color.black);
				graph.setFill(n, true);
				graph.setTextColor(n, Color.white);
				requiredVertices.add(n);
			}
		} else {
			graph.setColor(n, Color.black);
			graph.setFill(n, false);
			graph.setTextColor(n, Color.black);
			requiredVertices.remove(n);
		}
	}

	public Integer getRandomRequiredVertice() {
		return Collections2.randomElement(requiredVertices);
	}

	public void sortRequiredVertices(Comparator<Integer> comp) {
		Collections.sort(requiredVertices, comp);
	}

	public int maxCost() {
		Iterator<Arc> it = graph.getEdgesIterator();
		int c = Integer.MIN_VALUE, d;
		while (it.hasNext()) {
			d = this.getIntCost(it.next());
			if (c < d) {
				c = d;
			}
		}
		return c;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Noeuds\n\n");
		for (Integer n : graph.getVertices()) {
			s.append(n);
			if (isRequired(n))
				s.append(" ").append("x");
			s.append("\n");
		}

		s.append("\nArcs\n\n");
		for (Arc a : graph.getEdges()) {
			s.append(a).append(" ").append(getIntCost(a)).append("\n");
		}

		return s.toString();
	}

}