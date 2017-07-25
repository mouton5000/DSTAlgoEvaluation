package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.algorithms.shortestDistance.arcCost.RoyWarshallFloydAlgorithm;
import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.shortestPath.ArcShortestPathsInstance;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.utils.Couple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * Implementation of the G_F_Triangle algorithm.
 * <p>
 * This algorithm returns a feasible solution for the Directed Steiner Tree
 * problem. It is a greedy algorihm which uses a sub-algorihthm named FLAC.
 * <p>
 * FLAC is designed to returns a feasible solution for the minimum Density
 * Directed Steiner Tree problem: it searches for a tree rooted in the root of
 * the instance which spans a part of the terminal. It try to minimize the total
 * cost of the tree divided by the number of terminals.
 * <p>
 * G_F_Triangle first builds the closure metric of the graph. It then works
 * exclusively in that new graph. It uses FLAC to span some terminals, delete those terminals, and repeat FLAC
 * to span other terminals. It continues until all the terminals are reached.
 * <p>
 * To span the terminals, FLAC grows a flow from the terminals: we call them
 * sources. That flow is send through the entering arcs, with a flow rate equals
 * to 1 L/s, considering the volume of an arc is its weight. Then, when an arc
 * is full (we say saturated), the source feeds its entering arcs. If two
 * sources reach the same arc with a path of saturated arcs, the flow rate is 2
 * L/s. And if more sources reaches the arc, the flow rate increases again: the
 * flow rate inside an arc is equals to the number of sources it can reach with
 * paths of saturated arcs
 * <p>
 * FLAC continue until the root is reached, and we return the set of saturated
 * arcs linked to the root. As we want to return a tree, each time an arc is
 * saturated, if a node is connected to the same source with two paths of
 * saturated arcs, we delete that last saturated arc.
 * <p>
 * 
 * @author Watel Dimitri
 * 
 */
public class GFLACTRAlgorithm extends SteinerArborescenceApproximationAlgorithm {

	// --------------------   Directed Steiner Tree Part --------------------//


	@Override
	protected void computeWithoutTime() {

		ArcShortestPathsInstance aspi = new ArcShortestPathsInstance(instance.getGraph());
		aspi.setCosts(instance.getCosts());

		// Compute shortest paths

		RoyWarshallFloydAlgorithm rwf = new RoyWarshallFloydAlgorithm();
		rwf.setInstance(aspi);
		rwf.setCheckFeasibility(false);
		rwf.setComputeOnlyCosts(false);
		rwf.compute();

		// Generate the metric closure instance

		DirectedGraph closureGraph = new DirectedGraph();
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while(it.hasNext())
			closureGraph.addVertice(it.next());

		SteinerDirectedInstance closureInstance = new SteinerDirectedInstance(closureGraph);
		closureInstance.setRoot(instance.getRoot());
		it = instance.getRequiredVerticesIterator();
		while(it.hasNext())
			closureInstance.setRequired(it.next());

		it = closureGraph.getVerticesIterator();
		Iterator<Integer> it2;
		Integer u,v;
		while(it.hasNext()){
			u = it.next();
			it2 = closureGraph.getVerticesIterator();
			while(it2.hasNext()){
				v = it2.next();
				Integer shortestCost = rwf.getCosts().get(new Couple<Integer,Integer>(u,v));
				if (shortestCost != null){ 
					Arc a = closureGraph.addDirectedEdge(u, v);
					closureInstance.setCost(a, shortestCost);
				}
			}
		}

		// Compute GFLAC on the closure instance

		GFLACAlgorithm gflac = new GFLACAlgorithm();
		gflac.setInstance(closureInstance);
		gflac.setCheckFeasibility(false);
		gflac.compute();

		HashSet<Arc> h = gflac.getArborescence();
		HashSet<Arc> h2 = new HashSet<Arc>();
		Integer cst = 0;

		// Build the solution from the closure instance solution

		for(Arc a : h){
			Couple<Integer,Integer> c = new Couple<Integer,Integer>(a.getInput(), a.getOutput());
			List<Arc> path = rwf.getShortestPaths().get(c);
			for(Arc b : path){
				if(h2.add(b))
					cst += instance.getIntCost(b);
			}
		}

		arborescence = h2;
		cost = cst;

	}


}