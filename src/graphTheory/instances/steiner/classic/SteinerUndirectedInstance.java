package graphTheory.instances.steiner.classic;

import graphTheory.graph.UndirectedGraph;

/**
 * 
 * Instance for the Undirected Steiner Tree problem : given a graph,
 * some nodes called terminals or required vertices,  and weight over 
 * the arcs, return the minimum cost tree spanning all the terminals.
 * 
 * @author Watel Dimitri
 *
 */
public class SteinerUndirectedInstance extends SteinerInstance {

	public SteinerUndirectedInstance(UndirectedGraph g) {
		super(g);
	}

	public UndirectedGraph getGraph() {
		return (UndirectedGraph) graph;
	}

	@Override
	public boolean hasSolution() {
		Integer u, v;
		int k = getNumberOfRequiredVertices();
		if (k == 0)
			return true;

		u = getRequiredVertice(0);
		for (int j = 1; j < k; j++) {
			v = getRequiredVertice(j);
			if (!graph.areConnectedByUndirectedEdges(u, v))
				return false;
		}
		return true;
	}

}