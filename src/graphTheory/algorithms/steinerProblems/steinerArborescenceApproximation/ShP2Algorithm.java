package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.algorithms.shortestDistance.arcCost.ArcDijkstraOneSourceAlgorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.shortestPath.ArcShortestPathOneSourceInstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 
 * This algorithm is a k-approximation for the Directed Steiner Tree problem. It
 * returns the union of all the shortest path from the root to all the
 * terminals.
 * 
 * To do so, it computes all the shortest path from the root using the dijkstra
 * algorithm and and keep the shortest one reaching a terminal, then set the weight 
 * of the arcs of this path to 0 and restart until all the terminals are covered.
 * 
 * @author Watel Dimitri
 * 
 */
public class ShP2Algorithm extends SteinerArborescenceApproximationAlgorithm {


	
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
		alg.setComputeOnlyCosts(false);
		alg.setInstance(aspi);
		
		HashSet<Integer> toCover = new HashSet<Integer>(instance.getRequiredVertices());
		do{

			alg.compute();
			HashMap<Integer, Integer> shc = alg.getCosts();
			
			// Get the shortest path from the root to any terminal
			Integer term = getNearestRequiredVertice(shc, toCover);

			List<Arc> path = alg.getShortestPaths().get(term);
			for(Arc a : path){
				costs.put(a, 0);
				h.add(a);
			}
			toCover.remove(term);

		}while(!(toCover.isEmpty()));
		
		// Compute the cost of optimal solution
		int c = 0;
		for (Arc a : h)
			c += instance.getIntCost(a);

		arborescence = h;
		cost = c;
	}

	private Integer getNearestRequiredVertice(HashMap<Integer, Integer> shc, HashSet<Integer> toCover){
		int bestCost = Integer.MAX_VALUE;
		Integer nearestTerm = null;
		for(Integer term : toCover){
			int cost = shc.get(term);
			if(cost < bestCost){
				bestCost = cost; 
				nearestTerm = term;
			}
		}
		return nearestTerm;
	}

}
