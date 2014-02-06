package graphTheory.instances.steiner.classic;

import graphTheory.graph.UndirectedGraph;

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

// TODO Relire
// TODO Refactor
// TODO Commenter
