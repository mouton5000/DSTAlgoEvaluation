package graphTheory.algorithms.shortestDistance.arcCost;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.shortestPath.ArcShortestPathOneDestinationInstance;
import graphTheory.utils.FibonacciHeap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the dijkstra algorithm. This algorithm compute the shortest
 * path between all the nodes to a destination in a directed or undirected
 * graph.
 * 
 * This version is implemented with a Fibonacci Heap.
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcDijkstraOneDestinationAlgorithm extends Algorithm<ArcShortestPathOneDestinationInstance> {

	/**
	 * If true, do not compute the shortest paths, but only the costs of the
	 * shortest paths
	 */
	protected boolean computeOnlyCosts;

	/**
	 * For each node v, this map contains the shortest path from v to the
	 * destination
	 */
	protected HashMap<Integer, List<Arc>> shPs;

	/**
	 * For each node v, this map contains the cost of the shortest path from v
	 * to the destination
	 */
	protected HashMap<Integer, Integer> costs;

	public HashMap<Integer, List<Arc>> getShortestPaths() {
		return shPs;
	}

	public HashMap<Integer, Integer> getCosts() {
		return costs;
	}

	public void setComputeOnlyCosts(boolean computeOnlyCosts) {
		this.computeOnlyCosts = computeOnlyCosts;
	}

	/**
	 * Map linking every node to its distance to destination
	 */
	private HashMap<Integer, Integer> distanceToDestination;

	/**
	 * Map linking every node arc following this node in one lowest cost path.
	 */
	private HashMap<Integer, Arc> following;

	/**
	 * Fibonacci Heap linking every node to its distance to destination.
	 */
	private FibonacciHeap<Integer> fibTree;

	/**
	 * Map linking every node to its fibonacciHeapNode in the heap.
	 */
	private HashMap<Integer, FibonacciHeap<Integer>.FibonacciHeapNode<Integer>> nodes;

	@Override
	protected void computeWithoutTime() {

		init();

		Integer n;
		while (!fibTree.isEmpty()) {
			n = fibTree.removeMin().getData();
			expandFrom(n);
		}

		costs = distanceToDestination;

		if (!computeOnlyCosts)
			shPs = computePaths();
	}

	/**
	 * Init the parameters.
	 */
	private void init() {

		distanceToDestination = new HashMap<Integer, Integer>();
		following = new HashMap<Integer, Arc>();

		fibTree = new FibonacciHeap<Integer>();
		nodes = new HashMap<Integer, FibonacciHeap<Integer>.FibonacciHeapNode<Integer>>();

		initDistances();
	}

	/**
	 * Init the distance from every node to the destination: the destination is
	 * at distance 0 from the destination and every other node is at infinite
	 * distance until they are reached.
	 */
	private void initDistances() {
		FibonacciHeap<Integer>.FibonacciHeapNode<Integer> fhn;
		Integer n;
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext()) {
			n = it.next();
			double d;
			if (n.equals(instance.getDestination())) {
				distanceToDestination.put(n, 0);
				d = 0.0;
			} else {
				distanceToDestination.put(n, null);
				d = Double.POSITIVE_INFINITY;
			}

			if (!computeOnlyCosts)
				following.put(n, null);
			fhn = fibTree.insert(n, d);
			nodes.put(n, fhn);
		}
	}

	/**
	 * Read the output arc (directed or not) (n,v) of n and see if this arc can
	 * reduce the distance from v to the destination.
	 * 
	 * @param n
	 */
	private void expandFrom(Integer n) {
		Integer distanceToDestination = this.distanceToDestination.get(n);
		if (distanceToDestination == null) // Infinite distance from the source.
			return;

		Integer input;
		Arc a;

		Iterator<Arc> it = instance.getGraph().getInputArcsIterator(n);
		while (it.hasNext()) {
			a = it.next();

			input = a.getInput();
			expand(distanceToDestination, a, input);
		}

		it = instance.getGraph().getUndirectedNeighbourEdgesIterator(n);
		while (it.hasNext()) {
			a = it.next();
			input = instance.getGraph().getNeighbourNode(n, a);
			expand(distanceToDestination, a, input);
		}
	}

	/**
	 * See if using a path of weight distanceToDestination, and the arc a, we
	 * can reduce the distance from the node input to the destination.
	 * 
	 * @param distanceToDestination
	 * @param a
	 * @param input
	 */
	private void expand(Integer distanceToDestination, Arc a, Integer input) {
		Integer dist = distanceToDestination + instance.getIntCost(a);
		Integer nndN = this.distanceToDestination.get(input);
		if ((nndN == null || nndN > dist)) {
			FibonacciHeap<Integer>.FibonacciHeapNode<Integer> fhn = nodes
					.get(input);
			fibTree.decreaseKey(fhn, dist);
			this.distanceToDestination.put(input, dist);
			if (!computeOnlyCosts)
				following.put(input, a);
		}
	}

	/**
	 * Using the map preceding compute and return the lowest cost path from all
	 * nodes to the destination
	 * 
	 * @return the lowest cost path from all nodes to the destination
	 */
	private HashMap<Integer, List<Arc>> computePaths() {
		HashMap<Integer, List<Arc>> paths = new HashMap<Integer, List<Arc>>();
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext()) {
			Integer v = it.next();
			computePaths(v, paths);
		}
		return paths;
	}

	/**
	 * Using the map preceding compute and return the lowest cost path from v to
	 * the destination
	 * 
	 * @return the lowest cost path from v to the destination
	 */
	private List<Arc> computePaths(Integer v, HashMap<Integer, List<Arc>> paths) {
		List<Arc> l = paths.get(v);
		if (l != null)
			return l;

		if (v.equals(instance.getDestination())) {
			l = new LinkedList<Arc>();
		} else {
			Arc a = following.get(v);
			if (a == null)
				l = null;
			else {
				l = new LinkedList<Arc>(computePaths(a.getOutput(), paths));
				((LinkedList<Arc>) l).addFirst(a);
			}
		}
		paths.put(v, l);
		return l;
	}

	@Override
	protected void setNoSolution() {
		shPs = null;
		costs = null;
	}

}
