package graphTheory.instances.shortestPath;

import graphTheory.graph.Graph;
import graphTheory.instances.ArcCostGraphInstance;

/**
*  Instance of the shortest paths problem : given a graph
 * and weight over the arcs, return the shortest path from 
 * u to v for each couple of nodes (u,v).
 
 * @author mouton
 *
 */
public class ArcShortestPathsInstance extends ArcCostGraphInstance{


	public ArcShortestPathsInstance(Graph g) {
		super(g);
	}
	
	@Override
	public boolean hasSolution() {
		return true;
	}
}