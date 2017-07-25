package graphTheory.algorithms.shortestDistance.arcCost;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.shortestPath.ArcShortestPathOneSourceInstance;
import graphTheory.utils.FibonacciHeap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the dijkstra algorithm. This algorithm compute the shortest
 * path between a source and all the nodes in a directed or undirected graph.
 * 
 * This version is implemented with a Fibonacci Heap.
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcDijkstraOneSourceAlgorithm extends Algorithm<ArcShortestPathOneSourceInstance> {


	/**
	 * If true, do not compute the shortest paths, but only the costs of the
	 * shortest paths
	 */
	protected boolean computeOnlyCosts;

	/**
	 * For each node v, this map contains the shortest path from the source to v
	 */
	protected HashMap<Integer, List<Arc>> shPs;

	/**
	 * For each node v, this map contains the cost of the shortest path from the
	 * source to v
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
	 * Map linking every node to its distance from the source
	 */
	private HashMap<Integer, Integer> distanceFromSource;

	/**
	 * Map linking every node arc preceding this node in one lowest cost path.
	 */
	private HashMap<Integer, Arc> preceding;

	/**
	 * Fibonacci Heap linking every node to its distance from the source.
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

		costs = distanceFromSource;

		if (!computeOnlyCosts)
			shPs = computePaths();
	}

	/**
	 * Init the parameters.
	 */
	private void init() {

		distanceFromSource = new HashMap<Integer, Integer>();
		preceding = new HashMap<Integer, Arc>();

		fibTree = new FibonacciHeap<Integer>();
		nodes = new HashMap<Integer, FibonacciHeap<Integer>.FibonacciHeapNode<Integer>>();

		initDistances();
	}

	/**
	 * Init the distance from the source to every node : the source is at
	 * distance 0 from the source and every other node is at infinite distance
	 * until they are reached.
	 */
	private void initDistances() {
		FibonacciHeap<Integer>.FibonacciHeapNode<Integer> fhn;
		Integer n;
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext()) {
			n = it.next();
			double d;
			if (n.equals(instance.getSource())) {
				distanceFromSource.put(n, 0);
				d = 0.0;
			} else {
				distanceFromSource.put(n, null);
				d = Double.POSITIVE_INFINITY;
			}

			if (!computeOnlyCosts)
				preceding.put(n, null);
			fhn = fibTree.insert(n, d);
			nodes.put(n, fhn);
		}
	}

	/**
	 * Read the output arc (directed or not) (n,v) of n and see if this arc can
	 * reduce the distance from the source to v.
	 * 
	 * @param n
	 */
	private void expandFrom(Integer n) {
		Integer distanceFromSource = this.distanceFromSource.get(n);
		if (distanceFromSource == null) // Infinite distance from the source.
			return;

		Integer output;
		Arc a;
		Iterator<Arc> it = instance.getGraph().getOutputArcsIterator(n);
		while (it.hasNext()) {
			a = it.next();
			output = a.getOutput();
			expand(distanceFromSource, a, output);
		}

		it = instance.getGraph().getUndirectedNeighbourEdgesIterator(n);
		while (it.hasNext()) {
			a = it.next();
			output = instance.getGraph().getNeighbourNode(n, a);
			expand(distanceFromSource, a, output);
		}
	}

	/**
	 * See if using a path of weight distanceFromSource, and the arc a, we can
	 * reduce the distance from the source to the node output.
	 * 
	 * @param distanceFromSource
	 * @param a
	 * @param output
	 */
	private void expand(Integer distanceFromSource, Arc a, Integer output) {
		Integer dist = distanceFromSource + instance.getIntCost(a);
		Integer nndN = this.distanceFromSource.get(output);
		if ((nndN == null || nndN > dist)) {
			FibonacciHeap<Integer>.FibonacciHeapNode<Integer> fhn = nodes
					.get(output);
			fibTree.decreaseKey(fhn, dist);
			this.distanceFromSource.put(output, dist);
			if (!computeOnlyCosts)
				preceding.put(output, a);
		}
	}

	/**
	 * Using the map preceding compute and return the lowest cost path from
	 * source to all nodes
	 * 
	 * @return the lowest cost path from source to all nodes
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
	 * Using the map preceding compute and return the lowest cost path from
	 * source to v
	 * 
	 * @return the lowest cost path from source to v
	 */
	private List<Arc> computePaths(Integer v, HashMap<Integer, List<Arc>> paths) {
		List<Arc> l = paths.get(v);
		if (l != null)
			return l;

		if (v.equals(instance.getSource())) {
			l = new LinkedList<Arc>();
		} else {
			Arc a = preceding.get(v);
			if (a == null)
				l = null;
			else {
				l = new LinkedList<Arc>(computePaths(a.getInput(), paths));
				l.add(a);
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
