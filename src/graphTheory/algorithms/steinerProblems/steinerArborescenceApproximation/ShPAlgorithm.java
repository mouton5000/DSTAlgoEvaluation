package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.algorithms.shortestDistance.arcCost.ArcDijkstraOneSourceAlgorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.shortestPath.ArcShortestPathOneSourceInstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * This algorithm is a k-approximation for the Directed Steiner Tree problem. It
 * returns the union of all the shortest path from the root to all the
 * terminals.
 * 
 * To do so, it computes all the shortest paths from the root using the dijkstra
 * algorithm and compute the union of all the shortest path from the root to the
 * terminals.
 * 
 * @author Watel Dimitri
 * 
 */
public class ShPAlgorithm extends SteinerArborescenceApproximationAlgorithm {

	@Override
	protected void computeWithoutTime() {

		HashMap<Arc, Integer> costs = instance.getIntCosts();
		HashSet<Arc> h = new HashSet<Arc>();
		Integer v;

		// Create a shortest path instance
		ArcShortestPathOneSourceInstance aspi = new ArcShortestPathOneSourceInstance(
				instance.getGraph());
		aspi.setCosts(instance.getCosts());
		aspi.setSource(instance.getRoot());

		// Create a dijkstra algorithm
		ArcDijkstraOneSourceAlgorithm alg = new ArcDijkstraOneSourceAlgorithm();
		alg.setCheckFeasibility(false); // No need to check if there is a shortest path from the root to all nodes
		alg.setInstance(aspi);
		alg.setComputeOnlyCosts(false);
		alg.compute();

		HashMap<Integer, List<Arc>> shp = alg.getShortestPaths();

		// Merge the shortest paths from root to terminals
		Iterator<Integer> it = instance.getRequiredVerticesIterator();
		while (it.hasNext()) {
			v = it.next();
			h.addAll(shp.get(v));
		}

		// Compute the cost of optimal solution
		int c = 0;
		for (Arc a : h)
			c += instance.getIntCost(a);

		arborescence = h;
		cost = c;
	}

}
